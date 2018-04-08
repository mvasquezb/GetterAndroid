package com.oligark.getter.search.model

import com.oligark.getter.service.model.Store
import com.oligark.getter.service.model.Offer

/**
 * Created by pmvb on 17-11-09.
 */
class SearchResult(
        val stores: List<Store>,
        val offers: List<Offer>
){

}