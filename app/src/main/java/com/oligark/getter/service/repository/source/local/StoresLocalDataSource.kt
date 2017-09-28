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
class StoresLocalDataSource : StoreDataSource {
    private val storeDao: StoreDao
    private var executors: AppExecutors

    constructor(executors: AppExecutors, storeDao: StoreDao) {
        this.executors = executors
        this.storeDao = storeDao
    }

    override fun getItems(callback: DataSource.LoadItemsCallback<Store>) {
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

    override fun getItem(itemId: Int, callback: DataSource.GetItemCallback<Store>) {
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

    override fun deleteAll() {
        executors.diskIO.execute {
            storeDao.deleteAll()
        }
    }
}