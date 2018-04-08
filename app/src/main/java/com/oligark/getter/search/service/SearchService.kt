package com.oligark.getter.search.service

import com.oligark.getter.search.model.SearchResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by pmvb on 17-11-09.
 */
interface SearchService {
    @GET("/api/search")
    fun search(@Query("value") term: String): Call<SearchResult>
}