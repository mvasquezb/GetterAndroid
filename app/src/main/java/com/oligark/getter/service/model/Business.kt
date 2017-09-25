package com.oligark.getter.service.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

/**
 * Created by pmvb on 17-09-25.
 */
@Entity(foreignKeys = arrayOf(ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("owner_id")
)))
class Business(
        @PrimaryKey
        val id: Int,
        val name: String,
        @ColumnInfo(name = "owner")
        val owner_id: Int,
        @ColumnInfo(name = "logo_url")
        val logoUrl: String
) {

}