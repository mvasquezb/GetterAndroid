package com.oligark.getter.view.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.login.LoginManager
import com.google.firebase.auth.*

import com.oligark.getter.R
import com.oligark.getter.databinding.ActivitySigninBinding
import com.oligark.getter.viewmodel.SignInViewModel
import com.oligark.getter.viewmodel.SignInViewModel.Companion.RC_GOOGLE_SIGN_IN
import com.oligark.getter.viewmodel.resources.SignInStateResource
import com.oligark.getter.util.animate

class SignInActivity: AppCompatActivity() {

    companion object {
        @JvmField
        val TAG = SignInActivity::class.java.simpleName
        @JvmField
        val AUTH_CREDENTIAL_KEY = "auth_credential"
    }

    private lateinit var binding: ActivitySigninBinding
    private lateinit var signInViewModel: SignInViewModel
    private var credential: AuthCredential? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signin)
        signInViewModel = ViewModelProviders.of(this).get(SignInViewModel::class.java)

        binding.loginBtn.setOnClickListener { emailLogin() }
        binding.loginFbBtn.setOnClickListener { fbLogin() }
        binding.loginTwitterBtn.setOnClickListener { twitterLogin() }
        binding.loginGoogleBtn.setOnClickListener { googleLogin() }
        binding.loginRegisterBtn.setOnClickListener { registerUser() }
        binding.loginGuestBtn.setOnClickListener { guestLogin() }
        binding.loginForgottenPassword.setOnClickListener { forgottenPassword() }

        if (FirebaseAuth.getInstance().currentUser != null) {
            launchMainActivity()
            return
        }

        signInViewModel.init()
        signInViewModel.signInState.observe(this, Observer { signInState ->
            when(signInState?.signInState) {
                SignInStateResource.SignInState.LOADING -> {
                    showLoading()
                }
                SignInStateResource.SignInState.ERROR -> {
                    Log.e(TAG, "OnLoginError")
                    onLoginComplete()
                    onLoginError()
                }
                SignInStateResource.SignInState.SUCCESS -> {
                    onLoginComplete()
                    onLoginSuccess()
                }
                SignInStateResource.SignInState.CANCELLED -> {
                    onLoginComplete()
                    onLoginCancelled()
                }
                SignInStateResource.SignInState.FACEBOOK_PROMPT -> {
                    facebookPrompt(signInState.permissions)
                }
                SignInStateResource.SignInState.GOOGLE_PROMPT -> {
                    googlePrompt(signInState.permissions)
                }
                SignInStateResource.SignInState.TWITTER_PROMPT -> {
                    twitterPrompt(signInState.permissions)
                }
                SignInStateResource.SignInState.USER_COLLISION -> {
                    onLoginComplete()
                    handleUserCollision()
                }
                SignInStateResource.SignInState.INVALID_USER -> {
                    onLoginComplete()
                    handleInvalidUser()
                }
                else -> {}
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "request code: $requestCode. result code: $resultCode")
        signInViewModel.onActivityResult(requestCode, resultCode, data)
    }

    private fun launchMainActivity() {
        Log.d(TAG, "LaunchMainActivity")
        val mainIntent = Intent(applicationContext, MainActivity::class.java)
        mainIntent.putExtra(AUTH_CREDENTIAL_KEY, credential)
        startActivity(mainIntent)
        finish()
    }

    private fun onLoginSuccess() {
        launchMainActivity()
    }

    private fun onLoginComplete() {
        hideLoading()
    }

    private fun onLoginError() {
        Toast.makeText(this, getString(R.string.login_error_message), Toast.LENGTH_SHORT).show()
    }

    private fun onLoginCancelled() = onLoginComplete()

    private fun handleUserCollision() {
        Toast.makeText(
                this,
                getString(R.string.login_user_collision_message),
                Toast.LENGTH_SHORT
        ).show()
    }

    private fun handleInvalidUser() {
        Toast.makeText(
                this,
                getString(R.string.login_invalid_user_message),
                Toast.LENGTH_SHORT
        ).show()
    }

    fun forgottenPassword() {
        // Redirect to forgotten password screen
        Log.d(TAG, "Forgotten password clicked")
    }

    fun registerUser() {
        // Redirect to user registration screen
        Log.d(TAG, "Register user")
    }

    fun guestLogin() {
        // Anonymous user login
        Log.d(TAG, "Guest login")
        doLogin(SignInViewModel.SignInProvider.GUEST)
    }

    fun emailLogin() {
        doLogin(SignInViewModel.SignInProvider.EMAIL)
    }

    fun fbLogin() {
        doLogin(SignInViewModel.SignInProvider.FACEBOOK)
    }

    fun twitterLogin() {
        doLogin(SignInViewModel.SignInProvider.TWITTER)
    }

    fun googleLogin() {
        doLogin(SignInViewModel.SignInProvider.GOOGLE)
    }

    private fun doLogin(providerType: SignInViewModel.SignInProvider) {
        val user = binding.loginUserInput.text.toString()
        val pass = binding.loginPasswordInput.text.toString()
        doLogin(providerType, user, pass)
    }

    private fun doLogin(
            providerType: SignInViewModel.SignInProvider,
            user: String = "",
            pass: String = ""
    ) {
        Log.d(TAG, "$user - $pass - $providerType")
        signInViewModel.doLogin(this, providerType, user, pass)
    }

    private fun showLoading() {
        binding.signinLoading.progressOverlay.animate(View.VISIBLE, 1f, 400)
    }

    private fun hideLoading() {
        binding.signinLoading.progressOverlay.animate(View.GONE, 0f, 400)
    }

    private fun facebookPrompt(permissions: List<String>?) {
        LoginManager.getInstance().logInWithReadPermissions(this, permissions)
    }

    private fun googlePrompt(permissions: List<String>?) {
        val signInIntent = signInViewModel.googleSignInIntent
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
    }

    private fun twitterPrompt(permissions: List<String>?) {

    }
}
