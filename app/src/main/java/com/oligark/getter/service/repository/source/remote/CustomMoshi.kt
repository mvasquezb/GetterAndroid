package com.oligark.getter.service.repository.source.remote

import com.squareup.moshi.Moshi

/**
 * Should be injected singleton
 */
object CustomMoshi {
    val INSTANCE = Moshi.Builder()
            .add(StoreJsonAdapter())
            .build()
}