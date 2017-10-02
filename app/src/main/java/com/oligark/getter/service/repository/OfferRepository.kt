package com.oligark.getter.service.repository

import android.util.Log
import com.oligark.getter.service.model.Offer
import com.oligark.getter.service.repository.source.DataSource
import com.oligark.getter.service.repository.source.OfferDataSource

/**
 * Created by pmvb on 17-09-29.
 */
class OfferRepository private constructor(
        private val localDataSource: OfferDataSource,
        private val remoteDataSource: OfferDataSource
) : OfferDataSource {

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

    /**
     * This is how items will be most frequently accesed, so this is cached
     * Offer map per store
     */
    private val offerCache = mutableMapOf<Int, MutableMap<Int, Offer>>()
    private var cacheIsDirty = false

    override fun getStoreOffers(
            storeId: Int,
            callback: DataSource.LoadItemsCallback<Offer>,
            active: Boolean?,
            productInfo: Boolean,
            forceUpdate: Boolean
    ) {
        val storeOffers = offerCache[storeId]
        if (storeOffers != null && !cacheIsDirty && !forceUpdate) {
            Log.e(TAG, "StoreOffers loaded from cache")
            callback.onItemsLoaded(storeOffers.values.toList())
            return
        }
        if (forceUpdate) {
            getStoreOffersFromRemoteSource(storeId, callback, active, productInfo)
            return
        }

        localDataSource.getStoreOffers(storeId, object : DataSource.LoadItemsCallback<Offer> {
            override fun onItemsLoaded(items: List<Offer>) {
                Log.e(TAG, "StoreOffers loaded from local storage")
                refreshCache(storeId, items)
                callback.onItemsLoaded(items)
            }

            override fun onDataNotAvailable() {
                getStoreOffersFromRemoteSource(storeId, callback, active)
            }
        }, active, productInfo, forceUpdate)
    }

    private fun getStoreOffersFromRemoteSource(
            storeId: Int,
            callback: DataSource.LoadItemsCallback<Offer>,
            active: Boolean? = null,
            productInfo: Boolean = true
    ) {
        remoteDataSource.getStoreOffers(storeId, object : DataSource.LoadItemsCallback<Offer> {
            override fun onItemsLoaded(items: List<Offer>) {
                Log.e(TAG, "StoreOffers loaded from remote storage")
                if (items.isEmpty()) {
                    Log.e(TAG, "Offers: ${items.size}")
                } else {
                    Log.e(TAG, "Offers: ${items.size} ${items.first().description}")
                }
                refreshCache(storeId, items)
                refreshLocalDataSource(items)
                callback.onItemsLoaded(items)
            }

            override fun onDataNotAvailable() {
                Log.e(TAG, "Failed to load StoreOffers from remote storage")
                callback.onDataNotAvailable()
            }
        }, active, productInfo)
    }

    private fun refreshLocalDataSource(items: List<Offer>) {
        localDataSource.deleteAll()
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

    override fun getActiveOffers(
            callback: DataSource.LoadItemsCallback<Offer>,
            active: Boolean,
            productInfo: Boolean,
            forceUpdate: Boolean
    ) {
        if (forceUpdate) {
            getRemoteActiveOffers(callback, active, productInfo)
            return
        }
        localDataSource.getActiveOffers(object : DataSource.LoadItemsCallback<Offer> {
            override fun onItemsLoaded(items: List<Offer>) {
                callback.onItemsLoaded(items)
            }

            override fun onDataNotAvailable() {
                getRemoteActiveOffers(callback, active, productInfo)
            }
        }, active, productInfo, forceUpdate)
    }

    private fun getRemoteActiveOffers(
            callback: DataSource.LoadItemsCallback<Offer>,
            active: Boolean,
            productInfo: Boolean
    ) {
        remoteDataSource.getActiveOffers(object : DataSource.LoadItemsCallback<Offer> {
            override fun onItemsLoaded(items: List<Offer>) {
                onRemoteLoadSuccess(items, callback)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        }, active, productInfo)
    }

    override fun getItems(
            callback: DataSource.LoadItemsCallback<Offer>,
            forceUpdate: Boolean
    ) {
        if (forceUpdate) {
            loadRemoteItems(callback)
            return
        }
        localDataSource.getItems(object : DataSource.LoadItemsCallback<Offer> {
            override fun onItemsLoaded(items: List<Offer>) {
                callback.onItemsLoaded(items)
            }

            override fun onDataNotAvailable() {
                loadRemoteItems(callback)
            }
        })
    }

    private fun loadRemoteItems(callback: DataSource.LoadItemsCallback<Offer>) {
        remoteDataSource.getItems(object : DataSource.LoadItemsCallback<Offer> {
            override fun onItemsLoaded(items: List<Offer>) {
                onRemoteLoadSuccess(items, callback)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
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

    override fun getItem(
            itemId: Int,
            callback: DataSource.GetItemCallback<Offer>,
            forceUpdate: Boolean
    ) {
        if (forceUpdate) {
            getSingleRemoteItem(itemId, callback)
            return
        }

        localDataSource.getItem(itemId, object : DataSource.GetItemCallback<Offer> {
            override fun onItemLoaded(item: Offer) {
                callback.onItemLoaded(item)
            }

            override fun onDataNotAvailable() {
                getSingleRemoteItem(itemId, callback)
            }
        })
    }

    private fun getSingleRemoteItem(itemId: Int, callback: DataSource.GetItemCallback<Offer>) {
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