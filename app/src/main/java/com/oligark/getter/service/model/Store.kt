package com.oligark.getter.service.model

import android.arch.persistence.room.*

/**
 * Created by pmvb on 17-09-25.
 */

@Entity(tableName = "stores",
        foreignKeys = arrayOf(ForeignKey(
                entity = Business::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("business_id")
        ))
)
class Store(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "business_id")
    val businessId: Int,
    val latitude: Float,
    val longitude: Float
) {

}