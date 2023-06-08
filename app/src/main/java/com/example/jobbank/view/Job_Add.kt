package com.example.jobbank.view

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import com.example.jobbank.view.fragment.JobAddData
import com.example.jobbank.R
import com.example.jobbank.databinding.ActivityJobAddBinding
import com.example.jobbank.model.Company
import com.example.jobbank.model.Job
import com.example.jobbank.view.fragment.OnButtonClickListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class Job_Add : AppCompatActivity(), OnButtonClickListener {

    private lateinit var binding: ActivityJobAddBinding
    private val database = FirebaseDatabase.getInstance()
    private lateinit var f: JobAddData
    private lateinit var sharedPreferencesId: SharedPreferences
    private lateinit var sharedPreferencesEmail: SharedPreferences
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesId = getSharedPreferences("sharedPreferencesId", Context.MODE_PRIVATE)
        sharedPreferencesEmail = getSharedPreferences("sharedPreferencesEmail", Context.MODE_PRIVATE)

        f = JobAddData()
        userData()
        image()

        binding.btnTitleJobAdd.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("title", "Job title")
            bundle.putString("query", "jobtitle")
            f.arguments = bundle
            supportFragmentManager.beginTransaction()
                .replace(R.id.containerFragment, f)
                .commit()
        }

        binding.btnBusinessJobAdd.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("title", "Speciality")
            bundle.putString("query", "speciality")
            f.arguments = bundle
            supportFragmentManager.beginTransaction()
                .replace(R.id.containerFragment, f)
                .commit()
        }

        binding.btnTypeJobAdd.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("title", "Job type")
            bundle.putString("query", "jobtype")
            f.arguments = bundle
            supportFragmentManager.beginTransaction()
                .replace(R.id.containerFragment, f)
                .commit()
        }

        binding.btnLocationJobAdd.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("title", "Job Location")
            bundle.putString("query", "joblocation")
            f.arguments = bundle
            supportFragmentManager.beginTransaction()
                .replace(R.id.containerFragment, f)
                .commit()
        }

        binding.btnPostJobAdd.setOnClickListener{
            if (!validate()){
                Toast.makeText(this@Job_Add, "Empty fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            setValue()
        }

        binding.sbApplicationsJobAdd.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                binding.tvApplicationsJobAdd.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        binding.sbAvailablesJobAdd.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                binding.tvAvailablesJobAdd.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun userData() {
        val myRef = database.getReference("company")
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val item = snapshot.getValue(Company::class.java)
                    if (item?.id == sharedPreferencesId.getInt("spId", 0)) {
                        binding.tvBusinessJobAdd.text = item?.name!!
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Job_Add, "Unknown error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun validate(): Boolean{
        val d1 = binding.btnTitleJobAdd.compoundDrawables
        val d2 = binding.btnBusinessJobAdd.compoundDrawables
        val d3 = binding.btnTypeJobAdd.compoundDrawables
        val d4 = binding.btnLocationJobAdd.compoundDrawables
        if (d1[2] == null && d2[2] == null && d3[2] == null && d4[2] == null) {
            return !binding.etDescriptionJobAdd.text.isEmpty()
        }
        return false
    }

    private fun setValue(){
        val myRef = database.getReference("jobs").orderByKey().limitToLast(1)
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val id = dataSnapshot.children.last().child("id").getValue(Int::class.java)
                jobData(id!!)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Job_Add, "Unknown error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun jobData(id: Int) {
        val myRef = database.getReference("jobs").child(id.plus(1).toString())
        myRef.setValue(jobObject(id.plus(1))).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this@Job_Add, "Published job", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(
                    this,
                    "We are sorry that your job was not created. Try again", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun jobObject(id: Int): Job {
        return Job(
            id,
            sharedPreferencesEmail.getString("spEmail", "").toString(),
            binding.btnTitleJobAdd.text.toString(),
            binding.tvBusinessJobAdd.text.toString(),
            binding.btnLocationJobAdd.text.toString(),
            0,
            binding.tvApplicationsJobAdd.text.toString().toInt(),
            binding.btnTypeJobAdd.text.toString(),
            binding.tvAvailablesJobAdd.text.toString().toInt(),
            binding.sStudentsJobAdd.isChecked,
            binding.btnBusinessJobAdd.text.toString(),
            binding.etDescriptionJobAdd.text.toString(),
            false,
            true,
            imageUrl!!
        )
    }

    private fun image(){
        val storage = FirebaseStorage.getInstance()
        val imageRef = storage.getReference("company").child(sharedPreferencesId.getInt("spId",0).toString()).child("profile.png")
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            imageUrl = uri.toString()
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Unknown error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onButtonClicked(parameter: String?, view: String) {
        if (view == "Job title"){
            binding.btnTitleJobAdd.text = parameter
            binding.btnTitleJobAdd.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        } else if (view == "Speciality"){
            binding.btnBusinessJobAdd.text = parameter
            binding.btnBusinessJobAdd.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        } else if (view == "Job type"){
            binding.btnTypeJobAdd.text = parameter
            binding.btnTypeJobAdd.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        } else if (view == "Job Location"){
            binding.btnLocationJobAdd.text = parameter
            binding.btnLocationJobAdd.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
        supportFragmentManager.beginTransaction().remove(f).commit()
    }
}