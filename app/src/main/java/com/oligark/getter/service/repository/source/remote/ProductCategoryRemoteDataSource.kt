package com.oligark.getter.service.repository.source.remote

import android.util.Log
import com.oligark.getter.service.model.ProductCategory
import com.oligark.getter.service.repository.source.DataSource
import com.oligark.getter.service.repository.source.ProductCategoryDataSource
import com.oligark.getter.service.repository.source.api.BaseApi
import com.oligark.getter.service.repository.source.api.ProductCategoryService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Created by pmvb on 17-11-03.
 */
class ProductCategoryRemoteDataSource : ProductCategoryDataSource {
    companion object {
        val TAG = ProductCategoryRemoteDataSource::class.java.simpleName
    }

    val productCategoriesService: ProductCategoryService = Retrofit.Builder()
            .baseUrl(BaseApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(CustomMoshi.INSTANCE)) // Should be custom
            .build()
            .create(ProductCategoryService::class.java)

    override fun getItems(callback: DataSource.LoadItemsCallback<ProductCategory>, forceUpdate: Boolean) {
        productCategoriesService.getCategories().enqueue(object : Callback<List<ProductCategory>> {
            override fun onResponse(call: Call<List<ProductCategory>>?, response: Response<List<ProductCategory>>?) {
                if (response == null || response.isSuccessful.not()) {
                    Log.e(TAG, "Response is null or not successfull: ${response?.code()}")
                    callback.onDataNotAvailable()
                    return
                }
                val categories = response.body()
                if (categories == null || categories.isEmpty()) {
                    Log.e(TAG, "Categories list is null or empty: $categories")
                    callback.onDataNotAvailable()
                    return
                }
                callback.onItemsLoaded(categories)
            }

            override fun onFailure(call: Call<List<ProductCategory>>?, t: Throwable?) {
                Log.e(TAG, "Error loading product categories: $t")
                callback.onDataNotAvailable()
            }
        })
    }

    override fun getItem(itemId: Int, callback: DataSource.GetItemCallback<ProductCategory>, forceUpdate: Boolean) {
        productCategoriesService.getCategory(itemId).enqueue(object : Callback<ProductCategory> {
            override fun onResponse(call: Call<ProductCategory>?, response: Response<ProductCategory>?) {
                if (response == null || response.isSuccessful.not()) {
                    callback.onDataNotAvailable()
                    return
                }
                val category = response.body()
                if (category == null) {
                    callback.onDataNotAvailable()
                    return
                }
                callback.onItemLoaded(category)
            }

            override fun onFailure(call: Call<ProductCategory>?, t: Throwable?) {
                Log.e(TAG, "Error loading category: $t")
                callback.onDataNotAvailable()
            }
        })
    }

    override fun refreshItems() {
        // Do nothing
    }

    override fun saveItem(item: ProductCategory) {
        // Do nothing
    }

    override fun saveBulkItems(vararg items: ProductCategory) {
        // Do nothing
    }

    override fun deleteAll() {
        // Do nothing
    }
}