package com.example.carsqllite

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide // Add Glide dependency for image loading

class MainActivity : AppCompatActivity() {
    private var selectedCarId: Int = -1
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private val carsList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Adjust system padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI components
        val edName = findViewById<EditText>(R.id.editTextName)
        val edPrice = findViewById<EditText>(R.id.editTextPrice)
        val edImage = findViewById<EditText>(R.id.editTextImage)
        val isFullOption = findViewById<Switch>(R.id.isFulloption)
        val buttonAddCar = findViewById<Button>(R.id.buttonAddCar)
        val buttonDeleteCar = findViewById<Button>(R.id.buttonDeleteCar)
        val listView = findViewById<ListView>(R.id.list)

        // Display selected car details
        val nameView = findViewById<TextView>(R.id.name)
        val priceView = findViewById<TextView>(R.id.price)
        val imageView = findViewById<ImageView>(R.id.imageview)
        val fullOptionView = findViewById<TextView>(R.id.fulloption)

        // Set up the adapter
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, carsList)
        listView.adapter = arrayAdapter

        // Fetch initial data
        fetchData()

        // Add or update car
        buttonAddCar.setOnClickListener {
            val name = edName.text.toString().trim()
            val priceText = edPrice.text.toString().trim()
            val image = edImage.text.toString().trim()
            val fullOption = isFullOption.isChecked

            if (name.isNotBlank() && priceText.isNotBlank() && image.isNotBlank()) {
                val price = priceText.toDoubleOrNull()
                if (price != null) {
                    val carDao = CarsDataBase.getDataBase(applicationContext).carsDao()
                    if (selectedCarId == -1) {
                        val car = Cars(name = name, price = price, image = image, isfulloption = fullOption)
                        carDao.insertCar(car)
                        Toast.makeText(this, "Car added successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        carDao.updateCar(selectedCarId, name, price, image, fullOption)
                        Toast.makeText(this, "Car updated successfully", Toast.LENGTH_SHORT).show()
                        selectedCarId = -1
                    }

                    // Clear fields and refresh data
                    edName.text.clear()
                    edPrice.text.clear()
                    edImage.text.clear()
                    isFullOption.isChecked = false
                    fetchData()
                } else {
                    edPrice.error = "Invalid price"
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Delete car
        buttonDeleteCar.setOnClickListener {
            if (selectedCarId != -1) {
                CarsDataBase.getDataBase(applicationContext).carsDao().deleteCar(selectedCarId)
                Toast.makeText(this, "Car deleted successfully", Toast.LENGTH_SHORT).show()

                // Clear fields and refresh data
                edName.text.clear()
                edPrice.text.clear()
                edImage.text.clear()
                isFullOption.isChecked = false
                selectedCarId = -1
                fetchData()
            } else {
                Toast.makeText(this, "Please select a car to delete", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle list item click
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedCar = CarsDataBase.getDataBase(applicationContext).carsDao().getAllCars()[position]
            edName.setText(selectedCar.name)
            edPrice.setText(selectedCar.price.toString())
            edImage.setText(selectedCar.image)
            isFullOption.isChecked = selectedCar.isfulloption

            selectedCarId = selectedCar.id

            // Display car details
            nameView.text = selectedCar.name
            priceView.text = "$${selectedCar.price}"
            fullOptionView.text = if (selectedCar.isfulloption) "Full Option: Yes" else "Full Option: No"
            Glide.with(this).load(selectedCar.image).into(imageView)
        }
    }

    private fun fetchData() {
        val carDao = CarsDataBase.getDataBase(applicationContext).carsDao()
        val cars = carDao.getAllCars()
        carsList.clear()

        cars.forEach { car ->
            carsList.add("${car.name} - $${car.price}")
        }
        arrayAdapter.notifyDataSetChanged()
    }
}
