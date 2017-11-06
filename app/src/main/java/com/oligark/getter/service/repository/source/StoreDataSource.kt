package com.oligark.getter.service.repository.source

import com.oligark.getter.service.model.Store

/**
 * In case we need store specific logic
 */
interface StoreDataSource : DataSource<Store> {
    fun filter(
            callback: DataSource.LoadItemsCallback<Store>,
            filters: Map<String, List<String>>
    )
}