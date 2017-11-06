package com.oligark.getter.service.repository.source.remote

import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.Rfc3339DateJsonAdapter
import java.util.*

/**
 * Should be injected singleton
 */
object CustomMoshi {
    val INSTANCE = Moshi.Builder()
            .add(DateJsonAdapter())
            .add(StoreJsonAdapter())
            .add(OfferJsonAdapter())
            .add(ProductCategoryJsonAdapter())
            .build()
}