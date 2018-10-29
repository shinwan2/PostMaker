package com.shinwan2.postmaker

import com.shinwan2.postmaker.domain.SchedulerManager
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class InstantSchedulerManager : SchedulerManager {
    override val uiThreadScheduler: Scheduler
        get() = Schedulers.trampoline()
    override val backgroundThreadScheduler: Scheduler
        get() = Schedulers.trampoline()
}