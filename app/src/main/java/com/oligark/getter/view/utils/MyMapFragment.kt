package com.oligark.getter.view.utils

/**
 * Created by sergio on 9/22/17.
 */


import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import android.support.annotation.NonNull
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.CameraPosition
import android.R.attr.duration




class MyMapFragment : SupportMapFragment(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap

    override fun onMapReady(map: GoogleMap?) {
        System.err.println("OnMapReady start")
        mMap = map as GoogleMap;

        val sydney = LatLng(-34.0, 151.0);
        mMap.addMarker(MarkerOptions().position(sydney).title("Seed nay"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        Toast.makeText(this.context, "OnMapReady end", Toast.LENGTH_LONG).show()
        val mfusedLocationProviderclient: FusedLocationProviderClient? = null
        val myLocation= mfusedLocationProviderclient?.lastLocation?.result
        if (!(myLocation == null)){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(myLocation.latitude,myLocation.longitude)))
        }else{
            val toast = Toast.makeText(context, "Ubicación no encontrada", duration)
            toast.show()
        }
    }

}