package com.oligark.getter.service.repository

import android.util.Log
import com.oligark.getter.service.model.Offer
import com.oligark.getter.service.repository.source.DataSource
import com.oligark.getter.service.repository.source.OfferDataSource

/**
 * Created by pmvb on 17-09-29.
 */
class OfferRepository : OfferDataSource {

    companion object {
        val TAG = OfferRepository::class.java.simpleName

        @JvmStatic private var INSTANCE: OfferRepository? = null

        @JvmStatic
        fun getInstance(
                localDataSource: OfferDataSource,
                remoteDataSource: OfferDataSource
        ): OfferRepository {
            if (INSTANCE == null) {
                INSTANCE = OfferRepository(localDataSource, remoteDataSource)
            }
            return INSTANCE!!
        }
    }
    private val localDataSource: OfferDataSource
    private val remoteDataSource: OfferDataSource

    /**
     * This is how items will be most frequently accesed, so this is cached
     * Offer map per store
     */
    private val offerCache = mutableMapOf<Int, MutableMap<Int, Offer>>()
    private var cacheIsDirty = false

    private constructor(localDataSource: OfferDataSource, remoteDataSource: OfferDataSource) {
        this.localDataSource = localDataSource
        this.remoteDataSource = remoteDataSource
    }

    override fun getStoreOffers(
            storeId: Int,
            callback: DataSource.LoadItemsCallback<Offer>,
            active: Boolean?
    ) {
        val storeOffers = offerCache[storeId]
        if (storeOffers != null && !cacheIsDirty) {
            callback.onItemsLoaded(storeOffers.values.toList())
            return
        }
        localDataSource.getStoreOffers(storeId, object : DataSource.LoadItemsCallback<Offer> {
            override fun onItemsLoaded(items: List<Offer>) {
                refreshCache(storeId, items)
                callback.onItemsLoaded(items)
            }

            override fun onDataNotAvailable() {
                getStoreOffersFromRemoteSource(storeId, callback)
            }
        })
    }

    private fun getStoreOffersFromRemoteSource(
            storeId: Int,
            callback: DataSource.LoadItemsCallback<Offer>,
            active: Boolean? = null
    ) {
        remoteDataSource.getStoreOffers(storeId, object : DataSource.LoadItemsCallback<Offer> {
            override fun onItemsLoaded(items: List<Offer>) {
                refreshCache(storeId, items)
                refreshLocalDataSource(items)
                callback.onItemsLoaded(items)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        }, active)
    }

    private fun refreshLocalDataSource(items: List<Offer>) {
        localDataSource.saveBulkItems(*items.toTypedArray())
    }

    private fun refreshCache(storeId: Int, items: List<Offer>) {
        if (offerCache[storeId] == null) {
            offerCache[storeId] = mutableMapOf()
        }
        offerCache[storeId]?.clear()
        items.forEach { offer ->
            offerCache[storeId]!![offer.id] = offer
        }
    }

    override fun getActiveOffers(callback: DataSource.LoadItemsCallback<Offer>, active: Boolean) {
        localDataSource.getActiveOffers(object : DataSource.LoadItemsCallback<Offer> {
            override fun onItemsLoaded(items: List<Offer>) {
                callback.onItemsLoaded(items)
            }

            override fun onDataNotAvailable() {
                remoteDataSource.getItems(object : DataSource.LoadItemsCallback<Offer> {
                    override fun onItemsLoaded(items: List<Offer>) {
                        onRemoteLoadSuccess(items, callback)
                    }

                    override fun onDataNotAvailable() {
                        callback.onDataNotAvailable()
                    }
                })
            }
        }, active)
    }

    override fun getItems(callback: DataSource.LoadItemsCallback<Offer>) {
        localDataSource.getItems(object : DataSource.LoadItemsCallback<Offer> {
            override fun onItemsLoaded(items: List<Offer>) {
                callback.onItemsLoaded(items)
            }

            override fun onDataNotAvailable() {
                remoteDataSource.getItems(object : DataSource.LoadItemsCallback<Offer> {
                    override fun onItemsLoaded(items: List<Offer>) {
                        onRemoteLoadSuccess(items, callback)
                    }

                    override fun onDataNotAvailable() {
                        callback.onDataNotAvailable()
                    }
                })
            }
        })
    }

    private fun onRemoteLoadSuccess(
            items: List<Offer>,
            callback : DataSource.LoadItemsCallback<Offer>
    ) {
        refreshLocalDataSource(items)
        callback.onItemsLoaded(items)
    }

    override fun getItem(itemId: Int, callback: DataSource.GetItemCallback<Offer>) {
        localDataSource.getItem(itemId, object : DataSource.GetItemCallback<Offer> {
            override fun onItemLoaded(item: Offer) {
                callback.onItemLoaded(item)
            }

            override fun onDataNotAvailable() {
                remoteDataSource.getItem(itemId, object : DataSource.GetItemCallback<Offer> {
                    override fun onItemLoaded(item: Offer) {
                        Log.d(TAG, "Remote offer loaded: ${item.description}")
                        callback.onItemLoaded(item)
                    }

                    override fun onDataNotAvailable() {
                        callback.onDataNotAvailable()
                    }
                })
            }
        })
    }

    override fun refreshItems() {
        cacheIsDirty = true
    }

    override fun saveItem(item: Offer) {
        localDataSource.saveItem(item)
        remoteDataSource.saveItem(item)

        if (offerCache[item.storeId] == null) {
            offerCache[item.storeId] = mutableMapOf()
        }
        offerCache[item.storeId]?.put(item.id, item)
    }

    override fun saveBulkItems(vararg items: Offer) {
        localDataSource.saveBulkItems(*items)
        remoteDataSource.saveBulkItems(*items)

        items.forEach { offer ->
            offerCache[offer.storeId]?.put(offer.id, offer)
        }
    }

    override fun deleteAll() {
        localDataSource.deleteAll()
        remoteDataSource.deleteAll()
        offerCache.clear()
    }
}