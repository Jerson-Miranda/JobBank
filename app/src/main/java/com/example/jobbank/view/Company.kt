package com.example.jobbank.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.jobbank.R
import com.example.jobbank.databinding.ActivityCompanyBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.File

class Company : AppCompatActivity() {

    private lateinit var binding: ActivityCompanyBinding
    //SharedPreferences
    private lateinit var sharedPreferencesEmail: SharedPreferences
    private lateinit var sharedPreferencesId: SharedPreferences
    //Firebase Storage
    private var storage = FirebaseStorage.getInstance()
    private val pickProfileRequestCode = 1
    private val pickBannerRequestCode = 2

    private val pickProfileActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data
            }
        }

    private val pickBannerActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompanyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesEmail = getSharedPreferences("sharedPreferencesEmail", Context.MODE_PRIVATE)
        sharedPreferencesId = getSharedPreferences("sharedPreferencesId", Context.MODE_PRIVATE)

        val me = intent.getBooleanExtra("me", true)

        if(!me){
            binding.ibEditProfileCompany.visibility = View.GONE
            binding.ibEditProfileDataCompany.visibility = View.GONE
            binding.ibEditBannerCompany.visibility = View.GONE
            binding.btnFollowCompany.visibility = View.VISIBLE
            binding.btnMesaggeCompany.visibility = View.VISIBLE
            binding.btnFollowCompany.setText(R.string.lbFollow)
            binding.btnMesaggeCompany.setText(R.string.message_company)
        } else {
            binding.ibEditProfileCompany.visibility = View.VISIBLE
            binding.ibEditProfileDataCompany.visibility = View.VISIBLE
            binding.ibEditBannerCompany.visibility = View.VISIBLE
            binding.btnFollowCompany.visibility = View.GONE
            binding.btnMesaggeCompany.visibility = View.GONE
        }

        getDataFromFirebase()

        binding.ibEditProfileCompany.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickProfileActivityResultLauncher.launch(intent)
        }

        binding.ibEditBannerCompany.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickBannerActivityResultLauncher.launch(intent)
        }
    }

    private fun getDataFromFirebase() {
        val email = intent.getStringExtra("selected")
        if (email == null) {
            val ref = FirebaseDatabase.getInstance().getReference("company").orderByChild("email").equalTo(sharedPreferencesEmail.getString("spEmail", ""))
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (childSnapshot in snapshot.children) {
                            binding.tvUsernameCompany.text = childSnapshot.child("name").getValue(String::class.java)
                            binding.tvSpecialityCompany.text = childSnapshot.child("speciality").getValue(String::class.java)
                            binding.tvAddressBCompany.text = childSnapshot.child("address").getValue(String::class.java)
                            binding.tvFollowersCompany.text = childSnapshot.child("followers").getValue(String::class.java)
                            Picasso.get()
                                .load(childSnapshot.child("imageUrl").getValue(String::class.java))
                                .fit()
                                .centerCrop()
                                .into(binding.ivProfileCompany)
                            Picasso.get()
                                .load(childSnapshot.child("imageUrl2").getValue(String::class.java))
                                .fit()
                                .centerCrop()
                                .into(binding.ivBannerCompany)
                        }
                    }

                }
                override fun onCancelled(error: DatabaseError) {}
            })
        } else {
            val ref = FirebaseDatabase.getInstance().getReference("company").orderByChild("email").equalTo(email)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (childSnapshot in snapshot.children) {
                            binding.tvUsernameCompany.text = childSnapshot.child("name").getValue(String::class.java)
                            binding.tvSpecialityCompany.text = childSnapshot.child("speciality").getValue(String::class.java)
                            binding.tvAddressBCompany.text = childSnapshot.child("address").getValue(String::class.java)
                            binding.tvFollowersCompany.text = childSnapshot.child("followers").getValue(String::class.java)
                            Picasso.get()
                                .load(childSnapshot.child("imageUrl").getValue(String::class.java))
                                .fit()
                                .centerCrop()
                                .into(binding.ivProfileCompany)
                            Picasso.get()
                                .load(childSnapshot.child("imageUrl2").getValue(String::class.java))
                                .fit()
                                .centerCrop()
                                .into(binding.ivBannerCompany)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    @Deprecated("Use registerForActivityResult and ActivityResultCallback instead")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickProfileRequestCode && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            getRealPathFromUri(uri!!)
            val fileRef = storage.getReference("company/${sharedPreferencesId.getInt("spId", 0)}/profile.png")

            fileRef.putFile(uri)
                .addOnSuccessListener {
                    getImageProfile()
                    Toast.makeText(this, "File uploaded successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to upload file", Toast.LENGTH_SHORT).show()
                }
        } else if (requestCode == pickBannerRequestCode && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            getRealPathFromUri(uri!!)
            val fileRef = storage.getReference("company/${sharedPreferencesId.getInt("spId", 0)}/banner.png")

            fileRef.putFile(uri)
                .addOnSuccessListener {
                    getImageBanner()
                    Toast.makeText(this, "File uploaded successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to upload file", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun getImageBanner(){
        binding.ivBannerCompany.setImageBitmap(null)
        val email = intent.getStringExtra("selected")
        if(email == null){
            val banner = storage.getReference("company/${sharedPreferencesId.getInt("spId", 0)}/banner.png")
            binding.pbBannerCompany.visibility = View.VISIBLE
            val localFile = File.createTempFile("images", "jpg")
            banner.getFile(localFile)
                .addOnSuccessListener {
                    binding.pbBannerCompany.visibility = View.GONE
                    val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                    Glide.with(this)
                        .load(bitmap)
                        .centerCrop()
                        .into(binding.ivBannerCompany)
                }
                .addOnFailureListener {}
        } else {
            val ref = FirebaseDatabase.getInstance().getReference("company").orderByChild("email").equalTo(email)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (childSnapshot in snapshot.children) {
                            Picasso.get()
                                .load(childSnapshot.child("imageUrl2").getValue(String::class.java))
                                .fit()
                                .centerCrop()
                                .into(binding.ivBannerCompany)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }

    }

    private fun getImageProfile() {
        binding.ivProfileCompany.setImageBitmap(null)
        val email = intent.getStringExtra("selected")
        if(email == null){
            val profile = storage.getReference("company/${sharedPreferencesId.getInt("spId", 0)}/profile.png")
            binding.pbProfileCompany.visibility = View.VISIBLE
            val localFile2 = File.createTempFile("images1", "jpg")
            profile.getFile(localFile2)
                .addOnSuccessListener {
                    binding.pbProfileCompany.visibility = View.GONE
                    val bitmap = BitmapFactory.decodeFile(localFile2.absolutePath)
                    Glide.with(this)
                        .load(bitmap)
                        .centerCrop()
                        .into(binding.ivProfileCompany)
                }
                .addOnFailureListener {}
        } else {
            val ref = FirebaseDatabase.getInstance().getReference("company").orderByChild("email").equalTo(email)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (childSnapshot in snapshot.children) {
                            Picasso.get()
                                .load(childSnapshot.child("imageUrl").getValue(String::class.java))
                                .fit()
                                .centerCrop()
                                .into(binding.ivProfileCompany)
                        }
                    }

                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
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
}