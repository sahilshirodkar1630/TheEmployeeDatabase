package com.example.employeeexample.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.employeeexample.R
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun createFile(context: Context, folder:String, ext:String): File {
    val timeStamp:String = SimpleDateFormat("yyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    //getFilesDir() is for internal storage
    val filesDir: File? = context.getExternalFilesDir(folder)
    val newFile = File(filesDir,"$timeStamp.$ext")
    newFile.createNewFile()
    return newFile
}

fun Activity.showToast(msg: String, duration: Int = Toast.LENGTH_SHORT){
    Toast.makeText(this, msg, duration).show()
}
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration:AppBarConfiguration



//    override fun onBackPressed() {
//        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
//            drawer_layout.closeDrawer(GravityCompat.START)
//        }else{
//            super.onBackPressed()
//        }
//
//
//    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}
