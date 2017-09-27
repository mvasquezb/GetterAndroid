package com.oligark.getter.service.repository.source.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.oligark.getter.service.model.Business

/**
 * Created by pmvb on 17-09-25.
 */
@Dao
interface BusinessDao {

    @Query("select * from businesses")
    fun getBusinesses(): List<Business>

    @Query("select * from businesses where id = :businessId")
    fun getBusiness(businessId: Int): Business

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBusiness(business: Business)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(businesses: List<Business>)
}