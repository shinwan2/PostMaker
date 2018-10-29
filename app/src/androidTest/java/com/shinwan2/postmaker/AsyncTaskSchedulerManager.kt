package com.shinwan2.postmaker

import android.os.AsyncTask
import com.shinwan2.postmaker.domain.SchedulerManager
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AsyncTaskSchedulerManager : SchedulerManager {
    override val uiThreadScheduler: Scheduler
        get() = AndroidSchedulers.mainThread()
    override val backgroundThreadScheduler: Scheduler
        get() =  Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR)
}