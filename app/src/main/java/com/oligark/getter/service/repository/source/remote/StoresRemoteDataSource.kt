package com.oligark.getter.service.repository.source.remote

import com.oligark.getter.service.model.Store
import com.oligark.getter.service.repository.source.DataSource
import com.oligark.getter.service.repository.source.StoreDataSource
import com.oligark.getter.service.repository.source.api.BaseApi
import com.oligark.getter.service.repository.source.api.StoresService
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Remote data source for stores
 * Potential for Dependency Injection (Singleton)
 */
class StoresRemoteDataSource : StoreDataSource {

    val storesService: StoresService = Retrofit.Builder()
            .baseUrl(BaseApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create()) // Should be custom
            .build()
            .create(StoresService::class.java)

    override fun getItems(callback: DataSource.LoadItemsCallback<Store>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItem(itemId: Int, callback: DataSource.GetItemCallback<Store>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun refreshItems() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveItem(item: Store) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAll() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}