package com.oligark.getter.service.repository.source.api

import com.oligark.getter.service.model.Store
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by pmvb on 17-09-25.
 */
interface StoresService {
    @GET("api/stores")
    fun getStores(@Query("business_info") businessInfo: Boolean = true): Call<List<Store>>

    @GET("api/business/{business}/stores")
    fun getBusinessStores(
            @Path("business") business: Int,
            @Query("business_info") businessInfo: Boolean = true
    ): Call<List<Store>>

    @GET("api/business/{business}/stores/{store}")
    fun getBusinessStore(
            @Path("business") business: Int,
            @Path("store") store: Int,
            @Query("business_info") businessInfo: Boolean = true
    ): Call<Store>

    @GET("api/stores/{store}")
    fun getStore(
            @Path("store") store: Int,
            @Query("business_info") businessInfo: Boolean = true
    ): Call<Store>
}