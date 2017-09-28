package com.oligark.getter.service.repository.source.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.oligark.getter.service.model.Offer

/**
 * Created by pmvb on 17-09-28.
 */
@Dao
interface OfferDao {

    @Query("select * from offers")
    fun getOffers(): List<Offer>

    @Query("select * from offers where store_id = :storeId")
    fun getStoreOffers(storeId: Int): List<Offer>

    @Query("select * from offers where id = :offerId")
    fun getOffer(offerId: Int): Offer

    @Query("select * from offers where active = :active")
    fun getActiveOffers(active: Boolean = true): List<Offer>

    @Query("select * from offers where store_id = :storeId and active = :active")
    fun getActiveStoreOffers(storeId: Int, active: Boolean = true): List<Offer>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(offer: Offer)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg offers: Offer)

    @Query("delete from offers")
    fun deleteAll()
}