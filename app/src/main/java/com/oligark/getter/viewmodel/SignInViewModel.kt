package com.oligark.getter.viewmodel

import android.arch.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI

/**
 * Created by pmvb on 17-09-13.
 */
class SignInViewModel : ViewModel() {

    enum class SIGNIN_PROVIDER(val provider: String) {
        GOOGLE(AuthUI.GOOGLE_PROVIDER),
        TWITTER(AuthUI.TWITTER_PROVIDER),
        FACEBOOK(AuthUI.FACEBOOK_PROVIDER),
        EMAIL(AuthUI.EMAIL_PROVIDER)
    }

    fun doLogin(provider: SIGNIN_PROVIDER, user: String = "", pass: String = "") {

    }
}