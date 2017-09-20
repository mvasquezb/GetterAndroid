package com.oligark.getter.view.ui

import android.arch.lifecycle.LifecycleActivity
import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.oligark.getter.R

class MainActivity : LifecycleActivity() {

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            startSignInActivity()
            return
        }
    }

    private fun startSignInActivity() {
        val signIn = Intent(this, SignInActivity::class.java)
        startActivity(signIn)
    }
}