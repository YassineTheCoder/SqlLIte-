package com.example.carsqllite

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Entity(tableName = "cars")
data class Cars (
    @PrimaryKey(autoGenerate = true)val id:Int=0,
    val name: String,
    val price: Double,
    val image: String,
    val isfulloption: Boolean
)

@Dao

interface CarsDao{
    @Insert
    fun insertCar(car:Cars)

    @Query("SELECT * FROM cars")
    fun getAllCars():List<Cars>

    @Query("DELETE FROM cars WHERE id = :carId")
    fun deleteCar(carId: Int)

    @Query("UPDATE cars SET name = :name, price = :price, image = :image, isfulloption = :isfulloption WHERE id = :id")
    fun updateCar(id: Int, name: String, price: Double, image: String, isfulloption: Boolean)
}

@Database(entities = [Cars::class], version = 1, exportSchema = false)
abstract class CarsDataBase: RoomDatabase(){
    abstract  fun carsDao():CarsDao
    companion object{
        private var INSTANCE: CarsDataBase?=null
        fun getDataBase(context: Context):CarsDataBase{
            return  INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CarsDataBase::class.java,
                    "cars_database"
                ).allowMainThreadQueries()
                    .build()
                INSTANCE =instance
                instance
            }
        }
    }


}