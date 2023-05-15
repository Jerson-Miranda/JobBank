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
import com.example.jobbank.model.Company
import com.example.jobbank.model.User
import com.example.jobbank.view.BasicSettings
import com.example.jobbank.view.Sign_Type_User
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


class Sign_Sucessfull : Fragment() {

    private lateinit var binding: FragmentSignSucessfullBinding
    val database = FirebaseDatabase.getInstance()
    val storage = FirebaseStorage.getInstance()
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
        if (Sign_Type_User.TypeUser.typeUser == "company"){
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
        var table = ""
        table = if (Sign_Type_User.TypeUser.typeUser == "company"){
            "company"
        } else {
            "users"
        }
        val ref = storage.getReference(table).child(sharedPreferencesId.getInt("spId", 0).toString()).child("profile.png")
        ref.downloadUrl.addOnSuccessListener { uri ->
            val ref = storage.getReference(table).child(sharedPreferencesId.getInt("spId", 0).toString()).child("banner.png")
            ref.downloadUrl.addOnSuccessListener { uri2 ->
                usersData(uri.toString(), uri2.toString())
            }.addOnFailureListener { exception ->
                Toast.makeText(requireActivity(), "Unknown error 2", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(requireActivity(), "Unknown error 1", Toast.LENGTH_SHORT).show()
        }
    }
}