package com.oligark.getter.service.repository.source.remote

import android.util.Log
import com.oligark.getter.service.model.Offer
import com.oligark.getter.service.repository.source.DataSource
import com.oligark.getter.service.repository.source.OfferDataSource
import com.oligark.getter.service.repository.source.api.BaseApi
import com.oligark.getter.service.repository.source.api.OffersService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Created by pmvb on 17-09-28.
 */
class OfferRemoteDataSource : OfferDataSource {

    companion object {
        val TAG = OfferRemoteDataSource::class.java.simpleName
    }

    private val offerService: OffersService = Retrofit.Builder()
            .baseUrl(BaseApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(CustomMoshi.INSTANCE)) // Should be custom
            .build()
            .create(OffersService::class.java)

    private fun getOffersUtil(
            callback: DataSource.LoadItemsCallback<Offer>,
            active: Boolean? = null
    ) {
        offerService.getOffers(active).enqueue(object : Callback<List<Offer>> {
            override fun onFailure(call: Call<List<Offer>>?, t: Throwable?) {
                Log.e(TAG, "API error: $t")
                callback.onDataNotAvailable()
            }

            override fun onResponse(call: Call<List<Offer>>?, response: Response<List<Offer>>?) {
                Log.e(TAG, response.toString())
                val offers = response?.body()
                if (offers == null) {
                    Log.e(TAG, "Offers null")
                    callback.onDataNotAvailable()
                    return
                }
                Log.e(TAG, "Offers: ${offers.first().description}")
                callback.onItemsLoaded(offers)
            }
        })
    }

    override fun getStoreOffers(
            storeId: Int,
            callback: DataSource.LoadItemsCallback<Offer>,
            active: Boolean?,
            forceUpdate: Boolean
    ) {
        offerService.getStoreOffers(storeId, active).enqueue(object : Callback<List<Offer>> {
            override fun onFailure(call: Call<List<Offer>>?, t: Throwable?) {
                Log.e(TAG, "Offer API error: $t")
                callback.onDataNotAvailable()
            }

            override fun onResponse(call: Call<List<Offer>>?, response: Response<List<Offer>>?) {
                Log.e(TAG, response.toString())
                val offers = response?.body()
                if (offers == null) {
                    Log.e(TAG, "Something went wrong loading offers for store $storeId")
                    callback.onDataNotAvailable()
                    return
                }
                if (offers.isEmpty()) {
                    Log.e(TAG, "Store $storeId has no available offers")
                }
                callback.onItemsLoaded(offers)
            }
        })
    }

    override fun getItems(
            callback: DataSource.LoadItemsCallback<Offer>,
            forceUpdate: Boolean
    ) {
        getOffersUtil(callback)
    }

    override fun getActiveOffers(
            callback: DataSource.LoadItemsCallback<Offer>,
            active: Boolean,
            forceUpdate: Boolean
    ) {
        getOffersUtil(callback, active)
    }

    override fun getItem(
            itemId: Int,
            callback: DataSource.GetItemCallback<Offer>,
            forceUpdate: Boolean
    ) {
        offerService.getOffer(itemId).enqueue(object : Callback<Offer> {
            override fun onFailure(call: Call<Offer>?, t: Throwable?) {
                Log.e(TAG, "Offer API error: $t")
                callback.onDataNotAvailable()
            }

            override fun onResponse(call: Call<Offer>?, response: Response<Offer>?) {
                Log.d(TAG, response.toString())
                val offer = response?.body()
                if (offer == null) {
                    callback.onDataNotAvailable()
                    return
                }
                callback.onItemLoaded(offer)
            }
        })
    }

    override fun refreshItems() {
        // Do nothing
    }

    override fun saveItem(item: Offer) {
        // Do nothing
    }

    override fun saveBulkItems(vararg items: Offer) {
        // Do nothing
    }

    override fun deleteAll() {
        // Do nothing
    }
}