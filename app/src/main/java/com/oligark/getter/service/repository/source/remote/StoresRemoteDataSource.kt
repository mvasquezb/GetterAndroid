package com.oligark.getter.service.repository.source.remote

import android.util.Log
import com.oligark.getter.service.model.BusinessStore
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
 * Potential for Dependency Injection (Singleton)
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

    override fun getItems(callback: DataSource.LoadItemsCallback<BusinessStore>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItem(itemId: Int, callback: DataSource.GetItemCallback<BusinessStore>) {
        storesService.getStore(itemId).enqueue(object : Callback<BusinessStore> {
            override fun onResponse(call: Call<BusinessStore>?, response: Response<BusinessStore>?) {
                val store = response?.body()
                if (store == null) {
                    Log.e(TAG, response.toString())
                    return
                }
                Log.e(TAG, "${store.businessName} ${store.businessLogoUrl} ${store.businessId} ${store.id} ${store.latitude} ${store.longitude}")
                callback.onItemLoaded(store)
            }

            override fun onFailure(call: Call<BusinessStore>?, t: Throwable?) {
                callback.onDataNotAvailable()
            }
        })
    }

    override fun refreshItems() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveItem(item: BusinessStore) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAll() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}