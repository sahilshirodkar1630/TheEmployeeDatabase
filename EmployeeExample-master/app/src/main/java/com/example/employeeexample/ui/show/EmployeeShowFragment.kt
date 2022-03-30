package com.example.employeeexample.ui.show

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.employeeexample.R
import com.example.employeeexample.data.Employee
import com.example.employeeexample.data.Gender
import com.example.employeeexample.data.Role
import com.example.employeeexample.ui.list.EmployeeListViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_employee_detail.*
import kotlinx.android.synthetic.main.fragment_employee_show.*


class EmployeeShowFragment : Fragment() {

    private lateinit var viewModel: EmployeeShowViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Initializing viewModel
        viewModel = ViewModelProviders.of(this)
            .get(EmployeeShowViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employee_show, container, false)


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val navController = NavHostFragment.findNavController(nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar_show
            .setupWithNavController(navController, appBarConfiguration)
        employee_photo_show.setImageResource(R.drawable.blank_photo)
        employee_photo_show.tag = ""

        val id = EmployeeShowFragmentArgs.fromBundle(
            requireArguments()
        ).id
        viewModel.setEmployeeId(id)

        viewModel.employee.observe(viewLifecycleOwner, Observer {
            it.let { setData(it) }
        })

    }


    //Updating the EmployeeDetails
    private fun setData(employee: Employee){

        with(employee.photo ){
            if (isNotEmpty()){
                employee_photo_show.setImageURI(Uri.parse(this))
                employee_photo_show.tag = this
            } else{
                employee_photo_show.setImageResource(R.drawable.blank_photo)
                employee_photo_show.tag = ""
            }
        }

        collapsing_toolbar.title = employee.name


        run loop@{
            Role.values().forEach {
                if(it.ordinal == employee.role){
                    employee_role_show.text = it.name
                    return@loop
                }
            }
        }

        employee_age_show.text = "$employee_age years"
        when (employee.gender) {
            Gender.Male.ordinal -> {
                gender_male.isChecked = true
            }
            Gender.Female.ordinal -> {
                gender_female.isChecked = true
            }
            else -> {
                gender_other.isChecked = true
            }
        }

        employee_responsibilities_show.setText(employee.responsibility)
        employee_experience_show.setText(employee.experience)
        employee_education_show.setText(employee.education)
        if(employee.phone>0){
            employee_phone_show.setText(employee.phone.toString())
        }
        employee_email_show.setText(employee.email)
        employee_address_show.setText(employee.address)


    }

}