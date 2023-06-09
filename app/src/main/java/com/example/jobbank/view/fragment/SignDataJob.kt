package com.example.jobbank.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.jobbank.R
import com.example.jobbank.databinding.FragmentSignDataJobBinding
import com.example.jobbank.view.SignTypeUser.TypeUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignDataJob : Fragment() {

    object DataJob {
        lateinit var schoolJob: String
        lateinit var speciality: String
        lateinit var website: String
        lateinit var location: String
    }

    private lateinit var binding: FragmentSignDataJobBinding
    private var schoolJobList = ArrayList<String>()
    private var specialityList = ArrayList<String>()
    private var locationList = ArrayList<String>()
    private val database = FirebaseDatabase.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignDataJobBinding.inflate(inflater, container, false)
        val view = binding.root

        schoolJobData()
        specialityData()
        locationData()
        typeUser()

        binding.btnContinueSignDataJob.setOnClickListener{
            if(!validateWebsite()) {
                return@setOnClickListener
            }
            DataJob.schoolJob = binding.sSchoolJobSignDataJob.selectedItem.toString()
            DataJob.speciality = binding.sSpecialitySignDataJob.selectedItem.toString()
            DataJob.website = binding.etWebsiteSignDataJob.text.toString()
            DataJob.location = binding.sLocationSignDataJob.selectedItem.toString()
            val transaction = requireParentFragment().parentFragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right
            )
            transaction.replace(R.id.fragment_container, SignRegistration())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return view
    }

    private fun typeUser(){
        if(TypeUser.typeUser == "company"){
            binding.etWebsiteSignDataJob.visibility = View.VISIBLE
        } else {
            binding.sSchoolJobSignDataJob.visibility = View.VISIBLE
            binding.tv2DataJob.visibility = View.VISIBLE
            binding.etWebsiteSignDataJob.setText(R.string.nulll)
            if(TypeUser.typeUser == "student"){
                binding.tv2DataJob.setText(R.string.lbUniversity)
            } else {
                binding.tv2DataJob.setText(R.string.lbLastCompany)
            }
        }
    }

    private fun validateWebsite(): Boolean {
        val website = binding.etWebsiteSignDataJob.text.toString().trim()
        if (website.isEmpty()) {
            binding.etWebsiteSignDataJob.error = "Enter your website"
        } else {
            return true
        }
        return false
    }

    private fun schoolJobData() {
        schoolJobList.clear()
        val table = if (TypeUser.typeUser == "student") {
            "school"
        } else {
            "business"
        }
        val ref = database.getReference("dataapp").child(table).orderByValue()
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val value = snapshot.getValue(String::class.java)
                    schoolJobList.add(value!!)
                }
                val adapter = ArrayAdapter(requireActivity(), R.layout.item_spinner_dropdown, schoolJobList)
                adapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
                binding.sSchoolJobSignDataJob.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun specialityData(){
        specialityList.clear()
        val ref = database.getReference("dataapp").child("speciality").orderByValue()
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val value = snapshot.getValue(String::class.java)
                    specialityList.add(value!!)
                }
                val adapter = ArrayAdapter(requireActivity(), R.layout.item_spinner_dropdown, specialityList)
                adapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
                binding.sSpecialitySignDataJob.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun locationData(){
        locationList.clear()
        val ref = database.getReference("dataapp").child("joblocation").orderByValue()
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val value = snapshot.getValue(String::class.java)
                    locationList.add(value!!)
                }
                val adapter = ArrayAdapter(requireActivity(), R.layout.item_spinner_dropdown, locationList)
                adapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
                binding.sLocationSignDataJob.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

}