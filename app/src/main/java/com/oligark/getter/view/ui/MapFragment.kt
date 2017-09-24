package com.oligark.getter.view.ui

import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.oligark.getter.R
import android.widget.RelativeLayout
import com.google.android.gms.maps.model.CircleOptions

/**
 * Created by pmvb on 17-09-23.
 */
class MapFragment :
        SupportMapFragment(),
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener {
    companion object {
        @JvmField val TAG = MapFragment::class.java.simpleName
        @JvmStatic private val DEFAULT_ZOOM = 16.toFloat()
        @JvmField val PERMISSION_REQUEST_FINE_LOCATION = 51412
    }

    private lateinit var mMap: GoogleMap
    private var mLocationPermissionGranted = false

    private lateinit var locationClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null
    private val mDefaultLocation = LatLng(-12.069444, -77.079444) // PUCP

    override fun onActivityCreated(p0: Bundle?) {
        super.onActivityCreated(p0)

        if (requestLocationPermission()) {
            locationClient = LocationServices.getFusedLocationProviderClient(activity)
        }
        getMapAsync(this)
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
                    mapSetup()
                } else {
                    Toast.makeText(
                            activity,
                            getString(R.string.location_permission_rejected),
                            Toast.LENGTH_LONG
                    ).show()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /**
     * Requests location permission, if already granted, returns true
     * If not granted, requests permission and returns false
     */
    private fun requestLocationPermission(): Boolean {
        val locationPermissionStatus = ActivityCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        )
        mLocationPermissionGranted = locationPermissionStatus == PackageManager.PERMISSION_GRANTED
        if (!mLocationPermissionGranted) {
            ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_REQUEST_FINE_LOCATION
            )
        }
        return mLocationPermissionGranted
    }

    private fun mapSetup() {
        Log.d(TAG, "Permission granted: $mLocationPermissionGranted")
        if (mLocationPermissionGranted) {
            mMap.isMyLocationEnabled = true
            locationClient.lastLocation.addOnSuccessListener { location ->
                lastKnownLocation = location
                Log.d(TAG, "$lastKnownLocation")
                if (location != null) {
                    val currentPos = LatLng(
                            location.latitude,
                            location.longitude
                    )
//                    mMap.addMarker(MarkerOptions().position(currentPos).title("Current location"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos, DEFAULT_ZOOM))

                    // Set circle around current location
                    // Radius in meters
                    // TODO("update radius to match user level")
                    mMap.addCircle(CircleOptions()
                            .center(currentPos)
                            .radius(100.0)
                            .strokeWidth(0f)
                            .fillColor(Color.argb(96, 128, 128, 255))
                    )
                } else {
                    Log.d(TAG, "Task successful. Current location is null")
                }
            }.addOnFailureListener { exception ->
                Log.d(TAG, "Current location is null")
                Log.e(TAG, "LocationServices error: $exception")
                mMap.addMarker(MarkerOptions().position(mDefaultLocation).title("Marcador en la PUCP"))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mDefaultLocation))
            }

            // Move MyLocation button to bottom right
            val locationButton = (activity.findViewById<View>(1).parent as View)
                    .findViewById<View>(2)
            val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            layoutParams.setMargins(0, 0, 30, 30)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMyLocationButtonClickListener(this)

        if (requestLocationPermission()) {
            mapSetup()
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
//        Toast.makeText(activity, "MyLocation button clicked", Toast.LENGTH_SHORT).show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }
}