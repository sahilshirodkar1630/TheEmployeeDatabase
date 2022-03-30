package com.example.employeeexample.ui.detail


import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.example.employeeexample.BuildConfig
import com.example.employeeexample.R
import com.example.employeeexample.data.Employee
import com.example.employeeexample.data.Gender
import com.example.employeeexample.data.Role
import com.example.employeeexample.ui.EmployeeDetailFragmentArgs
import com.example.employeeexample.ui.createFile
import com.example.employeeexample.ui.showToast
import com.google.android.material.snackbar.Snackbar


import kotlinx.android.synthetic.main.fragment_employee_detail.*
import kotlinx.android.synthetic.main.fragment_employee_detail.employee_photo
import kotlinx.android.synthetic.main.fragment_employee_list.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


const val PERMISSION_REQUEST_CAMERA = 0
const val CAMERA_PHOTO_REQUEST = 1
const val GALLERY_PHOTO_REQUEST = 2
const val  LATEST_EMPLOYEE_NAME_KEY = "key"
class EmployeeDetailFragment : Fragment() {

    private lateinit var viewModel: EmployeeDetailViewModel
    private var selectedPhotoPath:String = ""

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        //To show menus
//        setHasOptionsMenu(true)

        viewModel = ViewModelProviders.of(this)
            .get(EmployeeDetailViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employee_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //To add menu options using Toolbar
        toolbar_detail.inflateMenu(R.menu.detail_menu)
        toolbar_detail.setOnMenuItemClickListener {
            handleMenuItem(it)
        }

//        To get back to previous screen using action bar
//        val navController = findNavController()
//        val appBarConfiguration = AppBarConfiguration(navController.graph)
//        toolbar_detail.setupWithNavController(navController,appBarConfiguration)

        val roles = mutableListOf<String>()
        Role.values().forEach { roles.add(it.name)}

        val arrayAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, roles)
        employee_role.adapter = arrayAdapter

        val ages = mutableListOf<Int>()
        for(i in 18 until 81){ ages.add(i) }
        employee_age.adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, ages)


        //Getting id of Selected Item from EmployeeListFragment
        val id = EmployeeDetailFragmentArgs.fromBundle(requireArguments()).id
        viewModel.setEmployeeId(id)

        //Observing the EmployeeDetails and Updating the Employee Details
        viewModel.employee.observe(viewLifecycleOwner, Observer {
            it?.let{ setData(it) }
        })

        //To save employe Details
        save_employee.setOnClickListener {
            saveEmployee()
        }

        //To delete employee from db
        delete_employee.setOnClickListener {
            deleteEmployee()
        }

        //To make image bllank
        employee_photo.setOnClickListener {
            employee_photo.setImageResource(R.drawable.blank_photo)
            employee_photo.tag = ""
        }

        //To open Camera to click and change photo
        photo_from_camera.setOnClickListener {
            clickPhotoAfterPermission(it)
        }

        //To open gallery to choose photo
        photo_from_gallery.setOnClickListener {
            pickPhoto()
        }



    }

    private fun handleMenuItem(item: MenuItem):Boolean {


        return when(item.itemId){
            R.id.menu_share_data ->{
                shareEmployeeData()
                true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }


    //Updating the EmployeeDetails
    private fun setData(employee: Employee){

        with(employee.photo ){
            if (isNotEmpty()){
                employee_photo.setImageURI(Uri.parse(this))
                employee_photo.tag = this
            } else{
                employee_photo.setImageResource(R.drawable.blank_photo)
                employee_photo.tag = ""
            }
        }

        employee_name.setText(employee.name)

        employee_role.setSelection(employee.role)
        employee_age.setSelection(employee.age - 18)

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

        employee_responsibilities_detail.setText(employee.responsibility)
        employee_experience.setText(employee.experience)
        employee_education.setText(employee.education)
        if(employee.phone>0){
            employee_phone.setText(employee.phone.toString())
        }
        employee_email.setText(employee.email)
        employee_address.setText(employee.address)


    }

    //Getting New/Updated Employee Details and inserting it into the Database table
    private fun saveEmployee(){
        val name = employee_name.text.toString()
        val role = employee_role.selectedItemPosition
        val age = employee_age.selectedItemPosition + 18

        val selectedStatusButton =  gender_group.findViewById<RadioButton>(gender_group.checkedRadioButtonId)

        var gender = Gender.Other.ordinal
        if(selectedStatusButton.text == Gender.Male.name){
            gender = Gender.Male.ordinal
        } else if(selectedStatusButton.text == Gender.Female.name) {
            gender = Gender.Female.ordinal
        }

        val photo = employee_photo.tag as String

        val responsibilities = employee_responsibilities_detail.text.toString()
        val experience = employee_experience.text.toString()
        val education = employee_education.text.toString()
        val email = employee_email.text.toString()
        val address = employee_address.text.toString()



        //do the validation here for empty values or values in specific formats
        if(name.isBlank()){
            requireActivity().showToast("Please enter the {name} of employee")
            return
        }
        if(responsibilities.isBlank()){
            requireActivity().showToast("Please enter the {responsibilities} of employee")
            return
        }
        if(experience.isBlank()){
            requireActivity().showToast("Please enter the {experience} of employee")
            return
        }
        if(education.isBlank()){
            requireActivity().showToast("Please enter the {education} of employee")
            return
        }

        val phone: Long
        try {
            phone = employee_phone.text.toString().toLong()
        } catch(ex: NumberFormatException){
            requireActivity().showToast("Please enter {valid phone} of employee")
            return
        }

        if(email.isBlank()){
            requireActivity().showToast("Please enter {email} of employee")
            return
        }
        //simple email validation
        if(email.indexOf("@") < 0){
            requireActivity().showToast("Please enter the {valid phone} of employee")
            return
        }
        if(address.isBlank()){
            requireActivity().showToast("Please enter {address}  of employee")
            return
        }



        val employee = Employee(viewModel.employeeId.value!!, name, role, age, gender,
            responsibilities,experience,education,phone,email,address, photo)

        viewModel.saveEmployee(employee)

            val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
            with(sharedPref.edit()){
                putString(LATEST_EMPLOYEE_NAME_KEY,name)
                commit()
            }


        requireActivity().onBackPressed()
    }


    //Deleting the Employee
    private fun deleteEmployee(){
        viewModel.deleteEmployee()
        requireActivity().onBackPressed()

    }

 //1. First it ask fir permission
    private fun clickPhotoAfterPermission(view: View){
        //Check if we have camera permission or not
        if(ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            clickPhoto()
        }else{
            //If not request for camera permission
            requestCameraPermission(view)
        }
    }

   // 4.Make a file and Click phtoto from camera
    private fun clickPhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also{ takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                val photoFile: File? = try {
                    createFile(requireActivity(),Environment.DIRECTORY_PICTURES,"jpg")
                } catch (ex:IOException){
                    Toast.makeText(requireActivity(),"Error occurred while creating file: {ex.message}",Toast.LENGTH_SHORT).show()
                    null
                }
                photoFile?.also {
                    selectedPhotoPath = it.absolutePath
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireActivity(),
                        BuildConfig.APPLICATION_ID +".fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI)
                    startActivityForResult(takePictureIntent, CAMERA_PHOTO_REQUEST)
                }
            }
        }
    }

    // 5. Gets the Request result here
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK){
            when(requestCode){
                CAMERA_PHOTO_REQUEST -> {
                    val uri = Uri.fromFile(File(selectedPhotoPath))
                    employee_photo.setImageURI(uri)
                    employee_photo.tag = uri.toString()
                }
                GALLERY_PHOTO_REQUEST -> {
                    data?.data?.also { uri ->
                        val photoFile: File? = try {
                            createFile(requireActivity(),Environment.DIRECTORY_PICTURES,"jpg")
                        } catch (ex:IOException){
                            Toast.makeText(requireActivity(),"Error occurred while creating file: {ex.message}",Toast.LENGTH_SHORT).show()
                            null
                        }

                        photoFile?.also {
                            try{
                                val resolver = requireActivity().applicationContext.contentResolver
                                resolver.openInputStream(uri).use { steam ->
                                    val output = FileOutputStream(photoFile)
                                    steam!!.copyTo(output)
                                }
                                val fileUri = Uri.fromFile(photoFile)
                                employee_photo.setImageURI(fileUri)
                                employee_photo.tag = fileUri.toString()
                            } catch(e:FileNotFoundException){
                                e.printStackTrace()
                            } catch(e:IOException){
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        }
    }

//2. If permission is not granted then we ask for permission
    private fun requestCameraPermission(view: View) {

        if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.CAMERA)){
            val snack = Snackbar.make(view,"We need your permission to take a photo",Snackbar.LENGTH_INDEFINITE)

            snack.setAction("Ok",View.OnClickListener {
                requestPermissions(arrayOf(Manifest.permission.CAMERA),PERMISSION_REQUEST_CAMERA)
            })
            snack.show()
        }else{
            requestPermissions(arrayOf(Manifest.permission.CAMERA),PERMISSION_REQUEST_CAMERA)

        }

    }

   // 3.
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == PERMISSION_REQUEST_CAMERA){
            if (grantResults.size==1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                clickPhoto()
            }else{
                Toast.makeText(requireActivity(),"Permission denied to use camera",Toast.LENGTH_SHORT).show()
            }
        }

    }



//This picks the photo from gallery
    private fun pickPhoto() {

        val pickPhotoIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickPhotoIntent.resolveActivity(requireActivity().packageManager)?.also {
            startActivityForResult(pickPhotoIntent, GALLERY_PHOTO_REQUEST)
        }

    }

//
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.detail_menu,menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//
//        return when(item.itemId){
//        R.id.menu_share_data ->{
//            shareEmployeeData()
//            true
//        }
//
//        else -> return super.onOptionsItemSelected(item)
//        }
//    }

    private fun shareEmployeeData() {
        val name = employee_name.text.toString()
        val role = employee_role.selectedItem.toString()
        val age = employee_age.selectedItem.toString()

        val selectedStatusButton =  gender_group.findViewById<RadioButton>(gender_group.checkedRadioButtonId)
        var gender = selectedStatusButton.text

        val shareText = getString(R.string.share_text,name,role,age,gender)

        val sendIntent:Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT,shareText)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent,"Choose the app to send data")
        startActivity(shareIntent)

    }


}
