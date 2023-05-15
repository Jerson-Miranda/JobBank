package com.example.jobbank.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.jobbank.R
import com.example.jobbank.databinding.FragmentHomeFeaturedBinding

class Home_Featured : Fragment() {

    private lateinit var binding: FragmentHomeFeaturedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeFeaturedBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

}