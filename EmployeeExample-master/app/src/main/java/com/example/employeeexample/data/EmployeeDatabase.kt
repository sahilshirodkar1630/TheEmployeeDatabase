package com.example.employeeexample.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.employeeexample.data.detail.EmployeeDetailDao
import com.example.employeeexample.data.list.EmployeeListDao

@Database(entities = [Employee::class], version = 2)
abstract class EmployeeDatabase : RoomDatabase() {

    abstract fun employeeListDao(): EmployeeListDao
    abstract fun employeeDetailDao(): EmployeeDetailDao

    companion object {
        @Volatile
        private var instance: EmployeeDatabase? = null

        fun getDatabase(context: Context) = instance
            ?: synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                EmployeeDatabase::class.java,
                "employee_database"
            ).fallbackToDestructiveMigration().build().also { instance = it }
        }
    }
}