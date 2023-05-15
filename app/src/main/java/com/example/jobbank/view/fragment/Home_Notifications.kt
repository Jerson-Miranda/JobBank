package com.example.jobbank.view.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.jobbank.databinding.FragmentHomeNotificationsBinding
import com.example.jobbank.model.Job
import com.example.jobbank.model.Notification
import com.example.jobbank.model.adapter.JobAdapter
import com.example.jobbank.model.adapter.JobTouchListener
import com.example.jobbank.model.adapter.NotificationAdapter
import com.example.jobbank.view.Job_Details
import com.example.jobbank.view.Profile
import com.example.jobbank.view.User_Details
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class Home_Notifications : Fragment() {

    private lateinit var binding: FragmentHomeNotificationsBinding
    private val database = Firebase.database
    private lateinit var sharedPreferencesEmail: SharedPreferences
    private val notificationList = mutableListOf<Notification>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeNotificationsBinding.inflate(inflater, container, false)
        val view = binding.root

        sharedPreferencesEmail = requireActivity().getSharedPreferences("sharedPreferencesEmail", Context.MODE_PRIVATE)
        visualize()
        eventsClick()

        return view
    }

    private fun toggleEmptyView(){
        if (binding.rvHomeNotifications.adapter?.itemCount == 0) {
            binding.rvHomeNotifications.visibility = View.GONE
            binding.ivNoNotificationHomeNotifications.visibility = View.VISIBLE
            binding.tvNotHomeNotifications.visibility = View.VISIBLE
        } else {
            binding.rvHomeNotifications.visibility = View.VISIBLE
            binding.ivNoNotificationHomeNotifications.visibility = View.GONE
            binding.tvNotHomeNotifications.visibility = View.GONE
        }
    }

    private fun visualize(){
        val ref = database.getReference("notifications")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notificationList.clear()
                for (jobSnapshot in snapshot.children) {
                    val item = jobSnapshot.getValue(Notification::class.java)
                    if(item?.recipient == sharedPreferencesEmail.getString("spEmail", ""))
                        notificationList.add(item!!)
                }
                binding.rvHomeNotifications.layoutManager = LinearLayoutManager(activity)
                binding.rvHomeNotifications.adapter = NotificationAdapter(notificationList)
                toggleEmptyView()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun eventsClick(){
        binding.rvHomeNotifications.addOnItemTouchListener(
            JobTouchListener(requireActivity(), binding.rvHomeNotifications, object : JobTouchListener.ClickListener {
                override fun onClick(view: View?, position: Int) {
                    val bottomSheetDialogFragment = User_Details().apply {
                        arguments = Bundle().apply {
                            putString("selected", notificationList[position].sender)
                        }
                    }
                    bottomSheetDialogFragment.show(requireFragmentManager(), bottomSheetDialogFragment.tag)
                }
                override fun onLongClick(view: View?, position: Int) {}
            })
        )

    }
}