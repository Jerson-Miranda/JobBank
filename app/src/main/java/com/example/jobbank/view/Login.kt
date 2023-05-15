package com.example.jobbank.view

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.jobbank.databinding.ActivityLoginBinding
import com.example.jobbank.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferencesLogin: SharedPreferences
    private lateinit var sharedPreferencesEmail: SharedPreferences
    private lateinit var sharedPreferencesId: SharedPreferences
    private lateinit var sharedPreferencesType: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesLogin = getSharedPreferences("sharedPreferencesLogin", Context.MODE_PRIVATE)
        sharedPreferencesEmail = getSharedPreferences("sharedPreferencesEmail", Context.MODE_PRIVATE)
        sharedPreferencesId = getSharedPreferences("sharedPreferencesId", Context.MODE_PRIVATE)
        sharedPreferencesType = getSharedPreferences("sharedPreferencesType", Context.MODE_PRIVATE)

        binding.btnLoginLogin.setOnClickListener{
            login()
        }
        binding.tvSignupPresentation.setOnClickListener{
            startActivity(Intent(this@Login, Sign_Type_User::class.java))
        }
    }

    private fun login() {
        if(!validateEmail() || !validatePassword()) {
            return
        }
        val emailRef = Firebase.database.getReference("users").orderByChild("email").equalTo(binding.etUsernameLogin.text.toString())
        emailRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    sucess(snapshot)
                } else {
                    val ref = Firebase.database.getReference("company").orderByChild("email").equalTo(binding.etUsernameLogin.text.toString())
                    ref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                sucess(snapshot)
                            } else {
                                startActivity(Intent(this@Login, Sign_Type_User::class.java))
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun sucess(snapshot: DataSnapshot){
        for (childSnapshot in snapshot.children) {
            val id = childSnapshot.child("id").getValue(Int::class.java)
            val pass = childSnapshot.child("password").getValue(String::class.java)
            val type = childSnapshot.child("typeUser").getValue(String::class.java)
            if (pass == binding.etPasswordLogin.text.toString()){
                sharedPreferencesEmail.edit().putString("spEmail", binding.etUsernameLogin.text.toString()).apply()
                sharedPreferencesId.edit().putInt("spId", id!!).apply()
                sharedPreferencesLogin.edit().putBoolean("spLogin", true).apply()
                sharedPreferencesType.edit().putString("spType", type).apply()
                startActivity(Intent(this@Login, Home::class.java))
            } else {
                Toast.makeText(this@Login, "Incorrect data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateEmail(): Boolean {
        val email = binding.etUsernameLogin.text.toString().trim()
        if (email.isEmpty()) {
            binding.etUsernameLogin.error = "Enter your email"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etUsernameLogin.error = "Enter a valid email"
        } else {
            return true
        }
        return false
    }

    private fun validatePassword(): Boolean {
        val password = binding.etPasswordLogin.text.toString().trim()
        val passwordPattern = ".*".toRegex()
        if (password.isEmpty()) {
            binding.etPasswordLogin.error = "Enter your password"
        } else if (!passwordPattern.matches(password)) {
            binding.etPasswordLogin.error = "Enter a valid password"
        } else {
            return true
        }
        return false
    }
}