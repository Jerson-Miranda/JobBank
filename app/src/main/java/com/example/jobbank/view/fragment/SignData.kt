package com.example.jobbank.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.jobbank.R
import com.example.jobbank.databinding.FragmentSignDataBinding
import com.example.jobbank.view.SignTypeUser.TypeUser

class SignData : Fragment() {

    object Data {
        lateinit var firstName: String
        lateinit var lastName: String
    }

    private lateinit var binding: FragmentSignDataBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignDataBinding.inflate(inflater, container, false)
        val view = binding.root

        typeUser()

        binding.btnContinueSignFullname.setOnClickListener{
            getData()
        }

        return view
    }

    private fun typeUser(){
        if(TypeUser.typeUser == "company"){
            binding.etLastNameSignFullname.visibility = View.GONE
            binding.etLastNameSignFullname.setText(R.string.nulll)
            binding.tvSubtitlePresentation.setText(R.string.lbCompany)
            binding.etFirstNameSignFullname.hint = "Company name"
        } else {
            binding.etLastNameSignFullname.visibility = View.VISIBLE
        }
    }

    private fun getData() {
        if(!validateFirstName() || !validateLastName()) {
            return
        }
        Data.firstName = binding.etFirstNameSignFullname.text.toString()
        Data.lastName = binding.etLastNameSignFullname.text.toString()
        val transaction = requireParentFragment().parentFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right
        )
        transaction.replace(R.id.fragment_container, SignDataJob())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun validateFirstName(): Boolean {
        val firstName = binding.etFirstNameSignFullname.text.toString().trim()
        val firstNamePattern = "[a-zA-ZñáéíóúÁÉÍÓÚüÜ\\s-]+".toRegex()
        if (firstName.isEmpty()) {
            binding.etFirstNameSignFullname.error = "Enter your first name"
        } else if (!firstName.matches(firstNamePattern)) {
            binding.etFirstNameSignFullname.error = "Enter your valid first name"
        } else {
            return true
        }
        return false
    }

    private fun validateLastName(): Boolean {
        val lastName = binding.etLastNameSignFullname.text.toString().trim()
        val lastNamePattern = "[a-zA-ZñáéíóúÁÉÍÓÚüÜ\\s-]+".toRegex()
        if (lastName.isEmpty()) {
            binding.etLastNameSignFullname.error = "Enter your last name"
        } else if (!lastName.matches(lastNamePattern)) {
            binding.etLastNameSignFullname.error = "Enter your valid last name"
        } else {
            return true
        }
        return false
    }
}