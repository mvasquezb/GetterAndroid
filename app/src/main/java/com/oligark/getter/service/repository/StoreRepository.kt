package com.oligark.getter.service.repository

import android.util.Log
import com.oligark.getter.service.model.Store
import com.oligark.getter.service.repository.source.DataSource
import com.oligark.getter.service.repository.source.StoreDataSource

/**
 * Store repository for all store loading
 * Potential for Dependency Injection
 */
class StoreRepository private constructor(
        private val localDataSource: StoreDataSource,
        private val remoteDataSource: StoreDataSource
) : StoreDataSource {
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

    private val cache = LinkedHashMap<Int, Store>()
    private var cacheIsDirty = false

    override fun getItems(
            callback: DataSource.LoadItemsCallback<Store>,
            forceUpdate: Boolean
    ) {
        if (cache.isNotEmpty() && !cacheIsDirty && !forceUpdate) {
            Log.e(TAG, "Cache not empty: ${cache.values}")
            callback.onItemsLoaded(cache.values.toList())
            return
        }

        if (cacheIsDirty || forceUpdate) {
            Log.e(TAG, "Getting items from remote source")
            getItemsFromRemoteDataSource(callback)
        } else {
            localDataSource.getItems(object : DataSource.LoadItemsCallback<Store> {
                override fun onItemsLoaded(items: List<Store>) {
                    Log.d(TAG, "Items loaded from local storage")
                    Log.d(TAG, "Items: ${items.size} - $items")
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
        localDataSource.saveBulkItems(*items.toTypedArray())
    }

    override fun getItem(
            itemId: Int,
            callback: DataSource.GetItemCallback<Store>,
            forceUpdate: Boolean
    ) {
        Log.d(TAG, "Before getting item")

        val cached = cache[itemId]
        if (cached != null && !cacheIsDirty && !forceUpdate) {
            callback.onItemLoaded(cached)
            return
        }
        Log.d(TAG, "Before getting local item")

        if (forceUpdate) {
            getSingleRemoteItem(itemId, callback)
            return
        }

        // Load from alternate data sources
        localDataSource.getItem(itemId, object : DataSource.GetItemCallback<Store> {
            override fun onItemLoaded(item: Store) {
                cache[item.id] = item
                Log.d(TAG, "Local item loaded")
                callback.onItemLoaded(item)
            }

            override fun onDataNotAvailable() {
                getSingleRemoteItem(itemId, callback)
            }
        })
    }

    private fun getSingleRemoteItem(itemId: Int, callback: DataSource.GetItemCallback<Store>) {
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

    override fun refreshItems() {
        cacheIsDirty = true
    }

    override fun saveItem(item: Store) {
        remoteDataSource.saveItem(item)
        localDataSource.saveItem(item)

        // Update cache
        cache[item.id] = item
    }

    override fun saveBulkItems(vararg items: Store) {
        remoteDataSource.saveBulkItems(*items)
        localDataSource.saveBulkItems(*items)

        items.forEach { item ->
            cache[item.id] = item
        }
    }

    override fun deleteAll() {
        remoteDataSource.deleteAll()
        localDataSource.deleteAll()

        cache.clear()
    }

    override fun filter(
            callback: DataSource.LoadItemsCallback<Store>,
            filters: Map<String, List<String>>
    ) {
//        localDataSource.filter(object : DataSource.LoadItemsCallback<Store> {
//            override fun onItemsLoaded(items: List<Store>) {
//                Log.d(TAG, "Items loaded from local storage")
//                Log.d(TAG, "Items: ${items.size} - $items")
//                refreshCache(items)
//                callback.onItemsLoaded(cache.values.toList())
//            }
//
//            override fun onDataNotAvailable() {
//                Log.e(TAG, "Items loaded from remote storage")
                remoteDataSource.filter(callback, filters = filters)
//            }
//        }, filters = filters)
    }
}