package com.example.jobbank.model.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.jobbank.view.fragment.HomeFeatured
import com.example.jobbank.view.fragment.HomeNotifications
import com.example.jobbank.view.fragment.HomePrincipal

class ViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> HomePrincipal()
            1 -> HomeNotifications()
            2 -> HomeFeatured()
            else -> HomePrincipal()
        }
    }

    override fun getCount(): Int {
        return 3
    }
}