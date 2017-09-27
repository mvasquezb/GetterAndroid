package com.oligark.getter.util

import android.os.Handler
import android.os.Looper

import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Global executor pools for the whole application.
 *
 *
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */
class AppExecutors {

    companion object {
        private val THREAD_COUNT = 3
    }

    val diskIO: Executor
    val networkIO: Executor
    val mainThread: Executor

    private constructor(diskIO: Executor, networkIO: Executor, mainThread: Executor) {
        this.diskIO = diskIO
        this.networkIO = networkIO
        this.mainThread = mainThread
    }

    constructor() : this(
            DiskIOThreadExecutor(),
            Executors.newFixedThreadPool(THREAD_COUNT),
            MainThreadExecutor()
    )

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }
}
