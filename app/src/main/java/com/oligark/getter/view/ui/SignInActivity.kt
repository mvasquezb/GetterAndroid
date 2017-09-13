package com.oligark.getter.view.ui

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import android.widget.Toast

import com.oligark.getter.R
import com.oligark.getter.databinding.ActivitySigninBinding
import com.oligark.getter.viewmodel.SignInViewModel

class SignInActivity : LifecycleActivity() {

    companion object {
        @JvmField
        val TAG = SignInActivity::class.java.simpleName
    }

    private lateinit var binding: ActivitySigninBinding
    private lateinit var viewModel: SignInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signin)
        viewModel = ViewModelProviders.of(this).get(SignInViewModel::class.java)
    }

    fun forgottenPassword(view: View) {
        // Redirect to forgotten password screen
    }

    fun registerUser(view: View) {
        // Redirect to user registration screen
    }

    fun guestLogin(view: View) {
        // Anonymous user login
    }

    fun emailLogin(view: View) {
        doLogin(SignInViewModel.SIGNIN_PROVIDER.EMAIL)
    }

    fun fbLogin(view: View) {
        doLogin(SignInViewModel.SIGNIN_PROVIDER.FACEBOOK)
    }

    fun twitterLogin(view: View) {
        doLogin(SignInViewModel.SIGNIN_PROVIDER.TWITTER)
    }

    fun googleLogin(view: View) {
        doLogin(SignInViewModel.SIGNIN_PROVIDER.GOOGLE)
    }

    private fun doLogin(providerType: SignInViewModel.SIGNIN_PROVIDER) {
        val user = binding.loginUserInput.text.toString()
        val pass = binding.loginPasswordInput.text.toString()
        doLogin(providerType, user, pass)
    }

    private fun doLogin(
            providerType: SignInViewModel.SIGNIN_PROVIDER,
            user: String = "",
            pass: String = ""
    ) {
        Toast.makeText(this, "$user - $pass - $providerType", Toast.LENGTH_SHORT).show()
        viewModel.doLogin(providerType, user, pass)
    }
}
