package com.oligark.getter.service.repository.source.remote

import java.util.Date

class OfferJson(
        val id: Int,
        val start_date: Date,
        val end_date: Date,
        val description: String,
        val product_id: Int,
        val store_id: Int,
        val offer_type: Int,
        val active: Boolean
)