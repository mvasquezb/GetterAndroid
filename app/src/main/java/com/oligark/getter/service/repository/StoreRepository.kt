package com.oligark.getter.service.repository

import android.util.Log
import com.oligark.getter.service.model.Store
import com.oligark.getter.service.repository.source.DataSource
import com.oligark.getter.service.repository.source.StoreDataSource

/**
 * Store repository for all store loading
 * Potential for Dependency Injection
 */
class StoreRepository : StoreDataSource {
    companion object {
        val TAG = StoreRepository::class.java.simpleName

        @JvmStatic private var INSTANCE: StoreRepository? = null

        @JvmStatic
        fun getInstance(
                localDataSource: StoreDataSource,
                remoteDataSource: StoreDataSource
        ): StoreRepository {
            if (INSTANCE == null) {
                INSTANCE = StoreRepository(localDataSource, remoteDataSource)
            }
            return INSTANCE!!
        }
    }
    private val localDataSource: StoreDataSource
    private val remoteDataSource: StoreDataSource

    private val cache = LinkedHashMap<Int, Store>()
    private var cacheIsDirty = false

    private constructor(localDataSource: StoreDataSource, remoteDataSource: StoreDataSource) {
        this.localDataSource = localDataSource
        this.remoteDataSource = remoteDataSource
    }

    override fun getItems(callback: DataSource.LoadItemsCallback<Store>) {
        if (cache.isNotEmpty() && !cacheIsDirty) {
            Log.e(TAG, "Cache not empty: ${cache.values}")
            callback.onItemsLoaded(cache.values.toList())
            return
        }

        if (cacheIsDirty) {
            Log.e(TAG, "Getting items from remote source")
            getItemsFromRemoteDataSource(callback)
        } else {
            localDataSource.getItems(object : DataSource.LoadItemsCallback<Store> {
                override fun onItemsLoaded(items: List<Store>) {
                    Log.e(TAG, "Items loaded from local storage")
                    Log.e(TAG, "Items: ${items.size} - $items")
                    refreshCache(items)
                    callback.onItemsLoaded(cache.values.toList())
                }

                override fun onDataNotAvailable() {
                    Log.e(TAG, "Items loaded from remote storage")
                    getItemsFromRemoteDataSource(callback)
                }
            })
        }
    }

    private fun refreshCache(items: List<Store>) {
        cache.clear()
        items.forEach {
            cache[it.id] = it
        }
        cacheIsDirty = false
    }

    private fun getItemsFromRemoteDataSource(callback: DataSource.LoadItemsCallback<Store>) {
        remoteDataSource.getItems(object : DataSource.LoadItemsCallback<Store> {
            override fun onItemsLoaded(items: List<Store>) {
                refreshCache(items)
                refreshLocalDataSource(items)
                callback.onItemsLoaded(cache.values.toList())
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    private fun refreshLocalDataSource(items: List<Store>) {
        localDataSource.deleteAll()
        items.forEach {
            localDataSource.saveItem(it)
        }
    }

    override fun getItem(itemId: Int, callback: DataSource.GetItemCallback<Store>) {
        Log.d(TAG, "Before getting item")

        val cached = cache[itemId]
        if (cached != null && !cacheIsDirty) {
            callback.onItemLoaded(cached)
            return
        }
        Log.d(TAG, "Before getting local item")

        // Load from alternate data sources
        localDataSource.getItem(itemId, object : DataSource.GetItemCallback<Store> {
            override fun onItemLoaded(item: Store) {
                cache[item.id] = item
                Log.d(TAG, "Local item loaded")
                callback.onItemLoaded(item)
            }

            override fun onDataNotAvailable() {
                remoteDataSource.getItem(itemId, object : DataSource.GetItemCallback<Store> {
                    override fun onItemLoaded(item: Store) {
                        cache[item.id] = item
                        Log.d(TAG, "Remote item loaded")
                        callback.onItemLoaded(item)
                    }

                    override fun onDataNotAvailable() {
                        Log.d(TAG, "Remote data not available")
                        callback.onDataNotAvailable()
                    }
                })
            }
        })
    }

    override fun refreshItems() {
        cacheIsDirty = true
    }

    override fun saveItem(item: Store) {
        remoteDataSource.saveItem(item)
        localDataSource.saveItem(item)

        // Update cache
        cache[item.id] = item
    }

    override fun deleteAll() {
        remoteDataSource.deleteAll()
        localDataSource.deleteAll()

        cache.clear()
    }
}