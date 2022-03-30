package com.example.employeeexample.data.list

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.employeeexample.data.Employee
import com.example.employeeexample.data.EmployeeDatabase


class EmployeeListRepository(context: Application){
    //Creating Instaance of EmployeeListDao
    private val employeeListDao: EmployeeListDao =
        EmployeeDatabase.getDatabase(context).employeeListDao()

    //Getting the latest list of Employees by calling dao function
    fun getEmployees(): LiveData<List<Employee>> =
          employeeListDao.getEmployees()




    suspend fun insertEmployees(employees:List<Employee>){
        return employeeListDao.insertEmployees(employees)
    }
    suspend fun getEmployeeList():List<Employee>{
        return employeeListDao.getEmployeeList()
    }


}