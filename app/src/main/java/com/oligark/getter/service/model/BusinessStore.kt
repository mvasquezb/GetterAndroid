package com.oligark.getter.service.model

import android.arch.persistence.room.Entity

/**
 * Created by pmvb on 17-09-25.
 */
@Entity
class BusinessStore(
        id: Int,
        businessId: Int,
        latitude: Double,
        longitude: Double,
        val businessName: String,
        val businessLogoUrl: String
) : Store(id, businessId, latitude, longitude)