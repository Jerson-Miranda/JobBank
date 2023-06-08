package com.example.jobbank.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.jobbank.databinding.ActivitySignTypeUserBinding

class SignTypeUser : AppCompatActivity() {

    object TypeUser {
        lateinit var typeUser: String
    }

    private lateinit var binding: ActivitySignTypeUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignTypeUserBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.cvWorker.setOnClickListener{
            selected("worker")
        }

        binding.cvStudent.setOnClickListener{
            selected("student")
        }

        binding.cvCompany.setOnClickListener{
            selected("company")
        }

    }

    private fun selected(type: String){
        TypeUser.typeUser = type
        startActivity(Intent(this, Signup::class.java))
    }
}