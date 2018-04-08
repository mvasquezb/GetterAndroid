package com.oligark.getter.search.service

import com.oligark.getter.search.model.SearchResult

/**
 * Created by pmvb on 17-11-09.
 */
interface SearchCallback {
    fun onSearchComplete(results: SearchResult)
    fun onSearchFailed()
}