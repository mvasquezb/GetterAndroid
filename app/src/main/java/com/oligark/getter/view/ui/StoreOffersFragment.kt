package com.oligark.getter.view.ui

import android.app.ActionBar
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.oligark.getter.R
import com.oligark.getter.databinding.FragmentStoreOffersBinding
import com.oligark.getter.service.model.Offer
import com.oligark.getter.service.model.Store
import com.oligark.getter.view.adapters.OfferAdapter
import com.oligark.getter.viewmodel.OfferViewModel
import com.oligark.getter.viewmodel.resources.Resource
import com.squareup.picasso.Picasso

/**
 * Created by pmvb on 17-09-30.
 */
class StoreOffersFragment : Fragment(), OfferAdapter.OnOfferSelectCallback {
    companion object {

        val TAG = StoreOffersFragment::class.java.simpleName
        val CURRENT_STORE_ARG_KEY = "current_store"
    }
    private lateinit var offerViewModel: OfferViewModel
    private lateinit var binding: FragmentStoreOffersBinding
    private lateinit var mStore: Store
    private lateinit var mOfferAdapter: OfferAdapter

    override fun onCreateView(
            inflater: LayoutInflater?,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_store_offers, container, false)

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        val supportActionBar = (activity as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener {
            activity.onBackPressed()
        }

        mStore = arguments.getParcelable(StoreOffersFragment.CURRENT_STORE_ARG_KEY)
        offerViewModel = ViewModelProviders.of(activity).get(OfferViewModel::class.java)
        binding.offerViewModel = offerViewModel

        bindStore()
        setupOfferObserver()
        setupOfferList()

        binding.offerListSwipeRefresh.setOnRefreshListener {
            offerViewModel.getStoreOffers(mStore.id, forceUpdate = true)
        }

        return binding.root
    }

    override fun onOfferSelected(offer: Offer) {
        // Generate QR code and show it in overlay
        Log.e(TAG, "Offer selected: ${offer.description}")
    }

    private fun setupOfferList() {
        binding.offerList.layoutManager = LinearLayoutManager(
                activity,
                LinearLayoutManager.VERTICAL,
                false
        )
        mOfferAdapter = OfferAdapter(listOf(), this)
        binding.offerList.adapter = mOfferAdapter
    }

    private fun setupOfferObserver() {
        offerViewModel.offers.observe(this, Observer { offerResource ->
            when (offerResource?.loadState) {
                Resource.LoadState.LOADING -> {
                    hideOfferLoadMessage()
                    if (!binding.offerListSwipeRefresh.isRefreshing) {
                        showOfferLoadProgress()
                    }
                }
                Resource.LoadState.ERROR -> {
                    Log.e(TAG, "Ocurrió un error al cargar datos de la tienda: ${mStore.id}")
                    hideOfferLoadProgress()
                    showOfferLoadMessage(R.string.store_offers_load_error)
                }
                Resource.LoadState.SUCCESS -> {
                    hideOfferLoadProgress()
                    showOfferList(offerResource.items)
                }
                else -> {}
            }
        })
    }

    private fun bindStore() {
        binding.offerListBusinessName.text = mStore.businessName
        Picasso.with(activity)
                .load(mStore.businessLogoUrl)
                .placeholder(R.drawable.getter_logo)
                .into(binding.offerListBusinessLogo)
    }

    private fun showOfferList(items: List<Offer>) {
        if (items.isEmpty()) {
            showOfferLoadMessage(R.string.empty_store_offers_list)
            return
        }
        Log.e(TAG, "Offers: ${mOfferAdapter.itemCount}")
        mOfferAdapter.setOfferList(items)
        Log.e(TAG, "Offers: ${mOfferAdapter.itemCount}")
//        showOfferLoadMessage("${items.first().startDate} ${items.first().endDate}")
    }

    private fun showOfferLoadMessage(stringRes: Int) {
        showOfferLoadMessage(getString(stringRes))
    }

    private fun showOfferLoadMessage(message: String) {
        Log.e(TAG, "Showing load message: $message")
        binding.offerListLoadMessage.visibility = View.VISIBLE
        binding.offerListLoadMessage.text = message
    }

    private fun hideOfferLoadMessage() {
        Log.e(TAG, "Hiding load message")
        binding.offerListLoadMessage.visibility = View.GONE
    }

    private fun hideOfferLoadProgress() {
        Log.e(TAG, "Hiding load progress bar")
        binding.offerListProgress.visibility = View.GONE
        binding.offerListSwipeRefresh.isRefreshing = false
    }

    private fun showOfferLoadProgress() {
        Log.e(TAG, "Showing load progress bar")
        binding.offerListProgress.visibility = View.VISIBLE
    }
}