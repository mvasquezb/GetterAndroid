package com.oligark.getter.viewmodel

import android.arch.lifecycle.ViewModel

/**
 * Created by pmvb on 17-09-13.
 */
class SignInViewModel : ViewModel() {

    enum class SIGNIN_PROVIDER {
        GOOGLE,
        TWITTER,
        FACEBOOK,
        EMAIL
    }

    fun doLogin(provider: SIGNIN_PROVIDER, user: String = "", pass: String = "") {

    }
}