package com.oligark.getter.service.repository.source.remote

import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.Rfc3339DateJsonAdapter

/**
 * Should be injected singleton
 */
object CustomMoshi {
    val INSTANCE = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(StoreJsonAdapter())
            .add(OfferJsonAdapter())
            .add(Rfc3339DateJsonAdapter())
            .build()
}