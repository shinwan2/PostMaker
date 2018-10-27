package com.shinwan2.postmaker.util

import android.view.View
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

private val CLICK_DEBOUNCE_TIMEOUT = Pair(500L, TimeUnit.MILLISECONDS)

fun View.debounceClicks(): Observable<Unit> {
    return this.clicks().debounce(CLICK_DEBOUNCE_TIMEOUT.first, CLICK_DEBOUNCE_TIMEOUT.second)
}