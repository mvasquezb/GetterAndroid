package com.oligark.getter.service.repository.source.remote

import android.util.Log
import com.oligark.getter.service.model.Store
import com.oligark.getter.service.repository.source.DataSource
import com.oligark.getter.service.repository.source.StoreDataSource
import com.oligark.getter.service.repository.source.api.BaseApi
import com.oligark.getter.service.repository.source.api.StoresService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Remote data source for stores
 * Potential for Dependency Injection (Singleton) for storesService
 */
class StoresRemoteDataSource : StoreDataSource {

    companion object {
        val TAG = StoresRemoteDataSource::class.java.simpleName
    }

    val storesService: StoresService = Retrofit.Builder()
            .baseUrl(BaseApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(CustomMoshi.INSTANCE)) // Should be custom
            .build()
            .create(StoresService::class.java)

    override fun getItems(
            callback: DataSource.LoadItemsCallback<Store>,
            forceUpdate: Boolean
    ) {
        storesService.getStores().enqueue(object : Callback<List<Store>> {
            override fun onResponse(call: Call<List<Store>>?, response: Response<List<Store>>?) {
                storesListSuccess(callback, response)
            }

            override fun onFailure(call: Call<List<Store>>?, t: Throwable?) {
                Log.e(TAG, "API error: $t")
                callback.onDataNotAvailable()
            }
        })
    }

    private fun storesListSuccess(
            callback: DataSource.LoadItemsCallback<Store>,
            response: Response<List<Store>>?
    ) {
        if (response == null || response.isSuccessful.not()) {
            Log.e(TAG, "Response unsuccessful: ${response?.code()} - ${response?.message()} - ${response?.raw()?.request()?.url()}")
            callback.onDataNotAvailable()
            return
        }
        val stores = response.body()
        if (stores == null) {
            Log.e(TAG, response.toString())
            callback.onDataNotAvailable()
            return
        }
        Log.e(TAG, response.toString())
        callback.onItemsLoaded(stores)
    }

    override fun getItem(
            itemId: Int,
            callback: DataSource.GetItemCallback<Store>,
            forceUpdate: Boolean
    ) {
        storesService.getStore(itemId).enqueue(object : Callback<Store> {
            override fun onResponse(call: Call<Store>?, response: Response<Store>?) {
                val store = response?.body()
                if (store == null) {
                    Log.e(TAG, response.toString())
                    callback.onDataNotAvailable()
                    return
                }
                Log.d(TAG, "${store.businessName} ${store.businessLogoUrl} ${store.businessId} ${store.id} ${store.latitude} ${store.longitude}")
                callback.onItemLoaded(store)
            }

            override fun onFailure(call: Call<Store>?, t: Throwable?) {
                Log.e(TAG, "API error: $t")
                callback.onDataNotAvailable()
            }
        })
    }

    override fun refreshItems() {
        // Not required
    }

    override fun saveItem(item: Store) {
        // Not required
    }

    override fun deleteAll() {
        // Not required
    }

    override fun saveBulkItems(vararg items: Store) {
        // Not required
    }

    override fun filter(
            callback: DataSource.LoadItemsCallback<Store>,
            filters: Map<String, List<String>>
    ) {
        storesService.getStores(categories = filters["categories"]).enqueue(
                object : Callback<List<Store>> {
                    override fun onResponse(
                            call: Call<List<Store>>?,
                            response: Response<List<Store>>?
                    ) {
                        storesListSuccess(callback, response)
                    }

                    override fun onFailure(call: Call<List<Store>>?, t: Throwable?) {
                        Log.e(TAG, "Exception: $t")
                        callback.onDataNotAvailable()
                    }
                }
        )
    }
}