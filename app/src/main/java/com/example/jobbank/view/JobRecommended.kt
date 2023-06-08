package com.example.jobbank.view

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobbank.databinding.ActivityJobRecommendedBinding
import com.example.jobbank.model.Job
import com.example.jobbank.model.adapter.JobAdapter
import com.example.jobbank.model.adapter.JobTouchListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class JobRecommended : AppCompatActivity() {

    private lateinit var binding: ActivityJobRecommendedBinding
    val database = Firebase.database
    private lateinit var sharedPreferencesEmail: SharedPreferences
    private lateinit var sharedPreferencesId: SharedPreferences

    private val jobRecommendedList = mutableListOf<Job>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobRecommendedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesEmail = getSharedPreferences("sharedPreferencesEmail", Context.MODE_PRIVATE)
        sharedPreferencesId = getSharedPreferences("sharedPreferencesId", Context.MODE_PRIVATE)

        visualize()

        binding.rvJobRecommended.addOnItemTouchListener(
            JobTouchListener(this, binding.rvJobRecommended, object : JobTouchListener.ClickListener {
                override fun onClick(view: View?, position: Int) {
                    val bottomSheetDialogFragment = Job_Details().apply {
                        arguments = Bundle().apply {
                            putInt("selected", jobRecommendedList[position].id)
                        }
                    }
                    bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
                }
                override fun onLongClick(view: View?, position: Int) {}
            })
        )
    }

    private fun visualize(){
        val ref2 = database.getReference("users").orderByChild("email").equalTo(sharedPreferencesEmail.getString("spEmail", ""))
        ref2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val speciality = childSnapshot.child("speciality").getValue(String::class.java)
                    val ref = database.getReference("jobs").orderByChild("status").equalTo(true)
                    ref.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            jobRecommendedList.clear()
                            for (jobSnapshot in snapshot.children) {
                                val item = jobSnapshot.getValue(Job::class.java)
                                if (item?.createdBy != sharedPreferencesId.getString("spEmail", "")) {
                                    if(item?.speciality == speciality) {
                                        jobRecommendedList.add(item!!)
                                    }
                                }
                            }
                            binding.rvJobRecommended.layoutManager = LinearLayoutManager(this@JobRecommended)
                            binding.rvJobRecommended.adapter = JobAdapter(jobRecommendedList, false)
                            toggleEmptyView()
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun toggleEmptyView(){
        if (binding.rvJobRecommended.adapter?.itemCount == 0) {
            binding.rvJobRecommended.visibility = View.GONE
            binding.ivNoRecommendedJobRecommended.visibility = View.VISIBLE
            binding.tvNoRecommendedJobRecommended.visibility = View.VISIBLE
        } else {
            binding.rvJobRecommended.visibility = View.VISIBLE
            binding.ivNoRecommendedJobRecommended.visibility = View.GONE
            binding.tvNoRecommendedJobRecommended.visibility = View.GONE
        }
    }
}