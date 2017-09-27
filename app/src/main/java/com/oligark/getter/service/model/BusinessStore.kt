package com.oligark.getter.service.model

/**
 * Created by pmvb on 17-09-25.
 */
class BusinessStore(
        id: Int,
        businessId: Int,
        latitude: Float,
        longitude: Float,
        val businessName: String,
        val businessLogoUrl: String
) : Store(id, businessId, latitude, longitude)