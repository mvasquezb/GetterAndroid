package com.oligark.getter.view.ui

import android.arch.lifecycle.LifecycleActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.oligark.getter.R

class MainActivity : LifecycleActivity(), OnMapReadyCallback {
    companion object {
        @JvmField val TAG = MainActivity::class.java.simpleName
        @JvmField val PERMISSION_REQUEST_FINE_LOCATION = 51412
    }

    private var mMap: GoogleMap? = null
    private var mLocationPermissionGranted = false

    private lateinit var locationClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            startSignInActivity()
            return
        }

        if (requestLocationPermission()) {
            locationClient = LocationServices.getFusedLocationProviderClient(this)
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_FINE_LOCATION -> {
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mapSetup(mMap!!)
                } else {
                    Toast.makeText(
                            this,
                            getString(R.string.location_permission_rejected),
                            Toast.LENGTH_LONG
                    ).show()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

    }

    private fun startSignInActivity() {
        val signIn = Intent(this, SignInActivity::class.java)
        startActivity(signIn)
    }

    /**
     * Requests location permission, if already granted, returns true
     * If not granted, requests permission and returns false
     */
    private fun requestLocationPermission(): Boolean {
        val locationPermissionStatus = ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        )
        mLocationPermissionGranted = locationPermissionStatus == PackageManager.PERMISSION_GRANTED
        if (!mLocationPermissionGranted) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_REQUEST_FINE_LOCATION
            )
        }
        return mLocationPermissionGranted
    }

    private fun mapSetup(googleMap: GoogleMap) {
        if (mLocationPermissionGranted) {
            val locationResult = locationClient.lastLocation
            locationResult.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    lastKnownLocation = task.result
                    Log.d(TAG, "$lastKnownLocation")
                    if (lastKnownLocation != null) {
                        val currentPos = LatLng(
                                lastKnownLocation!!.latitude,
                                lastKnownLocation!!.longitude
                        )
                        googleMap.addMarker(MarkerOptions().position(currentPos).title("Current location"))
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentPos))
                    } else {
                        Log.d(TAG, "Task successful. Current location is null")
                    }
                } else {
                    Log.d(TAG, "Current location is null")
                    Log.e(TAG, "LocationServices error: ${task.exception}")
                    val sydney = LatLng(-34.0, 151.0)
                    googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (requestLocationPermission()) {
            mapSetup(googleMap)
        }
    }
}