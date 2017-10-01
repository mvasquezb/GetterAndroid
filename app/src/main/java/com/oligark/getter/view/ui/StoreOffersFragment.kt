package com.oligark.getter.view.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.oligark.getter.R
import com.oligark.getter.databinding.FragmentStoreOffersBinding
import com.oligark.getter.service.model.Offer
import com.oligark.getter.viewmodel.OfferViewModel
import com.oligark.getter.viewmodel.resources.Resource

/**
 * Created by pmvb on 17-09-30.
 */
class StoreOffersFragment : Fragment() {

    companion object {
        val TAG = StoreOffersFragment::class.java.simpleName
    }

    private lateinit var offerViewModel: OfferViewModel
    private lateinit var binding: FragmentStoreOffersBinding

    override fun onCreateView(
            inflater: LayoutInflater?,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_store_offers, container, false)
        offerViewModel = ViewModelProviders.of(activity).get(OfferViewModel::class.java)
        offerViewModel.offers.observe(this, Observer { offerResource ->
            when (offerResource?.loadState) {
                Resource.LoadState.LOADING -> {
                    hideOfferLoadError()
                    showOfferLoadProgress()
                }
                Resource.LoadState.ERROR -> {
                    hideOfferLoadProgress()
                    showOfferLoadError()
                }
                Resource.LoadState.SUCCESS -> {
                    hideOfferLoadProgress()
                    showOfferList(offerResource.items)
                }
                else -> {}
            }
        })
        return binding.root
    }

    private fun showOfferList(items: List<Offer>) {
        if (items.isNotEmpty()) {
            Toast.makeText(activity, "${items.first().startDate} ${items.first().endDate}", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, "No offers loaded", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hideOfferLoadError() {
        //
    }

    private fun showOfferLoadError() {
        Log.e(TAG, "Error loading store offers")
        Toast.makeText(activity, "Error loading store offers", Toast.LENGTH_SHORT).show()
    }

    private fun hideOfferLoadProgress() {
        //
    }

    private fun showOfferLoadProgress() {
        //
    }
}