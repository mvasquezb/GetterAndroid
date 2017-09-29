package com.oligark.getter.service.repository.source.local

import com.oligark.getter.service.model.Offer
import com.oligark.getter.service.repository.source.DataSource
import com.oligark.getter.service.repository.source.OfferDataSource
import com.oligark.getter.service.repository.source.local.db.OfferDao
import com.oligark.getter.util.AppExecutors

/**
 * Created by pmvb on 17-09-28.
 */
class OfferLocalDataSource : OfferDataSource {
    private val offerDao: OfferDao
    private var executors: AppExecutors

    constructor(executors: AppExecutors, offerDao: OfferDao) {
        this.executors = executors
        this.offerDao = offerDao
    }

    override fun getItems(callback: DataSource.LoadItemsCallback<Offer>) {
        executors.diskIO.execute {
            val offers = offerDao.getOffers()
            if (offers.isEmpty()) {
                callback.onDataNotAvailable()
            } else {
                callback.onItemsLoaded(offers)
            }
        }
    }

    override fun getItem(itemId: Int, callback: DataSource.GetItemCallback<Offer>) {
        executors.diskIO.execute {
            val offer = offerDao.getOffer(itemId)
            if (offer == null) {
                callback.onDataNotAvailable()
            } else {
                callback.onItemLoaded(offer)
            }
        }
    }

    override fun refreshItems() {
        // Do nothing
    }

    override fun saveItem(item: Offer) {
        executors.diskIO.execute {
            offerDao.insert(item)
        }
    }

    override fun saveBulkItems(vararg items: Offer) {
        executors.diskIO.execute {
            offerDao.bulkInsert(*items)
        }
    }

    override fun deleteAll() {
        executors.diskIO.execute {
            offerDao.deleteAll()
        }
    }

    override fun getStoreOffers(
            storeId: Int,
            callback: DataSource.LoadItemsCallback<Offer>,
            active: Boolean?
    ) {
        executors.diskIO.execute {
            val offers = if (active == null) {
                offerDao.getStoreOffers(storeId)
            } else {
                offerDao.getActiveStoreOffers(storeId, active)
            }
            if (offers.isEmpty()) {
                callback.onDataNotAvailable()
            } else {
                callback.onItemsLoaded(offers)
            }
        }
    }

    override fun getActiveOffers(
            callback: DataSource.LoadItemsCallback<Offer>,
            active: Boolean
    ) {
        executors.diskIO.execute {
            val offers = offerDao.getActiveOffers(active)
            if (offers.isEmpty()) {
                callback.onDataNotAvailable()
            } else {
                callback.onItemsLoaded(offers)
            }
        }
    }
}