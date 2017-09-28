package com.oligark.getter.service.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import java.util.*

/**
 * 'Store' foreign key only, no relationship to product or offer type yet
 */
@Entity(tableName = "offers",
        foreignKeys = arrayOf(ForeignKey(
                entity = Store::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("store_id")
        ))
)
class Offer(
        @PrimaryKey
        val id: Int,
        @ColumnInfo(name = "start_date")
        val startDate: Date,
        @ColumnInfo(name = "end_date")
        val endDate: Date,
        val description: String,
        @ColumnInfo(name = "product_id")
        val productId: Int,
        @ColumnInfo(name = "store_id")
        val storeId: Int,
        @ColumnInfo(name = "offer_type")
        val offerType: Int,
        val active: Boolean
)