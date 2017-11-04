package com.oligark.getter.view.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionInflater
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.Toast
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.crashlytics.android.Crashlytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerImageLoader
import com.oligark.getter.R
import com.oligark.getter.databinding.ActivityMainBinding
import com.oligark.getter.service.model.ProductCategory
import com.oligark.getter.service.model.Store
import com.oligark.getter.viewmodel.FiltersViewModel
import com.oligark.getter.viewmodel.OfferViewModel
import com.oligark.getter.viewmodel.StoresViewModel
import com.oligark.getter.viewmodel.resources.DataResource
import com.squareup.picasso.Picasso
import io.fabric.sdk.android.Fabric

class MainActivity : AppCompatActivity(), MapFragment.OnStoreSelectCallback {
    companion object {
        @JvmField val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityMainBinding
    private var mLastQuery = ""

    private lateinit var mUser: FirebaseUser
    private lateinit var mDrawer: Drawer
    private lateinit var navHeader: AccountHeader

    private lateinit var storesViewModel: StoresViewModel
    private lateinit var offerViewModel: OfferViewModel

    private lateinit var filtersViewModel: FiltersViewModel

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            startSignInActivity()
            return
        }
        mUser = auth.currentUser!!

        storesViewModel = ViewModelProviders.of(this).get(StoresViewModel::class.java)
        storesViewModel.init()
        storesViewModel.stores.observe(this, Observer { storesResource ->
            when (storesResource?.loadState) {
                DataResource.LoadState.LOADING -> {
                    showProgress()
                }
                DataResource.LoadState.SUCCESS -> {
                    hideProgress()
                    // Update map markers in MapFragment
                }
                DataResource.LoadState.ERROR -> {
                    hideProgress()
                    Toast.makeText(
                            this,
                            "Ocurrió un error en la carga de datos. Intente nuevamente",
                            Toast.LENGTH_LONG
                    ).show()
                }
                else -> {}
            }
        })

        offerViewModel = ViewModelProviders.of(this).get(OfferViewModel::class.java)
        filtersViewModel = ViewModelProviders.of(this).get(FiltersViewModel::class.java)
        filtersViewModel.filtersApplied.observe(this, Observer { applyFilters ->
            if (applyFilters == true) {
                filterStores(filtersViewModel.selectedProductCategories.values)
            }
        })
        filtersViewModel.init()

        setupSearchbar()
        setupDrawerMenu()
    }

    private fun filterStores(selectedCategories: Collection<ProductCategory>) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStoreSelected(store: Store) {
        // Show offers fragment
        Log.e(TAG, "Store selected: $store")
        val data = Bundle()
        data.putParcelable(StoreOffersFragment.CURRENT_STORE_ARG_KEY, store)

        val storeOffers = StoreOffersFragment()
        val slide = TransitionInflater.from(this).inflateTransition(R.transition.slide)
        storeOffers.enterTransition = slide
        storeOffers.arguments = data
        supportFragmentManager.beginTransaction()
                .add(R.id.store_offers_container, storeOffers)
                .addToBackStack(null)
                .commit()
        offerViewModel.getStoreOffers(store.id)
        Log.e(TAG, "StoreOffersFragment loaded")
    }

    private fun showFiltersFragment() {
        Log.d(TAG, "Before displaying filters fragment")
        val filters = FiltersFragment()
        val slide = TransitionInflater.from(this).inflateTransition(R.transition.slide)
        filters.enterTransition = slide
        supportFragmentManager.beginTransaction()
                .add(R.id.filters_container, filters)
                .addToBackStack(null)
                .commit()
        Log.d(TAG, "FiltersFragment loaded")
    }

    private fun showProgress() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun hideProgress() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun startSignInActivity() {
        val signIn = Intent(this, SignInActivity::class.java)
        startActivity(signIn)
        finish()
    }

    private fun setupDrawerMenu() {
        // Setup image loading
        DrawerImageLoader.init(object : AbstractDrawerImageLoader() {
            override fun placeholder(ctx: Context): Drawable =
                    ContextCompat.getDrawable(ctx, R.mipmap.getter_logo)

            override fun set(imageView: ImageView, uri: Uri, placeholder: Drawable, tag: String) {
                Picasso.with(imageView.context).load(uri).placeholder(placeholder).into(imageView)
            }

            override fun cancel(imageView: ImageView) {
                Picasso.with(imageView.context).cancelRequest(imageView)
            }

        })
        // Add profile header
        navHeader = AccountHeaderBuilder().withActivity(this)
                .withHeaderBackground(R.drawable.login_round_border)
                .addProfiles(
                        ProfileDrawerItem().withName(mUser.displayName)
                                .withEmail(mUser.email)
                                .withIcon(R.mipmap.getter_logo)
                )
                .withSelectionListEnabledForSingleProfile(false)
                .withOnAccountHeaderListener { view, profile, current ->
                    Log.d(TAG, "Profile image clicked")
                    Toast.makeText(this, "Profile for: ${mUser.email}", Toast.LENGTH_SHORT).show()
                    // TODO("Open profile edit fragment")
                    true
                }
                .build()

        // Build basic drawer
        mDrawer = DrawerBuilder().withActivity(this)
                .withHeader(navHeader.view)
                .inflateMenu(R.menu.menu_main)
                .withSelectedItem(-1)
                .withOnDrawerItemClickListener { view, position, drawerItem ->
                    onNavigationItemSelected(view.id)
                    true
                }
                .build()

        // Setup searchbar hamburger hook
        binding.searchbar.attachNavigationDrawerToMenuButton(mDrawer.drawerLayout)
    }

    private fun onNavigationItemSelected(itemId: Int) {
        when (itemId) {
            R.id.btn_menu_preferences -> {
                // TODO("Load preferences fragment")
                Log.d(TAG, "Preferences clicked")
            }
            R.id.btn_menu_history -> {
                // TODO("Load history fragment")
                Log.d(TAG, "History clicked")
            }
            R.id.btn_menu_about -> {
                // TODO("Load about fragment")
                Log.d(TAG, "About clicked")
            }
            R.id.btn_menu_logout -> {
                Log.d(TAG, "Logout clicked")
                FirebaseAuth.getInstance().signOut()
                startSignInActivity()
                finish()
            }
        }
    }

    private fun setupSearchbar() {
        binding.searchbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.btn_menu_filter -> {
                    showFiltersFragment()
                }
            }
        }
        
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
