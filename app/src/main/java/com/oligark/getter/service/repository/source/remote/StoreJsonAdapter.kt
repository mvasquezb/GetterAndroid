package com.oligark.getter.service.repository.source.remote

import com.oligark.getter.service.model.Store
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

/**
 * Created by pmvb on 17-09-26.
 */
class StoreJsonAdapter {
    @FromJson fun fromJson(store: StoreJson): Store {
        return Store(
                store.id,
                store.business_id,
                store.latitude,
                store.longitude,
                store.business_name,
                store.business_logo_url
        )
    }

    @ToJson fun toJson(store: Store): StoreJson {
        return StoreJson(
                store.id,
                store.businessId,
                store.latitude,
                store.longitude,
                store.businessName,
                store.businessLogoUrl
        )
    }
}