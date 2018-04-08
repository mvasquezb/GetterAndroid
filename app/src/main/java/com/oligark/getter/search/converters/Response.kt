package com.oligark.getter.search.converters

import com.oligark.getter.search.model.SearchResult

/**
 * Created by pmvb on 17-11-09.
 */
class Response(
        val status: Int,
        val payload: SearchResult
)