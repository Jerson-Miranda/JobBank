package com.example.jobbank.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.jobbank.R
import com.example.jobbank.databinding.ActivitySignupBinding
import com.example.jobbank.view.fragment.SignData

class Signup : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, SignData()).commit()
    }

}