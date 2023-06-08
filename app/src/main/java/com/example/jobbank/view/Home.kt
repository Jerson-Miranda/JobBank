package com.example.jobbank.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GestureDetectorCompat
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.jobbank.R
import com.example.jobbank.databinding.ActivityHomeBinding
import com.example.jobbank.model.adapter.BottomNavigationBehavior
import com.example.jobbank.model.adapter.ViewPagerAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class Home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    //SharedPreferences
    private lateinit var sharedPreferencesEmail: SharedPreferences
    private lateinit var sharedPreferencesLogin: SharedPreferences
    private lateinit var sharedPreferencesId: SharedPreferences
    private lateinit var sharedPreferencesType: SharedPreferences
    //Firebase Storage
    private var storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesEmail = getSharedPreferences("sharedPreferencesEmail", Context.MODE_PRIVATE)
        sharedPreferencesLogin = getSharedPreferences("sharedPreferencesLogin", Context.MODE_PRIVATE)
        sharedPreferencesId = getSharedPreferences("sharedPreferencesId", Context.MODE_PRIVATE)
        sharedPreferencesType = getSharedPreferences("sharedPreferencesType", Context.MODE_PRIVATE)

        getPhotoDrawer()

        binding.floatingActionButton.setOnClickListener{
            startActivity(Intent(this, Job_Add::class.java))
        }

        binding.btnSignoutHome.setOnClickListener {
            val editor = sharedPreferencesEmail.edit()
            editor.remove("spEmail")
            editor.apply()
            val editor2 = sharedPreferencesLogin.edit()
            editor2.remove("spLogin")
            editor2.apply()
            val editor3 = sharedPreferencesId.edit()
            editor3.remove("spId")
            editor3.apply()
            val editor4 = sharedPreferencesType.edit()
            editor4.remove("spType")
            editor4.apply()
            startActivity(Intent(this, Presentation::class.java))
        }

        //Swipe to navigate the BottomNavigationView
        binding.viewPager.adapter = ViewPagerAdapter(supportFragmentManager)
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                binding.homeNavigation.menu.getItem(position).isChecked = true
                if(position == 0){
                    typeUser()
                } else {
                    binding.floatingActionButton.visibility = View.INVISIBLE
                }
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
        })

        binding.homeNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    binding.viewPager.currentItem = 0
                    typeUser()
                    true
                }
                R.id.nav_notifications -> {
                    binding.viewPager.currentItem = 1
                    binding.floatingActionButton.visibility = View.INVISIBLE
                    true
                }
                R.id.nav_featured -> {
                    binding.viewPager.currentItem = 2
                    binding.floatingActionButton.visibility = View.INVISIBLE
                    true
                }
                else -> false
            }
        }

        val layoutParams = binding.homeNavigation.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.behavior = BottomNavigationBehavior()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)
        val btn = headerView.findViewById<MaterialButton>(R.id.btnProfile_drawer_header)

        btn.setOnClickListener {
            if (sharedPreferencesType.getString("spType", "") == "company"){
                val intent = Intent(this, Company::class.java).apply {
                    putExtra("me", true)
                }
                startActivity(intent)
            } else {
                val intent = Intent(this, Profile::class.java).apply {
                    putExtra("me", true)
                }
                startActivity(intent)
            }
        }

        val gestureDetector = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (e1.x < e2.x) {
                    binding.drawerLayout.openDrawer(binding.navView)
                } else if (e1.x > e2.x) {
                    binding.drawerLayout.closeDrawer(binding.navView)
                }
                return super.onFling(e1, e2, velocityX, velocityY)
            }
        })
        binding.navView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    private fun typeUser(){
        if (sharedPreferencesType.getString("spType", "") != "company"){
            binding.floatingActionButton.visibility = View.GONE
        } else {
            binding.floatingActionButton.visibility = View.VISIBLE
        }

    }

    @Deprecated("Use moveTaskToBack() instead", replaceWith = ReplaceWith("moveTaskToBack(true)"))
    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    private fun getPhotoDrawer() {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)
        val photo = headerView.findViewById<ImageView>(R.id.ivProfile_drawer_header)
        val username = headerView.findViewById<TextView>(R.id.tvName_drawer_header)
        val email = headerView.findViewById<TextView>(R.id.tvEmail_drawer_header)

        val table = if (sharedPreferencesType.getString("spType", "") == "company"){
            "company"
        } else {
            "users"
        }
        val ref = FirebaseDatabase.getInstance().getReference(table).orderByChild("email").equalTo(sharedPreferencesEmail.getString("spEmail", ""))
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (childSnapshot in snapshot.children) {
                        if (sharedPreferencesType.getString("spType", "") == "company"){
                            username.text = childSnapshot.child("name").getValue(String::class.java)
                        } else {
                            username.text = getString(R.string.username_format, childSnapshot.child("firstName").getValue(String::class.java), childSnapshot.child("lastName").getValue(String::class.java))
                        }
                        email.text = childSnapshot.child("email").getValue(String::class.java)
                    }
                }

            }
            override fun onCancelled(error: DatabaseError) {}
        })

        val profile = storage.getReference("${table}/${sharedPreferencesId.getInt("spId", 0)}/profile.png")
        val localFile = File.createTempFile("images1", "jpg")
        profile.getFile(localFile)
            .addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                Glide.with(this)
                    .load(bitmap)
                    .centerCrop()
                    .into(photo)
            }
            .addOnFailureListener { }
    }
}

