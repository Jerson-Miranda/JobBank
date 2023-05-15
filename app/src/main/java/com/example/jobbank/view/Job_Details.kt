package com.example.jobbank.view

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.jobbank.R
import com.example.jobbank.databinding.ActivityJobDetailsBinding
import com.example.jobbank.model.Company
import com.example.jobbank.model.Job
import com.example.jobbank.model.Notification
import com.example.jobbank.model.User
import com.example.jobbank.view.fragment.Sign_Data
import com.example.jobbank.view.fragment.Sign_Registration
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.File

class Job_Details : BottomSheetDialogFragment() {

    private lateinit var binding: ActivityJobDetailsBinding
    private var selected: Int? = null
    private lateinit var sharedPreferencesId: SharedPreferences
    private lateinit var sharedPreferencesEmail: SharedPreferences
    private var nameUser: String = ""
    private var image: String = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityJobDetailsBinding.inflate(inflater, container, false)
        val view = binding.root

        arguments?.let {
            selected = it.getInt("selected")
        }

        sharedPreferencesId = requireActivity().getSharedPreferences("sharedPreferencesId", Context.MODE_PRIVATE)
        sharedPreferencesEmail = requireActivity().getSharedPreferences("sharedPreferencesEmail", Context.MODE_PRIVATE)

        getDataFromFirebase(selected!!)

        nameUser2()
        binding.btnApplyJobDetails.setOnClickListener{
            val myRef = FirebaseDatabase.getInstance().getReference("notifications").orderByKey().limitToLast(1)
            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val id = dataSnapshot.children.last().child("id").getValue(Int::class.java)
                    val myRef2 = FirebaseDatabase.getInstance().getReference("notifications").child(id?.plus(1).toString())
                    myRef2.setValue(
                        notificationData(
                            id?.plus(1)!!,
                            sharedPreferencesEmail.getString("spEmail", "")!!,
                            binding.tvIdCreatedByJobDetails.text.toString())
                    ).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(activity,"A request has been submitted", Toast.LENGTH_SHORT).show()
                            dismiss()
                        } else {
                            Toast.makeText(activity,"Error sending request", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Unknown error", Toast.LENGTH_SHORT).show()
                }
            })
        }

        return view
    }

    private fun notificationData(id: Int, sender: String, recipient: String): Notification{
        val data = Notification(
            id,
            sender,
            recipient,
            "Solicitud de trabajo",
            "$nameUser solicito un puesto en tu trabajo para " + binding.tvTitleJobDetails.text.toString(),
            0,
            false,
            image
        )
        return data
    }

    private fun getDataFromFirebase(selected: Int) {
        val ref = FirebaseDatabase.getInstance().getReference("jobs").child(selected.toString())
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val item = snapshot.getValue(Job::class.java)
                    if (item != null) {
                        binding.tvTitleJobDetails.text = item.title
                        binding.tvBusinessJobDetails.text = item.business
                        binding.tvAddressJobDetails.text = item.address
                        binding.tvTimeJobDetails.text = item.time.toString() + " hours ago "
                        binding.tvApplicantsJobDetails.text = ": " + item.applications + " applications"
                        binding.tvTypeJobDetails.text = item.type
                        binding.tvAvailableJobDetails.text = item.available.toString() + " jobs available"
                        if (item.students) binding.tvStudentsJobDetails.text = "For students" else binding.tvStudentsJobDetails.text = "Not for students"
                        binding.tvSpecialityJobDetails.text = item.speciality
                        binding.tvDescriptionJobDetails.text = item.description
                        nameUser(item.createdBy)
                        if (item.archive){
                            binding.ibArchiveJobDetails.setImageResource(R.drawable.ic_archive_true)
                        } else {
                            binding.ibArchiveJobDetails.setImageResource(R.drawable.ic_archive_false)
                        }
                        Picasso.get()
                            .load(item.imageUrl)
                            .fit()
                            .centerCrop()
                            .into(binding.ibBusinessJobDetails)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun nameUser(email: String){
        val ref = FirebaseDatabase.getInstance().getReference("company").orderByChild("email").equalTo(email)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(Company::class.java)
                    if (item != null) {
                        binding.tvIdCreatedByJobDetails.text = item.email
                        binding.tvCreatedByJobDetails.text = "Published by: " + item.name
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun nameUser2(){
        val ref = FirebaseDatabase.getInstance().getReference("users").orderByChild("email").equalTo(sharedPreferencesEmail.getString("spEmail", ""))
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(User::class.java)
                        nameUser = item?.firstName.toString()
                        image = item?.imageUrl.toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}