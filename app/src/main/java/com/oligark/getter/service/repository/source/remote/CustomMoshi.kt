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
            .add(StoreJsonAdapter())
            .add(OfferJsonAdapter())
            .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
            .build()
}