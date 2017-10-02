package com.oligark.getter.service.repository.source

import com.oligark.getter.service.model.Offer

/**
 * Created by pmvb on 17-09-28.
 */
interface OfferDataSource : DataSource<Offer> {
    fun getStoreOffers(
            storeId: Int,
            callback: DataSource.LoadItemsCallback<Offer>,
            active: Boolean? = null,
            productInfo: Boolean = true,
            forceUpdate: Boolean = false
    )

    fun getActiveOffers(
            callback: DataSource.LoadItemsCallback<Offer>,
            active: Boolean = true,
            productInfo: Boolean = true,
            forceUpdate: Boolean = false
    )
}