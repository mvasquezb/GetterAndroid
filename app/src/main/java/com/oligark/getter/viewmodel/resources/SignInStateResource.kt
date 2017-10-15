package com.oligark.getter.viewmodel.resources

/**
 * Created by pmvb on 17-10-07.
 */
open class SignInStateResource(
        val signInState: SignInState = SignInState.DEFAULT,
        val permissions: List<String>? = null
) {
    enum class SignInState {
        DEFAULT,
        LOADING,
        SUCCESS,
        ERROR,
        CANCELLED,
        FACEBOOK_PROMPT,
        GOOGLE_PROMPT,
        TWITTER_PROMPT,
        USER_COLLISION,
        INVALID_USER,
    }
}