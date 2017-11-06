package com.oligark.getter.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.oligark.getter.service.model.ProductCategory
import com.oligark.getter.service.repository.ProductCategoryRepository
import com.oligark.getter.service.repository.source.DataSource
import com.oligark.getter.service.repository.source.remote.ProductCategoryRemoteDataSource
import com.oligark.getter.viewmodel.resources.DataResource

/**
 * Created by pmvb on 17-11-03.
 */
class FiltersViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        val TAG = FiltersViewModel::class.java.simpleName
    }

    // For now, only use remote source
    private val dataSource = ProductCategoryRemoteDataSource()
    val productCategoryRepository = ProductCategoryRepository.getInstance(
            dataSource,
            dataSource
    )

    val productCategories = MutableLiveData<DataResource<ProductCategory>>()
    val selectedProductCategories = HashMap<Int, ProductCategory>()
    var filtersApplied = MutableLiveData<Boolean>()

    var priceMinValue = 0
        private set
    var priceMaxValue = 1000
        private set

    var priceRange: Pair<Int?, Int?> = (null to null)
        private set
    val priceRangeMin: Int?
        get() = priceRange.first
    val priceRangeMax: Int?
        get() = priceRange.second

    fun init() {
        productCategories.value = DataResource(listOf(), DataResource.LoadState.LOADING)
        productCategoryRepository.getItems(object : DataSource.LoadItemsCallback<ProductCategory> {
            override fun onItemsLoaded(items: List<ProductCategory>) {
                productCategories.value = DataResource(items, DataResource.LoadState.SUCCESS)
            }
            override fun onDataNotAvailable() {
                productCategories.value = DataResource(listOf(), DataResource.LoadState.ERROR)
            }
        })

        filtersApplied.value = false
    }

    fun selectCategory(category: ProductCategory) {
        val selectedCategories = selectedProductCategories
        if (selectedProductCategories.contains(category.id)) {
            selectedCategories.remove(category.id)
        } else {
            selectedProductCategories.put(category.id, category)
        }
    }

    fun clearAll() {
        filtersApplied.value = false
        selectedProductCategories.clear()
        priceRange = (null to null)
    }

    fun applyFilters() {
        filtersApplied.value = true
        Log.e(TAG, "PriceRange: $priceRange")
    }

    fun updatePriceRange(minValue: Int?, maxValue: Int?) {
        if (minValue != null && minValue < priceMinValue) {
            return
        }
        if (maxValue != null && maxValue > priceMaxValue) {
            return
        }
        priceRange = (minValue to maxValue)
    }

    fun updatePriceRange(range: Pair<Int, Int>) {
        updatePriceRange(range.first, range.second)
    }
}