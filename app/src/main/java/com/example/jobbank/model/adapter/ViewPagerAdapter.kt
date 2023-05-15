package com.example.jobbank.model.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.jobbank.view.fragment.Home_Featured
import com.example.jobbank.view.fragment.Home_Notifications
import com.example.jobbank.view.fragment.Home_Principal

class ViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> Home_Principal()
            1 -> Home_Notifications()
            2 -> Home_Featured()
            else -> Home_Principal()
        }
    }

    override fun getCount(): Int {
        return 3
    }
}