package com.oligark.getter.service.repository.source.api

import com.oligark.getter.service.model.BusinessStore
import com.oligark.getter.service.model.Store
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by pmvb on 17-09-25.
 */
interface StoresService : BaseApi {
    @GET("stores")
    fun getStores(@Query("business") businessInfo: Boolean = true): Call<List<BusinessStore>>

    @GET("business/{business}/stores")
    fun getBusinessStores(@Path("business") business: Int): Call<List<BusinessStore>>

    @GET("stores/{store}")
    fun getStore(@Path("store") store: Int): Call<Store>
}