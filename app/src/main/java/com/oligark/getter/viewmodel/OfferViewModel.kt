package com.oligark.getter.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.oligark.getter.service.model.Offer
import com.oligark.getter.service.repository.OfferRepository
import com.oligark.getter.service.repository.source.DataSource
import com.oligark.getter.service.repository.source.OfferDataSource
import com.oligark.getter.service.repository.source.local.OfferLocalDataSource
import com.oligark.getter.service.repository.source.local.db.GetterDatabase
import com.oligark.getter.service.repository.source.remote.OfferRemoteDataSource
import com.oligark.getter.util.AppExecutors
import com.oligark.getter.viewmodel.resources.Resource

/**
 * Created by pmvb on 17-09-29.
 */
class OfferViewModel(
        application: Application
) : AndroidViewModel(application) {
    companion object {
        val TAG = OfferViewModel::class.java.simpleName
    }

    val offers = MutableLiveData<Resource<Offer>>()
    private val offerRepository = OfferRepository.getInstance(
            OfferLocalDataSource(
                    AppExecutors(),
                    GetterDatabase.getInstance(application).offerDao()
            ),
            OfferRemoteDataSource()
    )

    fun getStoreOffers(storeId: Int) {
        offers.value = Resource(listOf())
        offerRepository.getStoreOffers(storeId, object : DataSource.LoadItemsCallback<Offer> {
            override fun onItemsLoaded(items: List<Offer>) {
                offers.value = Resource(items, Resource.LoadState.SUCCESS)
            }

            override fun onDataNotAvailable() {
                offers.value = Resource(listOf(), Resource.LoadState.ERROR)
            }
        })
    }
}