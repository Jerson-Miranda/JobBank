package com.example.jobbank.view.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.jobbank.databinding.FragmentSignSucessfullBinding
import com.example.jobbank.view.BasicSettings
import com.example.jobbank.view.SignTypeUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


class SignSuccessfully : Fragment() {

    private lateinit var binding: FragmentSignSucessfullBinding
    private val database = FirebaseDatabase.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private lateinit var sharedPreferencesId: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignSucessfullBinding.inflate(inflater, container, false)
        val view = binding.root

        sharedPreferencesId = requireActivity().getSharedPreferences("sharedPreferencesId", Context.MODE_PRIVATE)

        binding.btnContinueSignSucessfull.setOnClickListener{
            image()
        }

        return view
    }

    private fun usersData(uri: String, uri2: String) {
        if (SignTypeUser.TypeUser.typeUser == "company"){
            val myRef = database.getReference("company").child(sharedPreferencesId.getInt("spId", 0).toString())
            val updates = hashMapOf<String, Any>(
                "imageUrl" to uri,
                "imageUrl2" to uri2
            )
            myRef.updateChildren(updates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(activity, BasicSettings::class.java))
                } else {
                    Toast.makeText(
                        activity,"We are sorry that your account was not created. Try again",
                        Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            val myRef = database.getReference("users").child(sharedPreferencesId.getInt("spId", 0).toString())
            val updates = hashMapOf<String, Any>(
                "imageUrl" to uri,
                "imageUrl2" to uri2
            )
            myRef.updateChildren(updates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(activity, BasicSettings::class.java))
                } else {
                    Toast.makeText(activity,"We are sorry that your account was not created. Try again", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun image(){
        val table = if (SignTypeUser.TypeUser.typeUser == "company"){
            "company"
        } else {
            "users"
        }
        val ref = storage.getReference(table).child(sharedPreferencesId.getInt("spId", 0).toString()).child("profile.png")
        ref.downloadUrl.addOnSuccessListener { uri ->
            val ref1 = storage.getReference(table).child(sharedPreferencesId.getInt("spId", 0).toString()).child("banner.png")
            ref1.downloadUrl.addOnSuccessListener { uri2 ->
                usersData(uri.toString(), uri2.toString())
            }.addOnFailureListener {
                Toast.makeText(requireActivity(), "Unknown error 2", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(requireActivity(), "Unknown error 1", Toast.LENGTH_SHORT).show()
        }
    }
}