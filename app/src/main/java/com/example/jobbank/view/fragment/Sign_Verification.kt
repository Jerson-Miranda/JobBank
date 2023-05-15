package com.example.jobbank.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.jobbank.R
import com.example.jobbank.databinding.FragmentSignVerificationBinding
import com.example.jobbank.model.Company
import com.example.jobbank.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.example.jobbank.view.fragment.Sign_Data.Data
import com.example.jobbank.view.fragment.Sign_Data_Job.DataJob
import com.example.jobbank.view.Sign_Type_User.TypeUser
import com.example.jobbank.view.fragment.Sign_Registration.Registration
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class Sign_Verification : Fragment() {

    object Verification {
        lateinit var id: String
    }

    private lateinit var binding: FragmentSignVerificationBinding
    //SharedPreferences
    private lateinit var sharedPreferencesLogin: SharedPreferences
    private lateinit var sharedPreferencesEmail: SharedPreferences
    private lateinit var sharedPreferencesId: SharedPreferences
    private lateinit var sharedPreferencesType: SharedPreferences
    //FIrebase Instances
    val database = FirebaseDatabase.getInstance()
    val storage = FirebaseStorage.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignVerificationBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.tvSubtitleSignVerification.text = "Hi " + Data.firstName + "!"
        sharedPreferencesLogin = requireActivity().getSharedPreferences("sharedPreferencesLogin", Context.MODE_PRIVATE)
        sharedPreferencesEmail = requireActivity().getSharedPreferences("sharedPreferencesEmail", Context.MODE_PRIVATE)
        sharedPreferencesId = requireActivity().getSharedPreferences("sharedPreferencesId", Context.MODE_PRIVATE)
        sharedPreferencesType = requireActivity().getSharedPreferences("sharedPreferencesType", Context.MODE_PRIVATE)
        binding.pbWaitSignVerification.visibility = View.GONE

        binding.btnContinueSignVerification.setOnClickListener{
            binding.pbWaitSignVerification.visibility = View.VISIBLE
            tokenNotification()
            verify()
        }
        return view
    }

    private fun verify() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.reload()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (user.isEmailVerified) {
                    setValue()
                } else {
                    Toast.makeText(requireActivity(), "Unverified email", Toast.LENGTH_SHORT).show()
                    binding.pbWaitSignVerification.visibility = View.GONE
                }
            } else {
                Toast.makeText(requireActivity(), "Verification time expired", Toast.LENGTH_SHORT).show()
                binding.pbWaitSignVerification.visibility = View.GONE
            }
        }
    }

    private fun setValue(){
        if(TypeUser.typeUser == "company"){
            val myRef = database.getReference("company").orderByKey().limitToLast(1)
            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val id = dataSnapshot.children.last().child("id").getValue(Int::class.java)
                    usersData(id!!)
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Unknown error", Toast.LENGTH_SHORT).show()
                    binding.pbWaitSignVerification.visibility = View.GONE
                }
            })
        } else {
            val myRef = database.getReference("users").orderByKey().limitToLast(1)
            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val id = dataSnapshot.children.last().child("id").getValue(Int::class.java)
                    usersData(id!!)
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Unknown error", Toast.LENGTH_SHORT).show()
                    binding.pbWaitSignVerification.visibility = View.GONE
                }
            })
        }
    }

    private fun usersData(id: Int) {
        //Registrar Usuario
        if (TypeUser.typeUser == "company"){
            val myRef = database.getReference("company").child(id.plus(1).toString())
            myRef.setValue(dataCompany(id.plus(1))).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storageData(id)
                } else {
                    Toast.makeText(activity,"We are sorry that your account was not created. Try again", Toast.LENGTH_SHORT).show()
                    binding.pbWaitSignVerification.visibility = View.GONE
                }
            }
        } else {
            val myRef = database.getReference("users").child(id.plus(1).toString())
            myRef.setValue(dataUser(id.plus(1))).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storageData(id)
                } else {
                    Toast.makeText(activity,"We are sorry that your account was not created. Try again", Toast.LENGTH_SHORT).show()
                    binding.pbWaitSignVerification.visibility = View.GONE
                }
            }
        }
    }

    private fun storageData(id: Int){
        //Registrar profile del usuario
        var table = ""
        table = if (TypeUser.typeUser == "company"){
            "company"
        } else {
            "users"
        }
        val uri = Uri.parse("android.resource://" + requireActivity().packageName + "/" + R.mipmap.profile)
        val fileRef = storage.getReference("${table}/${id.plus(1)}/profile.png")
        fileRef.putFile(uri)
            .addOnSuccessListener {
                storageData2(id)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Unknown error", Toast.LENGTH_SHORT).show()
                binding.pbWaitSignVerification.visibility = View.GONE
            }
    }

    private fun storageData2(id: Int){
        //Registrar banner del usuario
        var table = ""
        table = if (TypeUser.typeUser == "company"){
            "company"
        } else {
            "users"
        }
        val uri = Uri.parse("android.resource://" + requireActivity().packageName + "/" + R.mipmap.banner)
        val fileRef = storage.getReference("${table}/${id.plus(1)}/banner.png")
        fileRef.putFile(uri)
            .addOnSuccessListener {
                Verification.id = id.plus(1).toString()
                sharedPreferencesLogin.edit().putBoolean("spLogin", true).apply()
                sharedPreferencesEmail.edit().putString("spEmail", Sign_Registration.Registration.email).apply()
                sharedPreferencesId.edit().putInt("spId", id.plus(1)).apply()
                sharedPreferencesType.edit().putString("spType", TypeUser.typeUser).apply()
                binding.pbWaitSignVerification.visibility = View.GONE
                val fragment = Sign_Sucessfull()
                val transaction = requireFragmentManager().beginTransaction()
                transaction.setCustomAnimations(
                    R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right
                )
                transaction.replace(R.id.fragment_container, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Unknown error", Toast.LENGTH_SHORT).show()
                binding.pbWaitSignVerification.visibility = View.GONE
            }
    }

    private fun tokenNotification(): String {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            binding.tvTokenSignVerification.text = task.result
        })
        return ""
    }

    private fun dataUser(id: Int): User{
        val data = User(
            id,
            Data.firstName,
            Data.lastName,
            Registration.email,
            Registration.cPassword,
            Registration.phone,
            TypeUser.typeUser ,
            DataJob.speciality ,
            DataJob.schooljob ,
            DataJob.location,
            "0" ,
            true ,
            binding.tvTokenSignVerification.text.toString(),
            "",
            ""
        )
        return data
    }

    private fun dataCompany(id: Int): Company {
        val data = Company(
            id,
            Data.firstName,
            Registration.email,
            Registration.cPassword,
            Registration.phone,
            TypeUser.typeUser ,
            DataJob.speciality ,
            DataJob.website ,
            DataJob.location,
            "0" ,
            true ,
            binding.tvTokenSignVerification.text.toString(),
            "",
            ""
        )
        return data
    }

}