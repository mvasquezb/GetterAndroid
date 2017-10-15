package com.oligark.getter.service.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Business -> User relationship not necessary for this app
 *
 */
@Entity(tableName = "businesses")
class Business(
        @PrimaryKey
        val id: Int,
        val name: String,
        @ColumnInfo(name = "owner_id")
        val ownerId: Int,
        @ColumnInfo(name = "logo_url")
        val logoUrl: String
)