package com.oligark.getter.view.ui

import android.arch.lifecycle.LifecycleActivity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.*
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient

import com.oligark.getter.R
import com.oligark.getter.databinding.ActivitySigninBinding
import com.oligark.getter.viewmodel.SignInViewModel

class SignInActivity: LifecycleActivity() {

    companion object {
        @JvmField
        val TAG = SignInActivity::class.java.simpleName
        @JvmField
        val AUTH_CREDENTIAL_KEY = "auth_credential"
        @JvmField
        val RC_GOOGLE_SIGN_IN = 12412
    }

    private lateinit var binding: ActivitySigninBinding
//    private lateinit var viewModel: SignInViewModel
    private var credential: AuthCredential? = null
    private lateinit var mGoogleApiClient: GoogleApiClient

    private val mFbCallbackManager: CallbackManager = CallbackManager.Factory.create()

    private lateinit var mTwitterAuthClient: TwitterAuthClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signin)
//        viewModel = ViewModelProviders.of(this).get(SignInViewModel::class.java)

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
                        onLoginError()
                    }
                }
        )

        Twitter.initialize(TwitterConfig.Builder(this).twitterAuthConfig(
                TwitterAuthConfig(
                        getString(R.string.twitter_consumer_key),
                        getString(R.string.twitter_consumer_secret)
                )
        ).build())
        mTwitterAuthClient = TwitterAuthClient()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_web_client_id))
                .requestEmail()
                .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "request code: $requestCode. result code: $resultCode")
        if (FacebookSdk.isFacebookRequestCode(requestCode)) {
            Log.d(TAG, "Facebook onActivityResult")
            mFbCallbackManager.onActivityResult(requestCode, resultCode, data)
            if (FirebaseAuth.getInstance().currentUser != null) {
                onLoginSuccess()
            }
            return
        }

        if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            Log.d(TAG, "Twitter onActivityResult")
            mTwitterAuthClient.onActivityResult(requestCode, resultCode, data)
            if (FirebaseAuth.getInstance().currentUser != null) {
                onLoginSuccess()
            }
            return
        }

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                val account = result.signInAccount
                handleGoogleLogin(account!!)
            } else {
                Log.e(TAG, "Sign in failed")
            }
        }
    }

    private fun launchMainActivity() {
        Log.d(TAG, "LaunchMainActivity")
        val mainIntent = Intent(applicationContext, MainActivity::class.java)
        mainIntent.putExtra(AUTH_CREDENTIAL_KEY, credential)
        startActivity(mainIntent)
        finish()
    }

    private fun onLoginSuccess() {
        onLoginComplete()
        launchMainActivity()
    }

    private fun onLoginComplete() {
        hideLoading()
    }

    private fun onLoginError() {
        onLoginComplete()
        Toast.makeText(this, "Ocurrió un error. Intente nuevamente", Toast.LENGTH_SHORT).show()
    }

    private fun handleGoogleLogin(account: GoogleSignInAccount) {
        credential = GoogleAuthProvider.getCredential(account.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Google FirebaseSignInWithCredential success")
                        onLoginSuccess()
                    } else {
                        Log.e(TAG, "Google FirebaseSignInWithCredential error", task.exception)
                        try {
                            throw task.exception!!
                        } catch (e: FirebaseAuthUserCollisionException) {
                            handleUserCollision()
                        }
                    }
                }
    }


    private fun handleFbLogin(accessToken: AccessToken) {
        Log.d(TAG, "handleFbLogn: $accessToken")
        credential = FacebookAuthProvider.getCredential(accessToken.token)
        FirebaseAuth.getInstance().signInWithCredential(credential!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Facebook FirebaseSignInWithCredential success")
                        onLoginSuccess()
                    } else {
                        Log.e(TAG, "Facebook FirebaseAuth exception", task.exception)
                        try {
                            throw task.exception!!
                        } catch (e: FirebaseAuthUserCollisionException) {
                            handleUserCollision()
                        }
                    }
                }
    }

    private fun handleTwitterLogin(session: TwitterSession) {
        credential = TwitterAuthProvider.getCredential(
                session.authToken.token,
                session.authToken.secret
        )
        FirebaseAuth.getInstance().signInWithCredential(credential!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Twitter FirebaseSignInWithCredential success")
                        onLoginSuccess()
                    } else {
                        Log.e(TAG, "Twitter FirebaseAuth exception", task.exception)
                        try {
                            throw task.exception!!
                        } catch (e: FirebaseAuthUserCollisionException) {
                            handleUserCollision()
                        }
                    }
                }
    }

    private fun handleUserCollision() {
        Toast.makeText(
                this,
                "Login with another provider, then link your account with this one",
                Toast.LENGTH_SHORT
        ).show()
    }

    private fun handleInvalidUser() {
        Toast.makeText(
                this,
                "There's no user registered with those credentials. Please try again",
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
        doLogin(SignInViewModel.SIGNIN_PROVIDER.GUEST)
    }

    fun emailLogin() {
        doLogin(SignInViewModel.SIGNIN_PROVIDER.EMAIL)
    }

    fun fbLogin() {
        doLogin(SignInViewModel.SIGNIN_PROVIDER.FACEBOOK)
    }

    fun twitterLogin() {
        doLogin(SignInViewModel.SIGNIN_PROVIDER.TWITTER)
    }

    fun googleLogin() {
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
        showLoading()
//        viewModel.doLogin(providerType, user, pass)
        val permissions = getPermissionsForProvider(providerType)
        when (providerType) {
            SignInViewModel.SIGNIN_PROVIDER.FACEBOOK -> {
                LoginManager.getInstance().logInWithReadPermissions(this, permissions)
            }
            SignInViewModel.SIGNIN_PROVIDER.GOOGLE -> {
                val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
                startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
            }
            SignInViewModel.SIGNIN_PROVIDER.TWITTER -> {
                Log.d(TAG, "Twitter provider clicked")
                mTwitterAuthClient.authorize(this, object : Callback<TwitterSession>() {
                    override fun failure(exception: TwitterException?) {
                        Log.e(TAG, "Twitter exception", exception)
                    }
                    override fun success(result: Result<TwitterSession>) {
                        Log.d(TAG, "Twitter authorization success")
                        handleTwitterLogin(result.data)
                    }
                })
            }
            SignInViewModel.SIGNIN_PROVIDER.EMAIL -> {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(user, pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "Email login success")
                                onLoginSuccess()
                            } else {
                                Log.e(TAG, "Email login error", task.exception)
                                try {
                                    throw task.exception!!
                                } catch (e: FirebaseAuthInvalidUserException) {
                                    handleInvalidUser()
                                }
                            }
                        }
            }
            SignInViewModel.SIGNIN_PROVIDER.GUEST -> {
                FirebaseAuth.getInstance().signInAnonymously()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "Anonymous login success")
                                onLoginSuccess()
                            } else {
                                Log.e(TAG, "Anonymous login error", task.exception)
                            }
                        }
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

    private fun showLoading() {
//        binding.signinLoading.progressOverlay.animate(View.VISIBLE, 0.6f, 200)
    }

    private fun hideLoading() {
//        binding.signinLoading.progressOverlay.animate(View.GONE, 0f, 200)
    }
}
