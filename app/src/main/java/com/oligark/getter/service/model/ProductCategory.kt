package com.oligark.getter.service.model

/**
 * Created by pmvb on 17-11-01.
 */
class ProductCategory(
        val id: Int,
        val slug: String,
        val name: String,
        val imageUrl: String
) {
    fun hasSameContent(other: ProductCategory): Boolean =
            slug == other.slug && name == other.name && imageUrl == other.imageUrl
}