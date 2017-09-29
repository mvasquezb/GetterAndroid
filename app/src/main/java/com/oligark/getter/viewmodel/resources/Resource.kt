package com.oligark.getter.viewmodel.resources

/**
 * Created by pmvb on 17-09-26.
 */
open class BaseResource<T>(
        var items: List<T>,
        var loadState: LoadState = LoadState.LOADING
) {
    enum class LoadState {
        LOADING,
        COMPLETED,
        SUCCESS,
        ERROR
    }
}