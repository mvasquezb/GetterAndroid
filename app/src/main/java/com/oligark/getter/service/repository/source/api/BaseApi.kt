package com.oligark.getter.service.repository.source.api

/**
 * Created by pmvb on 17-09-25.
 */
interface BaseApi {
    companion object {
        val BASE_URL: String
            get() = "http://35.192.224.154"
    }
}