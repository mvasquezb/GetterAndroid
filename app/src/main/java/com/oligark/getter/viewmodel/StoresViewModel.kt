package com.oligark.getter.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.oligark.getter.service.model.Store
import com.oligark.getter.service.repository.StoreRepository
import com.oligark.getter.service.repository.source.DataSource
import com.oligark.getter.service.repository.source.local.db.GetterDatabase
import com.oligark.getter.service.repository.source.local.StoresLocalDataSource
import com.oligark.getter.service.repository.source.remote.StoresRemoteDataSource
import com.oligark.getter.util.AppExecutors
import com.oligark.getter.viewmodel.resources.Resource

/**
 * Created by pmvb on 17-09-26.
 */
class StoresViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        val TAG = StoresViewModel::class.java.simpleName
    }

    val stores = MutableLiveData<Resource<Store>>()

    private val storeRepository = StoreRepository.getInstance(
            StoresLocalDataSource(
                    AppExecutors(),
                    GetterDatabase.getInstance(getApplication()).storeDao()
            ),
            StoresRemoteDataSource()
    )

    fun init() {
        stores.value = Resource(listOf(), Resource.LoadState.LOADING)
//        storeRepository.refreshItems() // By default
        storeRepository.getItems(object : DataSource.LoadItemsCallback<Store> {
            override fun onItemsLoaded(items: List<Store>) {
                stores.value = Resource(items, Resource.LoadState.SUCCESS)
            }

            override fun onDataNotAvailable() {
                stores.value = Resource(stores.value?.items!!, Resource.LoadState.ERROR)
            }
        })
    }
}