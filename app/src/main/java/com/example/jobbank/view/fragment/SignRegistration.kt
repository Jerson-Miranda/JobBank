package com.example.jobbank.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.jobbank.R
import com.example.jobbank.databinding.FragmentSignRegistrationBinding
import com.example.jobbank.view.Login
import com.google.firebase.auth.*

class SignRegistration : Fragment() {

    object Registration {
        lateinit var email: String
        lateinit var phone: String
        lateinit var cPassword: String
    }

    private lateinit var binding: FragmentSignRegistrationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignRegistrationBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnContinueSignRegistration.setOnClickListener{
            registration()
        }

        return view
    }

    private fun registration(){
        if(!validateEmail() || !validatePhone() || !validatePassword() || !validateCPassword()) {
            return
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.etEmailSignRegistration.text.toString(), binding.etPasswordSignRegistration.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sendVerificationEmail()
                } else {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthUserCollisionException) {
                        Toast.makeText(requireActivity(), "Registered account. Log in", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(requireActivity(), Login::class.java))
                    } catch (e: Exception) {
                        Toast.makeText(requireActivity(), "Unknown error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun sendVerificationEmail() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Registration.email = binding.etEmailSignRegistration.text.toString()
                    Registration.phone = binding.etPhoneSignRegistration.text.toString()
                    Registration.cPassword = binding.etCPasswordSignRegistration.text.toString()
                    val transaction = requireParentFragment().parentFragmentManager.beginTransaction()
                    transaction.setCustomAnimations(
                        R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right
                    )
                    transaction.replace(R.id.fragment_container, SignVerification())
                    transaction.addToBackStack(null)
                    transaction.commit()
                } else {
                    Toast.makeText(
                        requireActivity(), "Error sending verification email", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validateEmail(): Boolean {
        val email = binding.etEmailSignRegistration.text.toString().trim()
        if (email.isEmpty()) {
            binding.etEmailSignRegistration.error = "Enter your email"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmailSignRegistration.error = "Enter a valid email"
        } else {
            return true
        }
        return false
    }

    private fun validatePhone(): Boolean {
        val phone = binding.etPhoneSignRegistration.text.toString().trim()
        if (phone.isEmpty()) {
            binding.etPhoneSignRegistration.error = "Enter your phone"
        } else if (!Patterns.PHONE.matcher(phone).matches()) {
            binding.etPhoneSignRegistration.error = "Enter a valid phone"
        } else {
            return true
        }
        return false
    }

    private fun validatePassword(): Boolean {
        val password = binding.etPasswordSignRegistration.text.toString().trim()
        val passwordPattern = ".*".toRegex()
        if (password.isEmpty()) {
            binding.etPasswordSignRegistration.error = "Enter your password"
        } else if (!passwordPattern.matches(password)) {
            binding.etPasswordSignRegistration.error = "Enter a valid password"
        } else {
            return true
        }
        return false
    }

    private fun validateCPassword(): Boolean {
        val password = binding.etCPasswordSignRegistration.text.toString().trim()
        val passwordPattern = ".*".toRegex()
        if (password.isEmpty()) {
            binding.etCPasswordSignRegistration.error = "Enter your password"
        } else if (!passwordPattern.matches(password)) {
            binding.etCPasswordSignRegistration.error = "Enter a valid password"
        } else {
            return true
        }
        return false
    }

}