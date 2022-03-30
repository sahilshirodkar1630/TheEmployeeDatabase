package com.example.employeeexample.data.detail

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.employeeexample.data.Employee


@Dao
interface EmployeeDetailDao{

    // Get employee by its specific id
    @Query("SELECT * FROM employee WHERE `id` = :id")
    fun getEmployee(id: Long): LiveData<Employee>

    //Insert a Emloyee in Database table
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEmployee(employee: Employee): Long

    //Update the Employee Details in the table
    @Update
    suspend fun updateEmployee(employee: Employee)

    //Delete a Employee from table
    @Delete
    suspend fun deleteEmployee(employee: Employee)

}