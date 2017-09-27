package com.oligark.getter.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.oligark.getter.service.model.BusinessStore
import com.oligark.getter.service.model.Store
import com.oligark.getter.service.repository.StoreRepository
import com.oligark.getter.service.repository.source.DataSource
import com.oligark.getter.service.repository.source.local.StoresLocalDataSource
import com.oligark.getter.service.repository.source.remote.StoresRemoteDataSource

/**
 * Created by pmvb on 17-09-26.
 */
class StoresViewModel : ViewModel() {
    companion object {
        val TAG = StoresViewModel::class.java.simpleName
    }

    val businessStores = MutableLiveData<List<BusinessStore>>()

    private val businessStoreRepository = StoreRepository.getInstance(
            StoresLocalDataSource(),
            StoresRemoteDataSource()
    )

    init {
        Log.e(TAG, "Before getting item")
        businessStoreRepository.getItem(1, object : DataSource.GetItemCallback<Store> {
            override fun onItemLoaded(item: Store) {
                var store = item
                try {
                    store = store as BusinessStore
                    Log.e(TAG, "Store: ${store.businessLogoUrl} - ${store.businessName}")
                    businessStores.value = listOf(store)
                } catch (e: ClassCastException) {
                    Log.e(TAG, "Store is not business store")
                }
            }

            override fun onDataNotAvailable() {
                Log.e(TAG, "Data not available")
            }
        })
    }
}