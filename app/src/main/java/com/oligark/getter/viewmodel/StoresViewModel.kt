package com.oligark.getter.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.oligark.getter.service.model.Store
import com.oligark.getter.service.repository.StoreRepository
import com.oligark.getter.service.repository.source.DataSource
import com.oligark.getter.service.repository.source.local.GetterDatabase
import com.oligark.getter.service.repository.source.local.StoresLocalDataSource
import com.oligark.getter.service.repository.source.remote.StoresRemoteDataSource
import com.oligark.getter.util.AppExecutors
import com.oligark.getter.viewmodel.resources.BaseResource
import com.oligark.getter.viewmodel.resources.StoresResource

/**
 * Created by pmvb on 17-09-26.
 */
class StoresViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        val TAG = StoresViewModel::class.java.simpleName
    }

    val stores = MutableLiveData<StoresResource>()

    private val businessStoreRepository = StoreRepository.getInstance(
            StoresLocalDataSource(
                    AppExecutors(),
                    GetterDatabase.getInstance(getApplication()).storeDao()
            ),
            StoresRemoteDataSource()
    )

    init {
        stores.value = StoresResource(listOf(), BaseResource.LoadState.LOADING)
//        businessStoreRepository.refreshItems() // By default
        businessStoreRepository.getItems(object : DataSource.LoadItemsCallback<Store> {
            override fun onItemsLoaded(items: List<Store>) {
                stores.value = StoresResource(items, BaseResource.LoadState.SUCCESS)
            }

            override fun onDataNotAvailable() {
                stores.value = StoresResource(stores.value?.stores!!, BaseResource.LoadState.ERROR)
            }
        })
    }
}