package com.oligark.getter.service.repository.source.remote

import com.oligark.getter.service.model.BusinessStore
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

/**
 * Created by pmvb on 17-09-26.
 */
class BusinessStoreJsonAdapter {
    @FromJson fun fromJson(store: BusinessStoreJson): BusinessStore {
        return BusinessStore(
                store.id,
                store.business_id,
                store.latitude,
                store.longitude,
                store.business_name,
                store.business_logo_url
        )
    }

    @ToJson fun toJson(store: BusinessStore): BusinessStoreJson {
        return BusinessStoreJson(
                store.id,
                store.businessId,
                store.latitude,
                store.longitude,
                store.businessName,
                store.businessLogoUrl
        )
    }
}