package com.oligark.getter.view.ui

import android.arch.lifecycle.LifecycleActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
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
import com.oligark.getter.databinding.ActivityMainBinding

class MainActivity : LifecycleActivity() {
    companion object {
        @JvmField val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityMainBinding
    private var mLastQuery = ""

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            startSignInActivity()
            return
        }
        setupSearchbar()
    }

    private fun startSignInActivity() {
        val signIn = Intent(this, SignInActivity::class.java)
        startActivity(signIn)
    }

    private fun setupSearchbar() {
        binding.searchbar.setOnQueryChangeListener { oldQuery, newQuery ->
            if (oldQuery.isNotEmpty() && newQuery.isEmpty()) {
                binding.searchbar.clearSuggestions()
            } else {
                binding.searchbar.showProgress()

                // Load data
            }
            Log.d(TAG, "onQueryChange")
        }

        binding.searchbar.setOnSearchListener(object : FloatingSearchView.OnSearchListener {
            /**
             * Called when query is updated, or search button clicked
             * Should update current query and display results based on it
             */
            override fun onSearchAction(currentQuery: String) {
                mLastQuery = currentQuery
                // TODO("Load more results in background")
                Log.d(TAG, "onSearchAction()")
            }

            /**
             * Called when a suggestion item is clicked
             * Should update query in searchbar and load results based on suggestion
             */
            override fun onSuggestionClicked(searchSuggestion: SearchSuggestion) {
                // Cast to SearchSuggestion implementation if necessary
                val suggestion = searchSuggestion
                // TODO("Load more results in background")
                mLastQuery = searchSuggestion.body
                Log.d(TAG, "onSuggestionClicked")
            }
        })

        binding.searchbar.setOnFocusChangeListener(
                object : FloatingSearchView.OnFocusChangeListener {
                    override fun onFocusCleared() {
                        // Make last query remain in searchbar
                        binding.searchbar.setSearchBarTitle(mLastQuery)
                        Log.d(TAG, "onFocusCleared()")
                    }

                    override fun onFocus() {
                        // Load suggestions, usually history suggestions
//                        binding.searchbar.swapSuggestions()
                        Log.d(TAG, "onFocus()")
                    }
                }
        )
    }
}
