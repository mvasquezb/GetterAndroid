package com.oligark.getter.search.converters

import com.squareup.moshi.Types
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Created by pmvb on 17-11-09.
 */
class ResponseConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
            type: Type?,
            annotations: Array<out Annotation>?,
            retrofit: Retrofit?
    ): Converter<ResponseBody, *>? {
        val responseType = Types.newParameterizedType(Response::class.java, type)
        val delegate = retrofit?.nextResponseBodyConverter<Response>(
                this,
                responseType,
                annotations
        )
        return ResponseConverter(delegate)
    }
}