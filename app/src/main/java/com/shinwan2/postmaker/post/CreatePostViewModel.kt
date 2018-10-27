package com.shinwan2.postmaker.post

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.shinwan2.postmaker.domain.PostService
import com.shinwan2.postmaker.domain.SchedulerManager
import com.shinwan2.postmaker.domain.model.CreatePostRequest
import com.shinwan2.postmaker.domain.model.Post
import com.shinwan2.postmaker.util.Event
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver

class CreatePostViewModel(
    private val postService: PostService,
    private val schedulerManager: SchedulerManager
) : ViewModel() {

    val contentMaxLength = Post.CONTENT_MAX_LENGTH
    var content: String = ""
        set(value) {
            if (field == value) return
            field = value
            validate()
        }
    val isContentErrorRequired = MutableLiveData<Boolean>()

    val isButtonSubmitEnabled = MutableLiveData<Boolean>().also { it.value = isPostAllowed() }
    val isSubmitting = MutableLiveData<Boolean>()

    val finish = MutableLiveData<Event<Any?>>()
    val errorMessage = MutableLiveData<Event<String>>()

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun submitPost() {
        if (isSubmitting.value == true) return

        val disposable = postService.createPost(CreatePostRequest(content))
            .subscribeOn(schedulerManager.backgroundThreadScheduler)
            .observeOn(schedulerManager.uiThreadScheduler)
            .subscribeWith(object : DisposableCompletableObserver() {
                override fun onStart() {
                    super.onStart()
                    isButtonSubmitEnabled.value = false
                    isSubmitting.value = true
                }

                override fun onComplete() {
                    isButtonSubmitEnabled.value = true
                    isSubmitting.value = false
                    finish.value = Event(null)
                }

                override fun onError(e: Throwable) {
                    isButtonSubmitEnabled.value = true
                    isSubmitting.value = false
                    errorMessage.value = Event(e.message!!)
                }
            })
        compositeDisposable.add(disposable)
    }

    private fun validate() {
        isContentErrorRequired.value = content.isEmpty()
        isButtonSubmitEnabled.value = isPostAllowed()
    }

    private fun isPostAllowed(): Boolean {
        return !content.isEmpty() && content.length <= contentMaxLength
    }
}