package com.example.employeeexample.data.show

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.employeeexample.data.Employee
import com.example.employeeexample.data.EmployeeDatabase
import com.example.employeeexample.data.detail.EmployeeDetailDao
import com.example.employeeexample.data.list.EmployeeListDao

class EmployeeShowRepository(context: Application) {

    //Creating Instaance of EmployeeListDao
    private val employeeShowDao: EmployeeDetailDao =
        EmployeeDatabase.getDatabase(context).employeeDetailDao()

    fun getEmployee(id: Long): LiveData<Employee> {
        return employeeShowDao.getEmployee(id)
    }

}