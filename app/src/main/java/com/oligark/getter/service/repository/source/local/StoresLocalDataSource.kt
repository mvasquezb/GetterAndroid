package com.oligark.getter.service.repository.source.local

import com.oligark.getter.service.model.BusinessStore
import com.oligark.getter.service.repository.source.DataSource
import com.oligark.getter.service.repository.source.StoreDataSource

/**
 * Local data source for stores
 * Potential for Dependency Injection (Singleton)
 */
class StoresLocalDataSource : StoreDataSource {
    override fun getItems(callback: DataSource.LoadItemsCallback<BusinessStore>) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        callback.onDataNotAvailable()
    }

    override fun getItem(itemId: Int, callback: DataSource.GetItemCallback<BusinessStore>) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        callback.onDataNotAvailable()
    }

    override fun refreshItems() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveItem(item: BusinessStore) {
        // Do nothing
    }

    override fun deleteAll() {
        // Do nothing
    }
}