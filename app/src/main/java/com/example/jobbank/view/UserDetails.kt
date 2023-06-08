package com.example.jobbank.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.jobbank.R
import com.example.jobbank.databinding.ActivityUserDetailsBinding
import com.example.jobbank.model.Company
import com.example.jobbank.model.Notification
import com.example.jobbank.model.User
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class UserDetails : BottomSheetDialogFragment() {

    private lateinit var binding: ActivityUserDetailsBinding
    private var selected: String? = null
    private lateinit var sharedPreferencesId: SharedPreferences
    private lateinit var sharedPreferencesEmail: SharedPreferences
    private var nameUser: String = ""
    private var image: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityUserDetailsBinding.inflate(inflater, container, false)
        val view = binding.root

        arguments?.let {
            selected = it.getString("selected")
        }

        sharedPreferencesId = requireActivity().getSharedPreferences("sharedPreferencesId", Context.MODE_PRIVATE)
        sharedPreferencesEmail = requireActivity().getSharedPreferences("sharedPreferencesEmail", Context.MODE_PRIVATE)

        getDataFromFirebase(selected!!)

        nameUser2()
        binding.btnApplyUserDetails.setOnClickListener{
            val myRef = FirebaseDatabase.getInstance().getReference("notifications").orderByKey().limitToLast(1)
            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val id = dataSnapshot.children.last().child("id").getValue(Int::class.java)
                    val myRef2 = FirebaseDatabase.getInstance().getReference("notifications").child(id?.plus(1).toString())
                    myRef2.setValue(
                        notificationData(
                            id?.plus(1)!!,
                            sharedPreferencesEmail.getString("spEmail", "")!!,
                            binding.tvIdCreatedByUserDetails.text.toString())
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

    private fun notificationData(id: Int, sender: String, recipient: String): Notification {
        return Notification(
            id,
            sender,
            recipient,
            "Job Request",
            "$nameUser accepted his job application",
            0,
            false,
            image
        )
    }

    private fun getDataFromFirebase(selected: String) {
        val ref = FirebaseDatabase.getInstance().getReference("users").orderByChild("email").equalTo(selected)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(User::class.java)
                    binding.tvUsernameUserDetails.text = getString(R.string.username_format, item?.firstName, item?.lastName)
                    binding.tvTypeUserDetails.text = item?.typeUser
                    binding.tvSpecialityUserDetails.text = item?.speciality
                    binding.tvDescriptionUserDetails.text = item?.firstName
                    binding.tvIdCreatedByUserDetails.text = item?.email
                    Picasso.get()
                        .load(item?.imageUrl)
                        .fit()
                        .centerCrop()
                        .into(binding.ivProfileUserDetails)
                    Picasso.get()
                        .load(item?.imageUrl2)
                        .fit()
                        .centerCrop()
                        .into(binding.ivBannerUserDetails)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun nameUser2(){
        val ref = FirebaseDatabase.getInstance().getReference("company").orderByChild("email").equalTo(sharedPreferencesEmail.getString("spEmail", ""))
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(Company::class.java)
                    nameUser = item?.name.toString()
                    image = item?.imageUrl.toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}