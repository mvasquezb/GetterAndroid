package com.oligark.getter.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.oligark.getter.service.model.BusinessStore
import com.oligark.getter.service.repository.StoreRepository
import com.oligark.getter.service.repository.source.DataSource
import com.oligark.getter.service.repository.source.local.StoresLocalDataSource
import com.oligark.getter.service.repository.source.remote.StoresRemoteDataSource
import com.oligark.getter.viewmodel.resources.BaseResource
import com.oligark.getter.viewmodel.resources.StoresResource

/**
 * Created by pmvb on 17-09-26.
 */
class StoresViewModel : ViewModel() {
    companion object {
        val TAG = StoresViewModel::class.java.simpleName
    }

    val stores = MutableLiveData<StoresResource>()

    private val businessStoreRepository = StoreRepository.getInstance(
            StoresLocalDataSource(),
            StoresRemoteDataSource()
    )

    init {
        Log.e(TAG, "Before getting item")
        stores.value = StoresResource(listOf(), BaseResource.LoadState.LOADING)
        businessStoreRepository.getItems(object : DataSource.LoadItemsCallback<BusinessStore> {
            override fun onItemsLoaded(items: List<BusinessStore>) {
                stores.value = StoresResource(items, BaseResource.LoadState.SUCCESS)
            }

            override fun onDataNotAvailable() {
                stores.value = StoresResource(stores.value?.stores!!, BaseResource.LoadState.ERROR)
            }
        })
    }
}