package com.oligark.getter.service.repository.source.remote

/**
 * Created by pmvb on 17-09-26.
 */
data class StoreJson(
        val id: Int,
        val business_id: Int,
        val latitude: Double,
        val longitude: Double,
        val business_name: String,
        val business_logo_url: String
)