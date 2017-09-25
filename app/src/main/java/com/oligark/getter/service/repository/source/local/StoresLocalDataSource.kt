package com.oligark.getter.service.repository.source.local

import com.oligark.getter.service.model.Store
import com.oligark.getter.service.repository.source.DataSource
import com.oligark.getter.service.repository.source.StoreDataSource

/**
 * Local data source for stores
 * Potential for Dependency Injection (Singleton)
 */
class StoresLocalDataSource : StoreDataSource {
    override fun getItems(callback: DataSource.LoadItemsCallback<Store>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItem(itemId: Int, callback: DataSource.GetItemCallback<Store>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun refreshItems() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveItem(item: Store) {
        // Do nothing
    }

    override fun deleteAll() {
        // Do nothing
    }
}