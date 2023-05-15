package com.example.jobbank.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.jobbank.R
import com.example.jobbank.databinding.ActivityPresentationBinding

class Presentation : AppCompatActivity() {

    private lateinit var binding: ActivityPresentationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPresentationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLoginPresentation.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }

        binding.btnSignPresentation.setOnClickListener {
            startActivity(Intent(this, Sign_Type_User::class.java))
        }
    }
}