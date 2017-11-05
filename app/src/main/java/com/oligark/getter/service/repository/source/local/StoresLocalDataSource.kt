package com.oligark.getter.service.repository.source.local

import com.oligark.getter.service.model.Store
import com.oligark.getter.service.repository.source.DataSource
import com.oligark.getter.service.repository.source.StoreDataSource
import com.oligark.getter.service.repository.source.local.db.StoreDao
import com.oligark.getter.util.AppExecutors

/**
 * Local data source for stores
 * Potential for Dependency Injection (Singleton)
 */
class StoresLocalDataSource(
        private val executors: AppExecutors,
        private val storeDao: StoreDao
) : StoreDataSource {

    override fun getItems(
            callback: DataSource.LoadItemsCallback<Store>,
            forceUpdate: Boolean
    ) {
        executors.diskIO.execute {
            val stores = storeDao.getStores()
            executors.mainThread.execute {
                if (stores.isEmpty()) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onItemsLoaded(stores)
                }
            }
        }
    }

    override fun getItem(
            itemId: Int,
            callback: DataSource.GetItemCallback<Store>,
            forceUpdate: Boolean
    ) {
        executors.diskIO.execute {
            val store = storeDao.getStore(itemId)
            executors.mainThread.execute {
                if (store == null) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onItemLoaded(store)
                }
            }
        }
    }

    override fun refreshItems() {
        // Do nothing
    }

    override fun saveItem(item: Store) {
        executors.diskIO.execute {
            storeDao.insertStore(item)
        }
    }

    override fun saveBulkItems(vararg items: Store) {
        executors.diskIO.execute {
            storeDao.insertAll(*items)
        }
    }

    override fun deleteAll() {
        executors.diskIO.execute {
            storeDao.deleteAll()
        }
    }

    // Don't filter for now, just return local items
    override fun filter(
            callback: DataSource.LoadItemsCallback<Store>,
            filters: Map<String, List<String>>
    ) {
        getItems(callback)
    }
}