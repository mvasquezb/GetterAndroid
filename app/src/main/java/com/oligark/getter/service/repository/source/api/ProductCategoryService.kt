package com.oligark.getter.service.repository.source.api

import com.oligark.getter.service.model.ProductCategory
import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by pmvb on 17-11-03.
 */
interface ProductCategoryService {
    @GET("/api/product-categories")
    fun getCategories(): Call<List<ProductCategory>>
}