package com.oligark.getter.search.converters

import com.oligark.getter.search.model.SearchResult
import okhttp3.ResponseBody
import retrofit2.Converter

/**
 * Created by pmvb on 17-11-09.
 */
class ResponseConverter(
        val delegate: Converter<ResponseBody, Response>?
) : Converter<ResponseBody, SearchResult> {
    override fun convert(value: ResponseBody): SearchResult? {
        val response = delegate?.convert(value)
        return response?.payload
    }
}