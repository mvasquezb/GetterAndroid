package com.oligark.getter.search.model

import android.os.Parcel
import android.os.Parcelable
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion

/**
 * Created by pmvb on 17-11-09.
 */
class StoreSearchSuggestion(
        val id: Int,
        val latitude: Double,
        val longitude: Double,
        val businessId: Int,
        val businessName: String,
        val businessLogoUrl: String
) : SearchSuggestion {

    companion object {
        val CREATOR = object : Parcelable.Creator<StoreSearchSuggestion> {
            override fun newArray(size: Int): Array<StoreSearchSuggestion?> = arrayOfNulls(size)

            override fun createFromParcel(parcel: Parcel?): StoreSearchSuggestion? {
                if (parcel == null) {
                    return null
                }
                return StoreSearchSuggestion(
                        parcel.readInt(),
                        parcel.readDouble(),
                        parcel.readDouble(),
                        parcel.readInt(),
                        parcel.readString(),
                        parcel.readString()
                )
            }
        }
    }

    override fun writeToParcel(parcel: Parcel?, p1: Int) {
        parcel?.writeInt(id)
        parcel?.writeDouble(latitude)
        parcel?.writeDouble(longitude)
        parcel?.writeInt(businessId)
        parcel?.writeString(businessName)
        parcel?.writeString(businessLogoUrl)
    }

    override fun describeContents(): Int = 0

    override fun getBody(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}