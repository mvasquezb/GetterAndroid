package com.oligark.getter.service.repository.source.remote

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by pmvb on 17-09-30.
 */
class DateJsonAdapter {
    companion object {
        val DATE_FORMAT = "y-M-d H:m:s"
    }

    val dateFormat = SimpleDateFormat(DATE_FORMAT)

    @ToJson fun toTimestamp(value: Date): String = dateFormat.format(value)

    @FromJson fun fromTimestamp(value: String): Date = dateFormat.parse(value)
}