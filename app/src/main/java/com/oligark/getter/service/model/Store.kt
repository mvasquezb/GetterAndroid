package com.oligark.getter.service.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

/**
 * Created by pmvb on 17-09-25.
 */

@Entity(foreignKeys = arrayOf(ForeignKey(
        entity = Business::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("business_id")
)))
class Store(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "business_id")
    val businessId: Int,
    val latitude: Float,
    val longitude: Float
) {

}