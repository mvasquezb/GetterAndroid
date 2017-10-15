package com.oligark.getter.service.repository.source.local.db

import android.arch.persistence.room.TypeConverter
import java.util.*

/**
 * Created by pmvb on 17-09-29.
 */
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long): Date = Date(value)

    @TypeConverter
    fun toTimestamp(date: Date): Long = date.time
}