package com.invozone.paymat

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.invozone.paymat.databinding.FragmentLoginBinding


class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        binding.progressbar?.visible(false)
        addListener()

        binding.rlSignIn?.setOnClickListener {
            login()
        }
        binding.rlSignUp?.setOnClickListener {
            signUp()
        }
        binding.tvFp?.setOnClickListener {
            forgotView()
        }
        binding.serverSetupRl?.setOnClickListener {
            serverSetupView()
        }
    }

    private fun forgotView() {

    }

    private fun serverSetupView() {

    }

    private fun twoFAVerificationView() {

    }


    private fun addListener() {
        binding.etEmail?.addTextChangedListener(MyTextWatcher(binding.etEmail!!))
        binding.etPassword?.addTextChangedListener(MyTextWatcher(binding.etPassword!!))
    }

    private inner class MyTextWatcher :
        TextWatcher {
        private var view: View? = null

        constructor(view: View) {
            this.view = view
        }

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun afterTextChanged(editable: Editable) {
            when (view?.id) {
                R.id.etEmail -> validateEmail()
                R.id.etPassword -> validatePassword()
            }
        }
    }

    private fun signUp() {
    }

    private fun verification2FAView() {
    }

    private fun login() {
        if (!validateEmail() or !validatePassword()) {
            return
        }
        val email = binding.etEmail?.text.toString().trim()
        var password = binding.etPassword?.text.toString().trim()
        binding.progressbar.visible(true)
        Handler().postDelayed({
            binding.progressbar.visible(false)
            showToastMessage(requireContext(),"Login successfully!")
            findNavController().navigate(R.id.action_selection_fragment)
        }, 1000)



    }

    fun showToastMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    fun View.visible(isVisible: Boolean) {
        visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun validateEmail(): Boolean {
        val email = binding.etEmail?.text.toString().trim { it <= ' ' }
        return if (email.isEmpty()) {
            binding.emailInputLayout?.error = getString(R.string.required_email)
            false
        } else if (!isValidEmail(email)) {
            binding.emailInputLayout?.error = getString(R.string.valid_email)
            false
        } else {
            binding.emailInputLayout?.isErrorEnabled = false
            true
        }
    }

    fun isValidEmail(target: String?): Boolean {
        return if (target == null) false else android.util.Patterns.EMAIL_ADDRESS.matcher(target)
            .matches()

    }//validate email

    private fun validatePassword(): Boolean {
        val passwordInput = binding.etPassword?.text.toString().trim { it <= ' ' }
        return if (passwordInput.isEmpty()) {
            binding.passwordInputLayout?.error = getString(R.string.required_password)
            false
        } else {
            binding.passwordInputLayout?.isErrorEnabled = false
            true
        }
    } // validate password
}