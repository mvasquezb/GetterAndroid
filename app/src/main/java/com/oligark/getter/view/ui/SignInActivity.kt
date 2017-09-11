package com.oligark.getter.view.ui

import android.arch.lifecycle.LifecycleActivity
import android.os.Bundle

import com.oligark.getter.R

class SignInActivity : LifecycleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
    }
}
