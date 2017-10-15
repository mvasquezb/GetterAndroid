package com.oligark.getter.service.model

import android.arch.persistence.room.*
import android.os.Parcelable

/**
 * Created by pmvb on 17-09-25.
 */

@Entity(tableName = "stores"
//        foreignKeys = arrayOf(ForeignKey(
//                entity = Business::class,
//                parentColumns = arrayOf("id"),
//                childColumns = arrayOf("business_id")
//        ))
)
open class Store(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "business_id")
    val businessId: Int,
    val latitude: Double,
    val longitude: Double,
    val businessName: String,
    val businessLogoUrl: String
) : Parcelable {
    constructor(parcel: android.os.Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readString(),
            parcel.readString()
    )

    override fun writeToParcel(parcel: android.os.Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(businessId)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeString(businessName)
        parcel.writeString(businessLogoUrl)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Store> {
        override fun createFromParcel(parcel: android.os.Parcel): Store = Store(parcel)

        override fun newArray(size: Int): Array<Store?> = arrayOfNulls(size)
    }
}