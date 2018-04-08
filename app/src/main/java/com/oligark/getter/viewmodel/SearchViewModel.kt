package com.oligark.getter.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.oligark.getter.search.converters.ResponseConverterFactory
import com.oligark.getter.search.model.SearchResult
import com.oligark.getter.search.service.SearchCallback
import com.oligark.getter.search.service.SearchService
import com.oligark.getter.service.repository.source.api.BaseApi
import com.oligark.getter.service.repository.source.remote.CustomMoshi
import com.oligark.getter.viewmodel.resources.DataResource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Created by pmvb on 17-11-09.
 */
class SearchViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        val TAG = SearchViewModel::class.java.simpleName
    }

    val searchService = Retrofit.Builder()
            .baseUrl(BaseApi.BASE_URL)
            .addConverterFactory(ResponseConverterFactory())
            .addConverterFactory(MoshiConverterFactory.create(CustomMoshi.INSTANCE)) // Should be custom
            .build()
            .create(SearchService::class.java)

    val searchResults = DataResource<SearchResult>(listOf(), DataResource.LoadState.LOADING)

    fun searchStores(term: String, callback: SearchCallback) {
        searchService.search(term).enqueue(object : Callback<SearchResult> {
            override fun onFailure(call: Call<SearchResult>?, t: Throwable?) {
                Log.e(TAG, "Store search error: $t")
                callback.onSearchFailed()
            }

            override fun onResponse(call: Call<SearchResult>?, response: Response<SearchResult>?) {
                if (response == null || response.isSuccessful.not()) {
                    callback.onSearchFailed()
                    return
                }
                val results = response.body()
                if (results == null) {
                    callback.onSearchFailed()
                    return
                }
                callback.onSearchComplete(results)
            }
        })
    }
}