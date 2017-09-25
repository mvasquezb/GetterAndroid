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
import com.google.firebase.auth.FirebaseUser
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.oligark.getter.R
import com.oligark.getter.databinding.ActivityMainBinding

class MainActivity : LifecycleActivity() {
    companion object {
        @JvmField val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityMainBinding
    private var mLastQuery = ""

    private lateinit var mUser: FirebaseUser
    private lateinit var mDrawer: Drawer
    private lateinit var navHeader: AccountHeader

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            startSignInActivity()
            return
        }
        mUser = auth.currentUser!!

        setupSearchbar()
        setupDrawerMenu()
    }

    private fun startSignInActivity() {
        val signIn = Intent(this, SignInActivity::class.java)
        startActivity(signIn)
    }

    private fun setupDrawerMenu() {
        // Add profile header
        navHeader = AccountHeaderBuilder().withActivity(this)
                .withHeaderBackground(R.drawable.login_round_border)
                .addProfiles(
                        ProfileDrawerItem().withName(mUser.displayName)
                                .withEmail(mUser.email)
                                .withIcon(R.mipmap.ic_launcher_round)
                )
                .withSelectionListEnabledForSingleProfile(false)
                .build()

        // Build basic drawer
        mDrawer = DrawerBuilder().withActivity(this)
                .withHeader(navHeader.view)
                .inflateMenu(R.menu.menu_main)
                .withSelectedItem(-1)
                .build()

        // Setup searchbar hamburger hook
        binding.searchbar.attachNavigationDrawerToMenuButton(mDrawer.drawerLayout)
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
                        binding.searchbar.hideProgress()
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
