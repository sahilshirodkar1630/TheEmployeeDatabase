package com.example.employeeexample.ui.list


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Credentials
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.employeeexample.BuildConfig
import com.example.employeeexample.R
import com.example.employeeexample.data.Employee
import com.example.employeeexample.ui.EmployeeAdapter
import com.example.employeeexample.ui.EmployeeListFragmentDirections
import com.example.employeeexample.ui.detail.LATEST_EMPLOYEE_NAME_KEY
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.fragment_employee_list.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*

const val READ_FILE_REQUEST = 1

const val CREATE_FILE_REQUEST = 1
class EmployeeListFragment : Fragment() {

    private lateinit var viewModel: EmployeeListViewModel

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        //To show menus
//        setHasOptionsMenu(true)

        //Initializing viewModel
        viewModel = ViewModelProviders.of(this)
            .get(EmployeeListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employee_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupNavigationDrawer()

//        toolbar_list.inflateMenu(R.menu.list_menu)
//        toolbar_list.setOnMenuItemClickListener {
//            handleMenuItem(it)
//        }

        //This pass the data  to EmployeeDetailFragment whenever a item is clicked
        with(employee_list){
            layoutManager = LinearLayoutManager(activity)
            adapter = EmployeeAdapter {show,id ->
                if(show){
                    findNavController().navigate(EmployeeListFragmentDirections.actionEmployeeListFragmentToEmployeeShowFragment(id))

                }else {
                    findNavController().navigate(
                        EmployeeListFragmentDirections.actionEmployeeListFragmentToEmployeeDetailFragment(
                            id
                        )
                    )
                }
            }
        }

        //Whenever Floating action button is clicked id=0 will be pass to EmployeeDetailFragment
        add_employee.setOnClickListener{
            findNavController().navigate(
                EmployeeListFragmentDirections.actionEmployeeListFragmentToEmployeeDetailFragment(0)
            )
        }

        // Observes the list of employees through livedata and update the recycler view
        viewModel.employees.observe(viewLifecycleOwner, Observer {
            if(it.isEmpty()){
                no_employee_record.visibility = View.VISIBLE
            }else{
                no_employee_record.visibility = View.INVISIBLE
            }
            (employee_list.adapter as EmployeeAdapter).submitList(it)
        })



    }

    private fun setupNavigationDrawer() {
        val navController = Navigation.findNavController(requireActivity(),R.id.nav_host_fragment)
        val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout);
        val navigationView = requireActivity().findViewById<NavigationView>(R.id.navigation_view);

        NavigationUI.setupWithNavController(toolbar,navController,drawerLayout)
        navigationView.setupWithNavController(navController)

        navigationView.setNavigationItemSelectedListener {
            drawerLayout.closeDrawers()

            when(it.itemId){
                R.id.add_new -> {findNavController().navigate(EmployeeListFragmentDirections.actionEmployeeListFragmentToEmployeeDetailFragment(0))
                }
                R.id.contact  -> { findNavController().navigate(EmployeeListFragmentDirections.actionEmployeeListFragmentToContactFragment())
                }
                R.id.about  -> {  findNavController().navigate(EmployeeListFragmentDirections.actionEmployeeListFragmentToAboutFragment())
                }
                R.id.menu_export_data -> {
                    GlobalScope.launch {
                        exportEmployees()
                    }

                }
                R.id.menu_import_data -> {
                    importEmployees()

                }
                R.id.menu_latest_employee_name -> {
                    val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
                    val name = sharedPref?.getString(LATEST_EMPLOYEE_NAME_KEY,"")
                    if(!name.isNullOrEmpty()){
                        Toast.makeText(requireActivity(),"The name of latest employee is : ${name}",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(requireActivity()," No employee added yet",Toast.LENGTH_SHORT).show()
                    }
                }

            }
            true
        }
    }
//
//    private fun handleMenuItem(item: MenuItem):Boolean {
//        return when(item.itemId){
//            R.id.menu_import_data -> {
//                importEmployees()
//                true
//            }
//            R.id.menu_export_data ->{
//                GlobalScope.launch {
//                    exportEmployees()
//
//                }
//                true
//            }
//            R.id.menu_latest_employee_name -> {
//                val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)?: return true
//                val name = sharedPref.getString(LATEST_EMPLOYEE_NAME_KEY,"")
//                if(!name.isNullOrEmpty()){
//                    Toast.makeText(requireActivity(),"The name of latest employee is : ${name}",Toast.LENGTH_SHORT).show()
//                }else{
//                    Toast.makeText(requireActivity()," No employee added yet",Toast.LENGTH_SHORT).show()
//                }
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.list_menu,menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when(item.itemId){
//            R.id.menu_import_data -> {
//                importEmployees()
//                true
//            }
//            R.id.menu_export_data ->{
//                GlobalScope.launch {
//                    exportEmployees()
//
//                }
//                true
//            }
//            R.id.menu_latest_employee_name -> {
//                val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)?: return true
//                val name = sharedPref.getString(LATEST_EMPLOYEE_NAME_KEY,"")
//                if(!name.isNullOrEmpty()){
//                    Toast.makeText(activity!!,"The name of latest employee is : ${name}",Toast.LENGTH_SHORT).show()
//                }else{
//                    Toast.makeText(activity!!," No employee added yet",Toast.LENGTH_SHORT).show()
//                }
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//
//    }

    private suspend fun exportEmployees() {

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"
            putExtra(Intent.EXTRA_TITLE, "employee_list.csv")
        }
        startActivityForResult(intent, CREATE_FILE_REQUEST)
    }
//
//        var csvFile :File? = null
//        withContext(Dispatchers.IO){
//            csvFile = try{
//                createFile(activity!!,"Documents","csv")
//            }catch (ex:IOException){
//                Toast.makeText(activity!!,"Error occurred while creating file {ex.message",Toast.LENGTH_SHORT).show()
//                null
//            }
//
//            csvFile?.printWriter()?.use { out ->
//                val employees = viewModel.getEmployeeList()
//                if(employees.isNotEmpty()){
//                    employees.forEach{
//                        out.println(it.name + "," + it.role + "," + it.age + "," + it.gender)
//                    }
//                }
//            }
//        }
//
//        withContext(Dispatchers.Main){
//            csvFile?.let{
//                val uri = FileProvider.getUriForFile(
//                    requireActivity(), BuildConfig.APPLICATION_ID + ".fileprovider",
//                    it)
//                launchFile(uri, "csv")
//            }
//        }
//    }


//    private fun launchFile(uri: Uri, ext: String){
//        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
//        val intent = Intent(Intent.ACTION_VIEW)
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        intent.setDataAndType(uri, mimeType)
//        if(intent.resolveActivity(requireActivity().packageManager) != null){
//            startActivity(intent)
//        } else{
//            Toast.makeText(requireActivity(), getString(R.string.no_app_csv), Toast.LENGTH_SHORT).show()
//        }
//    }



    private fun importEmployees() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"
        }
        intent.resolveActivity(requireActivity().packageManager)?.also {
            startActivityForResult(intent, READ_FILE_REQUEST)
        }

//        Intent(Intent.ACTION_GET_CONTENT).also {  readFileIntent ->
//            readFileIntent.addCategory(Intent.CATEGORY_OPENABLE)
//            readFileIntent.type = "text/*"
//            readFileIntent.resolveActivity(activity!!.packageManager)?.also {
//                startActivityForResult(readFileIntent, READ_FILE_REQUEST)
//            }
//
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK){
            when(requestCode) {
                READ_FILE_REQUEST -> {
                    GlobalScope.launch {
                        data?.data?.also { uri ->
                            readFromFile(uri)
                        }
                    }
                }
                CREATE_FILE_REQUEST -> {
                    data?.data?.also { uri ->
                        GlobalScope.launch {

                            if(writeToFile(uri)){
                                withContext(Dispatchers.Main){
                                    Toast.makeText(requireActivity(),"File exported Succesfully",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun writeToFile(uri: Uri): Boolean {
        try{
            requireActivity().applicationContext.contentResolver.openFileDescriptor(uri,"w")?.use { pfd->
                FileOutputStream(pfd.fileDescriptor).use { outStream ->
                    val employees = viewModel.getEmployeeList()
                    if(employees.isNotEmpty()){
                        employees.forEach{
                            outStream.write((it.name + "," + it.role + "," + it.age + "," + it.gender+"\n").toByteArray())
                        }
                    }
                }

            }

        }catch(e: FileNotFoundException){
            e.printStackTrace()
            return false
        } catch(e: IOException){
            e.printStackTrace()
            return false
        }
        return true

    }

    private suspend fun readFromFile(uri: Uri) {
        try{
            requireActivity().applicationContext.contentResolver.openFileDescriptor(uri,"r")?.use {
                    FileInputStream(it.fileDescriptor).use {
                        withContext(Dispatchers.IO) {
                            parseCSVFile(it)
                        }
                    }

            }

        }catch(e: FileNotFoundException){
            e.printStackTrace()
        } catch(e: IOException){
            e.printStackTrace()
        }

    }

    private suspend fun parseCSVFile(stream:InputStream) {

        val employees = mutableListOf<Employee>()

        BufferedReader(InputStreamReader(stream)).forEachLine {
            val tokens = it.split(",")
            employees.add(Employee(id = 0, name = tokens[0], role = tokens[1].toInt(),
                age = tokens[2].toInt(), gender = tokens[3].toInt(), photo = "", responsibility = "",
                experience = "", education = "", phone = 0, email = "", address = ""))
        }
        if (employees.isNotEmpty()){
            viewModel.insertEmployees(employees)
        }
    }

}
