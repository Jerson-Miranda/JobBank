package com.example.jobbank

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.navigation.NavigationView

class CustomNavigationView(context: Context, attrs: AttributeSet) : NavigationView(context, attrs) {

    override fun performClick(): Boolean {
        return super.performClick()
    }
}