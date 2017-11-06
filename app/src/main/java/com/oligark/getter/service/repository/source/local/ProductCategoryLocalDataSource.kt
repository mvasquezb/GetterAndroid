package com.oligark.getter.service.repository.source.local

import com.oligark.getter.service.model.ProductCategory
import com.oligark.getter.service.repository.source.DataSource
import com.oligark.getter.service.repository.source.ProductCategoryDataSource
import com.oligark.getter.service.repository.source.local.db.ProductCategoryDao
import com.oligark.getter.service.repository.source.remote.ProductCategoryRemoteDataSource
import com.oligark.getter.util.AppExecutors

/**
 * Created by pmvb on 17-11-03.
 */

class ProductCategoryLocalDataSource(
        private val executors: AppExecutors,
        private val productCategoryDao: ProductCategoryDao
) : ProductCategoryDataSource {
    override fun getItems(callback: DataSource.LoadItemsCallback<ProductCategory>, forceUpdate: Boolean) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItem(itemId: Int, callback: DataSource.GetItemCallback<ProductCategory>, forceUpdate: Boolean) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun refreshItems() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveItem(item: ProductCategory) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveBulkItems(vararg items: ProductCategory) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAll() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}