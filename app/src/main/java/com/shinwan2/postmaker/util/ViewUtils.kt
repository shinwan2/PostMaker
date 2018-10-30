package com.shinwan2.postmaker.util

import android.view.View
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

private val CLICK_THROTTLE_TIMEOUT = Pair(500L, TimeUnit.MILLISECONDS)

fun View.throttleClicks(): Observable<Unit> {
    return this.clicks()
        .throttleFirst(
            CLICK_THROTTLE_TIMEOUT.first,
            CLICK_THROTTLE_TIMEOUT.second,
            Schedulers.io()
        )
        .observeOn(AndroidSchedulers.mainThread())
}