package com.example.employeeexample.ui.detail

import android.app.Application
import androidx.lifecycle.*
import com.example.employeeexample.data.Employee
import com.example.employeeexample.data.detail.EmployeeDetailRepository
import kotlinx.coroutines.launch

class EmployeeDetailViewModel(application: Application): AndroidViewModel(application){
    //Creating Object of EmployeeDetailRepository
    private val repo: EmployeeDetailRepository =
        EmployeeDetailRepository(application)

    private val _employeeId = MutableLiveData<Long>(0)
    val employeeId: LiveData<Long>
        get() = _employeeId

    val employee: LiveData<Employee> = Transformations
        .switchMap(_employeeId) { id ->
            repo.getEmployee(id)
        }


    fun setEmployeeId(id: Long){
        if(_employeeId.value != id ) {
            _employeeId.value = id
        }
    }

    fun saveEmployee(employee: Employee){
        //viewModelScope use Dispatchers.Main (which means it run on Main thread) but we don't
        // get error from Room. This is because functions in Room marked as "suspend" are main-safe,
        // which means that they can be called from Main thread
        viewModelScope.launch {
            if (_employeeId.value == 0L) {//If its a new Employe then we call insert function
                _employeeId.value = repo.insertEmployee(employee)
            } else {// Or else we call update function
                repo.updateEmployee(employee)
            }
        }
    }

    //Calling Repository Delete function to Delete Employee
    fun deleteEmployee(){
        viewModelScope.launch {
            employee.value?.let { repo.deleteEmployee(it) }
        }
    }
}