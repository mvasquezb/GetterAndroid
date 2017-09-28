package com.oligark.getter.service.repository.source.api

import com.oligark.getter.service.model.Offer
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by pmvb on 17-09-28.
 */
interface OffersService {
    @GET("api/offers/")
    fun getOffers(@Query("active") active: Boolean = true): Call<List<Offer>>

    @GET("api/offers/count")
    fun getOfferCount(@Query("active") active: Boolean = true): Call<Int>

    @GET("api/offers/{offer}")
    fun getOffer(@Path("offer") offerId: Int): Call<Offer>

    @GET("api/stores/{store}/offers")
    fun getStoreOffers(
            @Path("store") storeId: Int,
            @Query("active") active: Boolean = true
    ): Call<List<Offer>>

    @GET("api/stores/{store}/offers/count")
    fun getStoreOfferCount(
            @Path("store") storeId: Int,
            @Query("active") active: Boolean = true
    ): Call<Int>

    @GET("api/stores/{store}/offers/{offer}")
    fun getStoreOffer(
            @Path("store") storeId: Int,
            @Query("active") active: Boolean = true
    ): Call<Offer>
}