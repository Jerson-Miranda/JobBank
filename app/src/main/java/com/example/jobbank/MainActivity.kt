package com.example.jobbank

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Pair
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.example.jobbank.databinding.ActivityMainBinding
import com.example.jobbank.view.Home
import com.example.jobbank.view.Login
import com.example.jobbank.view.Presentation
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var animation: Animation
    private lateinit var sharedPreferencesLogin: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Animation
        animation = AnimationUtils.loadAnimation(this, R.anim.splash_screem)
        animation.duration = 1500
        binding.ivLogo.setAnimation(animation)

        sharedPreferencesLogin = getSharedPreferences("sharedPreferencesLogin", Context.MODE_PRIVATE)

        Handler().postDelayed({
            if (!sharedPreferencesLogin.getBoolean("spLogin", false)) {
                startActivity(Intent(this, Presentation::class.java))
                finish()
            } else {
                startActivity(Intent(this, Home::class.java))
                finish()
            }
        }, 3000)
    }

    //Development: Jerson Rai Miranda Diaz

}