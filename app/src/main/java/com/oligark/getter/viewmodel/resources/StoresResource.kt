package com.oligark.getter.viewmodel.resources

import com.oligark.getter.service.model.BusinessStore

/**
 * Created by pmvb on 17-09-26.
 */
data class StoresResource(
        var stores: List<BusinessStore>,
        var loadState: LoadState = BaseResource.LoadState.LOADING
) : BaseResource()