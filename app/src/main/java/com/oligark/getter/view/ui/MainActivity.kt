package com.oligark.getter.view.ui
import com.oligark.getter.R
import com.oligark.getter.view.utils.MyMapFragment

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment =
                supportFragmentManager.findFragmentById(R.id.map) as MyMapFragment
        mapFragment.getMapAsync(mapFragment)
        System.err.println("OnCreate end")
    }


}