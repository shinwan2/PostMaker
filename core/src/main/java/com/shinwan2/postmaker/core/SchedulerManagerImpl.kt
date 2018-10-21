package com.shinwan2.postmaker.core

import com.shinwan2.postmaker.domain.SchedulerManager
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SchedulerManagerImpl : SchedulerManager {
    override val uiThreadScheduler: Scheduler
        get() = AndroidSchedulers.mainThread()

    override val backgroundThreadScheduler: Scheduler
        get() = Schedulers.io()
}