package com.oligark.getter.viewmodel

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.util.Log
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.*
import com.oligark.getter.R
import com.oligark.getter.viewmodel.resources.SignInStateResource
import com.oligark.getter.viewmodel.resources.SignInStateResource.SignInState
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient

/**
 * Created by pmvb on 17-09-13.
 */
class SignInViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        @JvmStatic
        val TAG = SignInViewModel::class.java.simpleName
        @JvmStatic
        private val ANONYMOUS_PROVIDER = "anonymous_user"
        @JvmField
        val RC_GOOGLE_SIGN_IN = 12412
    }

    enum class SignInProvider(val provider: String) {
        GOOGLE(AuthUI.GOOGLE_PROVIDER),
        TWITTER(AuthUI.TWITTER_PROVIDER),
        FACEBOOK(AuthUI.FACEBOOK_PROVIDER),
        EMAIL(AuthUI.EMAIL_PROVIDER),
        GUEST(ANONYMOUS_PROVIDER)
    }

    private val mGoogleApiClient: GoogleApiClient by lazy {
        buildGoogleClient()
    }
    private val mFbCallbackManager: CallbackManager = CallbackManager.Factory.create()
    private val mTwitterAuthClient: TwitterAuthClient by lazy {
        buildTwitterClient()
    }

    val signInState = MutableLiveData<SignInStateResource>()
    val googleSignInIntent: Intent by lazy {
        Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
    }
    var credential: AuthCredential? = null

    fun init() {
        buildFacebookClient()
    }

    private fun buildFacebookClient() {
        LoginManager.getInstance().registerCallback(
                mFbCallbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onCancel() {
                        Log.d(TAG, "Login cancelled")
                        onLoginCancelled()
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
    }

    private fun buildGoogleClient(): GoogleApiClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(
                        getApplication<Application>().getString(R.string.google_web_client_id)
                )
                .requestEmail()
                .build()
        val googleApiClient = GoogleApiClient.Builder(getApplication())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
        return googleApiClient
    }

    private fun buildTwitterClient(): TwitterAuthClient {
        Twitter.initialize(TwitterConfig.Builder(getApplication()).twitterAuthConfig(
                TwitterAuthConfig(
                        getApplication<Application>().getString(R.string.twitter_consumer_key),
                        getApplication<Application>().getString(R.string.twitter_consumer_secret)
                )
        ).build())
        val twitterAuthClient = TwitterAuthClient()
        return twitterAuthClient
    }

    fun doLogin(
            activity: Activity,
            providerType: SignInProvider,
            user: String = "",
            pass: String = ""
    ) {
        signInState.value = SignInStateResource(SignInState.LOADING, null)
        val permissions = getPermissionsForProvider(providerType)
        when (providerType) {
            SignInViewModel.SignInProvider.FACEBOOK -> {
                signInState.value = SignInStateResource(
                        SignInState.FACEBOOK_PROMPT, permissions
                )
            }
            SignInViewModel.SignInProvider.GOOGLE -> {
                signInState.value = SignInStateResource(SignInState.GOOGLE_PROMPT, permissions)
            }
            SignInViewModel.SignInProvider.TWITTER -> {
                Log.d(TAG, "Twitter provider clicked")
                signInState.value = SignInStateResource(SignInState.TWITTER_PROMPT, permissions)
                mTwitterAuthClient.authorize(activity, object : Callback<TwitterSession>() {
                    override fun failure(exception: TwitterException?) {
                        Log.e(TAG, "Twitter exception", exception)
                        onLoginError()
                    }
                    override fun success(result: Result<TwitterSession>) {
                        Log.d(TAG, "Twitter authorization success")
                        handleTwitterLogin(result.data)
                    }
                })
            }
            SignInViewModel.SignInProvider.EMAIL -> {
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
                                    onInvalidUser()
                                }
                            }
                        }
            }
            SignInViewModel.SignInProvider.GUEST -> {
                FirebaseAuth.getInstance().signInAnonymously()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "Anonymous login success")
                                onLoginSuccess()
                            } else {
                                Log.e(TAG, "Anonymous login error", task.exception)
                                onLoginError()
                            }
                        }
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
                            onUserCollision()
                        }
                    }
                }
    }

    private fun handleFbLogin(accessToken: AccessToken) {
        Log.d(TAG, "handleFbLogin: $accessToken")
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
                            onUserCollision()
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
                            onUserCollision()
                        }
                    }
                }
    }

    private fun getPermissionsForProvider(provider: SignInViewModel.SignInProvider): List<String> {
        val permissions = mutableListOf<String>()
        when (provider) {
            SignInViewModel.SignInProvider.FACEBOOK -> {
                permissions.addAll(arrayOf("public_profile", "email"))
            }
            else -> {
                // Do nothing by default
            }
        }
        return permissions
    }

    private fun onLoginCancelled() {
        signInState.value = SignInStateResource(SignInState.CANCELLED, null)
    }

    private fun onLoginSuccess() {
        signInState.value = SignInStateResource(SignInState.SUCCESS, null)
    }

    private fun onLoginError() {
        signInState.value = SignInStateResource(SignInState.ERROR, null)
    }

    private fun onUserCollision() {
        signInState.value = SignInStateResource(SignInState.USER_COLLISION, null)
    }

    private fun onInvalidUser() {
        signInState.value = SignInStateResource(SignInState.INVALID_USER, null)
    }
}