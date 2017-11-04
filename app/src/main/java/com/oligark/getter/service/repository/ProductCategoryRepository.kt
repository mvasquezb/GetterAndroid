package com.oligark.getter.service.repository

import com.oligark.getter.service.model.ProductCategory
import com.oligark.getter.service.repository.source.DataSource
import com.oligark.getter.service.repository.source.ProductCategoryDataSource

/**
 * Created by pmvb on 17-11-03.
 */
class ProductCategoryRepository private constructor(
        private val localDataSource: ProductCategoryDataSource,
        private val remoteDataSource: ProductCategoryDataSource
) : ProductCategoryDataSource {
    companion object {
        val TAG = ProductCategoryRepository::class.java.simpleName

        @JvmStatic private var INSTANCE: ProductCategoryRepository? = null

        @JvmStatic
        fun getInstance(
                localDataSource: ProductCategoryDataSource,
                remoteDataSource: ProductCategoryDataSource
        ): ProductCategoryRepository {
            if (INSTANCE == null) {
                INSTANCE = ProductCategoryRepository(localDataSource, remoteDataSource)
            }
            return INSTANCE!!
        }
    }

    override fun getItems(callback: DataSource.LoadItemsCallback<ProductCategory>, forceUpdate: Boolean) {
        remoteDataSource.getItems(callback)
    }

    override fun getItem(itemId: Int, callback: DataSource.GetItemCallback<ProductCategory>, forceUpdate: Boolean) {
        remoteDataSource.getItem(itemId, callback)
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