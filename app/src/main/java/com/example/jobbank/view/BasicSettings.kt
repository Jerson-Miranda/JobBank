package com.example.jobbank.view

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.jobbank.databinding.ActivityBasicSettingsBinding
import com.example.jobbank.view.fragment.Sign_Data.Data
import com.example.jobbank.view.Sign_Type_User.TypeUser
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import kotlin.system.exitProcess
import com.example.jobbank.view.fragment.Sign_Verification.Verification
import com.example.jobbank.view.fragment.Sign_Data_Job.DataJob

class BasicSettings : AppCompatActivity() {

    private lateinit var binding: ActivityBasicSettingsBinding
    val storage = FirebaseStorage.getInstance()
    private val PICK_PROFILE_REQUEST_CODE = 1
    private val PICK_BANNER_REQUEST_CODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBasicSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getImageBanner()
        getImageProfile()
        getData()

        binding.btnContinueBasicSettingsImages.setOnClickListener {
            startActivity(Intent(this, Home::class.java))
        }

        binding.ibEditProfileBasicSettingsImages.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_PROFILE_REQUEST_CODE)
        }

        binding.ibEditBannerBasicSettingsImages.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_BANNER_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var table = ""
        table = if (Sign_Type_User.TypeUser.typeUser == "company"){
            "company"
        } else {
            "users"
        }
        if (requestCode == PICK_PROFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            getRealPathFromUri(uri!!)
            val fileRef = storage.getReference().child("${table}/${Verification.id}/profile.png")

            fileRef.putFile(uri!!)
                .addOnSuccessListener {
                    getImageProfile()
                    Toast.makeText(this, "Archivo subido exitosamente", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al subir el archivo", Toast.LENGTH_SHORT).show()
                }
        } else if (requestCode == PICK_BANNER_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            getRealPathFromUri(uri!!)
            val fileRef = storage.getReference().child("${table}/${Verification.id}/banner.png")

            fileRef.putFile(uri!!)
                .addOnSuccessListener {
                    getImageBanner()
                    Toast.makeText(this, "Archivo subido exitosamente", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al subir el archivo", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun getImageBanner(){
        var table = ""
        table = if (TypeUser.typeUser == "company"){
            "company"
        } else {
            "users"
        }
        val banner = storage.getReference("${table}/${Verification.id}/banner.png")
        binding.pbBannerBasicSettingsImages.visibility = View.VISIBLE
        val localFile = File.createTempFile("images", "jpg")
        banner.getFile(localFile)
            .addOnSuccessListener { bytes ->
                binding.pbBannerBasicSettingsImages.visibility = View.GONE
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                Glide.with(this)
                    .load(bitmap)
                    .centerCrop()
                    .into(binding.ivBannerBasicSettingsImages)
            }
            .addOnFailureListener { exception -> }
    }

    private fun getImageProfile() {
        var table = ""
        table = if (TypeUser.typeUser == "company"){
            "company"
        } else {
            "users"
        }
        val profile = storage.getReference("${table}/${Verification.id}/profile.png")
        binding.pbProfileBasicSettingsImages.visibility = View.VISIBLE
        val localFile2 = File.createTempFile("images1", "jpg")
        profile.getFile(localFile2)
            .addOnSuccessListener { bytes ->
                binding.pbProfileBasicSettingsImages.visibility = View.GONE
                val bitmap = BitmapFactory.decodeFile(localFile2.absolutePath)
                Glide.with(this)
                    .load(bitmap)
                    .centerCrop()
                    .into(binding.ivProfileBasicSettingsImages)
            }
            .addOnFailureListener { exception -> }
    }

    private fun getRealPathFromUri(uri: Uri): String? {
        val cursor: Cursor? = this.contentResolver.query(uri, null, null, null, null)
        return cursor?.let {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            val path = it.getString(columnIndex)
            it.close()
            path
        }
    }

    private fun getData() {
        if (TypeUser.typeUser == "company") {
            binding.tvUsernameBasicSettings.text = Data.firstName
            binding.tvBusinessBasicSettings.text = DataJob.website
        } else {
            binding.tvUsernameBasicSettings.text = Data.firstName + " "  + Data.lastName
            binding.tvBusinessBasicSettings.text = DataJob.schooljob
        }
        binding.tvSpecialityBasicSettings.text = DataJob.speciality
        binding.tvAddressBBasicSettings.text = DataJob.location
    }

    override fun onBackPressed() {
        finishAffinity() // Cierra todas las actividades en segundo plano
        exitProcess(0) // Cierra la aplicación completamente
    }
}