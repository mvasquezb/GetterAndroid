package com.oligark.getter.service.repository.source.remote

import com.oligark.getter.service.model.Offer
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

/**
 * Created by pmvb on 17-09-29.
 */
class OfferJsonAdapter {
    @FromJson fun fromJson(offer: OfferJson): Offer {
        return Offer(
                offer.id,
                offer.start_date,
                offer.end_date,
                offer.description,
                offer.product_id,
                offer.store_id,
                offer.offer_type,
                offer.active
        )
    }

    @ToJson fun toJson(offer: Offer): OfferJson {
        return OfferJson(
                offer.id,
                offer.startDate,
                offer.endDate,
                offer.description,
                offer.productId,
                offer.storeId,
                offer.offerType,
                offer.active
        )
    }
}