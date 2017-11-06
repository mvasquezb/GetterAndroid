package com.oligark.getter.service.repository.source.remote

import com.oligark.getter.service.model.ProductCategory
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

/**
 * Created by pmvb on 17-11-06.
 */
class ProductCategoryJsonAdapter {
    @FromJson fun fromJson(category: ProductCategoryJson): ProductCategory =
            ProductCategory(category.id, category.slug, category.name, category.image_url)

    @ToJson fun toJson(category: ProductCategory): ProductCategoryJson =
            ProductCategoryJson(category.id, category.slug, category.name, category.imageUrl)
}