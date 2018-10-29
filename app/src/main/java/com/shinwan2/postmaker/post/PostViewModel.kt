package com.shinwan2.postmaker.post

import android.arch.lifecycle.MutableLiveData
import com.shinwan2.postmaker.domain.model.Post
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

data class PostViewModel(
    private val deletePostDelegate: DeletePostDelegate,
    private val post: Post,
    val isDeletable: Boolean
) {
    private val user = checkNotNull(post.user)

    val isDeleting = MutableLiveData<Boolean>()

    val posterDisplayName: String
        get() = user.displayName
    val posterEmail: String
        get() = user.email

    val postId: String
        get() = post.postId
    val textContent: String
        get() = post.textContent
    val createdDateTime: String by lazy {
        formatDateTime(post.createdTimestamp)
    }

    fun deletePost() {
        deletePostDelegate.deletePost(this)
    }

    /**
     * Format a UTC timestamp [time] with unit of [timeUnit] to the specified [format] using given
     * [locale] and [timeZone].
     */
    @JvmOverloads
    fun formatDateTime(
        time: Long,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
        format: String = "EEE, MMM d yyyy, HH:mm",
        locale: Locale = Locale.getDefault(),
        timeZone: TimeZone = TimeZone.getDefault()
    ): String {
        return SimpleDateFormat(format, locale).let {
            it.timeZone = timeZone
            it.format(Date(timeUnit.toMillis(time)))
        }
    }
}

interface DeletePostDelegate {
    fun deletePost(post: PostViewModel)
}