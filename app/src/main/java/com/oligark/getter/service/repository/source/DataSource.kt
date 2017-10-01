package com.oligark.getter.service.repository.source

/**
 * Created by pmvb on 17-09-25.
 */
interface DataSource<T> {
    interface LoadItemsCallback<in T> {
        fun onItemsLoaded(items: List<T>)
        fun onDataNotAvailable()
    }

    interface GetItemCallback<in T> {
        fun onItemLoaded(item: T)
        fun onDataNotAvailable()
    }

    fun getItems(callback: LoadItemsCallback<T>, forceUpdate: Boolean = false)

    fun getItem(itemId: Int, callback: GetItemCallback<T>, forceUpdate: Boolean = false)

    fun refreshItems()

    fun saveItem(item: T)

    fun saveBulkItems(vararg items: T)

    fun deleteAll()
}