package com.shinwan2.postmaker.domain

import io.reactivex.Scheduler

interface SchedulerManager {
    val uiThreadScheduler: Scheduler
    val backgroundThreadScheduler: Scheduler
}