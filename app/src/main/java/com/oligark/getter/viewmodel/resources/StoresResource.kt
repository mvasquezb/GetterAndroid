package com.oligark.getter.viewmodel.resources

import com.oligark.getter.service.model.Store

/**
 * Created by pmvb on 17-09-26.
 */
data class StoresResource(
        var stores: List<Store>,
        var loadState: LoadState = BaseResource.LoadState.LOADING
) : BaseResource()