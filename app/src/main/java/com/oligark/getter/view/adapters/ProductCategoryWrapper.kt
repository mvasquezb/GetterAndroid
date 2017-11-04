package com.oligark.getter.view.adapters

import com.oligark.getter.service.model.ProductCategory

/**
 * Created by pmvb on 17-11-04.
 */
class ProductCategoryWrapper(productCategory: ProductCategory) {
    val category = productCategory
    var selected = false
}