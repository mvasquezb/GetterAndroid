package com.oligark.getter.view.ui

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth

import com.oligark.getter.R
import com.oligark.getter.databinding.ActivitySigninBinding
import com.oligark.getter.viewmodel.SignInViewModel

class SignInActivity : LifecycleActivity() {

    companion object {
        @JvmField
        val TAG = SignInActivity::class.java.simpleName
        @JvmField
        val AUTH_CREDENTIAL_KEY = "auth_credential"
    }

    private lateinit var binding: ActivitySigninBinding
//    private lateinit var viewModel: SignInViewModel
    private var credential: AuthCredential? = null

    private val mFbCallbackManager: CallbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signin)
//        viewModel = ViewModelProviders.of(this).get(SignInViewModel::class.java)
        LoginManager.getInstance().registerCallback(
                mFbCallbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onCancel() {
                        Log.d(TAG, "Login cancelled")
                    }
                    override fun onSuccess(result: LoginResult) {
                        Log.d(TAG, "Login success")
                        handleFbLogin(result.accessToken)
                    }
                    override fun onError(error: FacebookException?) {
                        Log.e(TAG, "Login error", error)
                    }
                }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (mFbCallbackManager.onActivityResult(requestCode, resultCode, data)) {
            launchMainActivity()
            return
        }
    }

    private fun launchMainActivity() {
        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.putExtra(AUTH_CREDENTIAL_KEY, credential)
        startActivity(mainIntent)
        finish()
    }

    private fun handleFbLogin(accessToken: AccessToken) {
        Log.d(TAG, "handleFbLogn: $accessToken")
        credential = FacebookAuthProvider.getCredential(accessToken.token)
        FirebaseAuth.getInstance().signInWithCredential(credential!!)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(TAG, "FirebaseSignInWithCredential success")
                    }
                }
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
        Log.d(TAG, "$user - $pass - $providerType")
//        viewModel.doLogin(providerType, user, pass)
        val permissions = getPermissionsForProvider(providerType)
        when (providerType) {
            SignInViewModel.SIGNIN_PROVIDER.FACEBOOK -> {
                LoginManager.getInstance().logInWithReadPermissions(this, permissions)
            }
            SignInViewModel.SIGNIN_PROVIDER.GOOGLE -> {

            }
            SignInViewModel.SIGNIN_PROVIDER.TWITTER -> {

            }
            SignInViewModel.SIGNIN_PROVIDER.EMAIL -> {

            }
        }
    }

    private fun getPermissionsForProvider(provider: SignInViewModel.SIGNIN_PROVIDER): List<String> {
        val permissions = mutableListOf<String>()
        when (provider) {
            SignInViewModel.SIGNIN_PROVIDER.FACEBOOK -> {
                permissions.addAll(arrayOf("public_profile", "email"))
            }
            else -> {
                // Do nothing by default
            }
        }
        return permissions
    }
}
