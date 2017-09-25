package com.oligark.getter.service.repository.source.local

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.oligark.getter.service.model.BusinessStore
import com.oligark.getter.service.model.Store

/**
 * Created by pmvb on 17-09-25.
 */
@Dao
interface StoresDao {
    /**
     * Get all stores
     */
    @Query("select * from stores")
    fun getStores(): LiveData<List<Store>>

    /**
     * Get all stores with business name and logo
     */
    @Query("select stores.*, businesses.name as businessName, businesses.logo_url as businessLogoUrl" +
            "from stores" +
            "left join businesses on businesses.id = stores.business_id")
    fun getBusinessStores(): LiveData<List<BusinessStore>>

    /**
     * Get store by id
     */
    @Query("select * from stores where id = :storeId")
    fun getStore(storeId: Int): LiveData<Store>

    /**
     * Get store by id with business name and logo
     */
    @Query("select stores.*, businesses.name as businessName, businesses.logo_url as businessLogoUrl" +
            "from stores" +
            "left join businesses on businesses.id = stores.business_id and stores.id = :storeId")
    fun getBusinessStore(storeId: Int): LiveData<BusinessStore>

    /**
     * Insert a store
     */
    // Not needed for now
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertStore(store: Store)


}