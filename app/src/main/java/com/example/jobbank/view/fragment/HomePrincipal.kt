package com.example.jobbank.view.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobbank.databinding.FragmentHomePrincipalBinding
import com.example.jobbank.model.Company
import com.example.jobbank.model.Job
import com.example.jobbank.model.User
import com.example.jobbank.model.adapter.CompanyAdapter
import com.example.jobbank.model.adapter.JobAdapter
import com.example.jobbank.model.adapter.JobTouchListener
import com.example.jobbank.model.adapter.UserAdapter
import com.example.jobbank.view.Job_Details
import com.example.jobbank.view.JobRecommended
import com.example.jobbank.view.Profile
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomePrincipal : Fragment() {

    private lateinit var binding: FragmentHomePrincipalBinding
    val database = Firebase.database
    private lateinit var sharedPreferencesEmail: SharedPreferences
    private lateinit var sharedPreferencesId: SharedPreferences
    private lateinit var sharedPreferencesType: SharedPreferences

    private var jobRecommendedList = mutableListOf<Job>()
    private val usersList = mutableListOf<User>()
    private val companyList = mutableListOf<Company>()
    private val moreJobList = mutableListOf<Job>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomePrincipalBinding.inflate(inflater, container, false)
        val view = binding.root
        sharedPreferencesEmail = requireActivity().getSharedPreferences("sharedPreferencesEmail", Context.MODE_PRIVATE)
        sharedPreferencesId = requireActivity().getSharedPreferences("sharedPreferencesId", Context.MODE_PRIVATE)
        sharedPreferencesType = requireActivity().getSharedPreferences("sharedPreferencesType", Context.MODE_PRIVATE)

        visualize()
        visualizeUser()
        visualizeCompany()
        eventsClick()

        binding.btnShowHomePrincipal.setOnClickListener{
            startActivity(Intent(requireActivity(), JobRecommended::class.java))
        }

        binding.ibMessagingHomePrincipal.setOnClickListener{

        }

        return view
    }

    private fun toggleEmptyView(){
        if (binding.rvRecommendedHomePrincipal.adapter?.itemCount == 0) {
            binding.rvRecommendedHomePrincipal.visibility = View.GONE
            binding.ivNoRecommendedHomePrincipal.visibility = View.VISIBLE
            binding.tvNoRecommendedHomePrincipal.visibility = View.VISIBLE
        } else {
            binding.rvRecommendedHomePrincipal.visibility = View.VISIBLE
            binding.ivNoRecommendedHomePrincipal.visibility = View.GONE
            binding.tvNoRecommendedHomePrincipal.visibility = View.GONE
        }
    }

    private fun visualize(){
        if (sharedPreferencesType.getString("spType", "") != "company") {
            val ref2 = database.getReference("users").orderByChild("email").equalTo(sharedPreferencesEmail.getString("spEmail", ""))
            ref2.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val speciality = childSnapshot.child("speciality").getValue(String::class.java)
                        val ref = database.getReference("jobs").orderByChild("status").equalTo(true)
                        ref.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                jobRecommendedList.clear()
                                moreJobList.clear()
                                for (jobSnapshot in snapshot.children) {
                                    val item = jobSnapshot.getValue(Job::class.java)
                                    if (item?.createdBy != sharedPreferencesEmail.getString("spEmail", "").toString()) {
                                        if(item?.speciality == speciality) {
                                            jobRecommendedList.add(item!!)
                                        } else {
                                            moreJobList.add(item!!)
                                        }
                                    }
                                }
                                binding.rvRecommendedHomePrincipal.layoutManager = LinearLayoutManager(activity)
                                binding.rvRecommendedHomePrincipal.adapter = JobAdapter(jobRecommendedList, true)
                                binding.rvMoreHomePrincipal.layoutManager = LinearLayoutManager(activity)
                                binding.rvMoreHomePrincipal.adapter = JobAdapter(moreJobList, false)
                                toggleEmptyView()
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    private fun visualizeUser(){
        if (sharedPreferencesType.getString("spType", "") != "company") {
            val ref = database.getReference("users").orderByChild("email").equalTo(sharedPreferencesEmail.getString("spEmail", ""))
            ref.addListenerForSingleValueEvent (object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (outerChildSnapshot in snapshot.children) {
                        val address = outerChildSnapshot.child("address").getValue(String::class.java)
                        val ref2 = database.getReference("users").orderByChild("address").equalTo(address)
                        ref2.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(innerSnapshot: DataSnapshot) {
                                usersList.clear()
                                for (innerChildSnapshot in innerSnapshot.children) {
                                    val item = innerChildSnapshot.getValue(User::class.java)
                                    if (item?.email != sharedPreferencesEmail.getString("spEmail", "")) {
                                        usersList.add(item!!)
                                    }
                                }
                                binding.rvUsersHomePrincipal.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
                                binding.rvUsersHomePrincipal.adapter = UserAdapter(usersList, true)
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    private fun visualizeCompany(){
        if (sharedPreferencesType.getString("spType", "") != "company") {
            val ref = database.getReference("users").orderByChild("email").equalTo(sharedPreferencesEmail.getString("spEmail", ""))
            ref.addListenerForSingleValueEvent (object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val speciality = childSnapshot.child("speciality").getValue(String::class.java)
                        val ref2 = database.getReference("company").orderByChild("speciality").equalTo(speciality)
                        ref2.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                companyList.clear()
                                for (innerChildSnapshot in snapshot.children) {
                                    val item = innerChildSnapshot.getValue(Company::class.java)
                                    companyList.add(item!!)
                                }
                                binding.rvCompanyHomePrincipal.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
                                binding.rvCompanyHomePrincipal.adapter = CompanyAdapter(companyList, false)
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    private fun eventsClick(){
        binding.rvRecommendedHomePrincipal.addOnItemTouchListener(
            JobTouchListener(requireActivity(), binding.rvRecommendedHomePrincipal, object : JobTouchListener.ClickListener {
                override fun onClick(view: View?, position: Int) {
                    val bottomSheetDialogFragment = Job_Details().apply {
                        arguments = Bundle().apply {
                            putInt("selected", jobRecommendedList[position].id)
                        }
                    }
                    bottomSheetDialogFragment.show(requireParentFragment().parentFragmentManager, bottomSheetDialogFragment.tag)
                }
                override fun onLongClick(view: View?, position: Int) {}
            })
        )

        binding.rvMoreHomePrincipal.addOnItemTouchListener(
            JobTouchListener(requireActivity(), binding.rvMoreHomePrincipal, object : JobTouchListener.ClickListener {
                override fun onClick(view: View?, position: Int) {
                    val bottomSheetDialogFragment = Job_Details().apply {
                        arguments = Bundle().apply {
                            putInt("selected", moreJobList[position].id)
                        }
                    }
                    bottomSheetDialogFragment.show(requireParentFragment().parentFragmentManager, bottomSheetDialogFragment.tag)
                }
                override fun onLongClick(view: View?, position: Int) {}
            })
        )

        binding.rvUsersHomePrincipal.addOnItemTouchListener(
            JobTouchListener(requireActivity(), binding.rvUsersHomePrincipal, object : JobTouchListener.ClickListener {
                override fun onClick(view: View?, position: Int) {
                    val intent = Intent(requireActivity(), Profile::class.java).apply {
                        putExtra("selected", usersList[position].email)
                        putExtra("me", false)
                    }
                    startActivity(intent)
                }
                override fun onLongClick(view: View?, position: Int) {}
            })
        )

        binding.rvCompanyHomePrincipal.addOnItemTouchListener(
            JobTouchListener(requireActivity(), binding.rvCompanyHomePrincipal, object : JobTouchListener.ClickListener {
                override fun onClick(view: View?, position: Int) {
                    val intent = Intent(requireActivity(), Company::class.java).apply {
                        putExtra("selected", companyList[position].email)
                        putExtra("me", false)
                    }
                    startActivity(intent)
                }
                override fun onLongClick(view: View?, position: Int) {}
            })
        )
    }
}