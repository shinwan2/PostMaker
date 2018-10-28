package com.shinwan2.postmaker.post

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.shinwan2.postmaker.domain.PostService
import com.shinwan2.postmaker.domain.SchedulerManager
import com.shinwan2.postmaker.domain.model.CursorList
import com.shinwan2.postmaker.domain.model.Post
import com.shinwan2.postmaker.util.Event
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver

private const val ITEM_REQUESTED = 10

class TimelinePostsViewModel(
    private val postService: PostService,
    private val schedulerManager: SchedulerManager
) : ViewModel() {
    val isRefreshing = MutableLiveData<Boolean>()
    val isLoadingMore = MutableLiveData<Boolean>()

    val items = MutableLiveData<CursorList<Post>>()
    val isEmpty: LiveData<Boolean> = Transformations.map(items) { it.list.isEmpty() }

    val errorMessage = MutableLiveData<Event<String>>()

    private var loadMoreDisposable: Disposable? = null
    private var compositeDisposable = CompositeDisposable()

    val hasNextPage: Boolean
        get() {
            val isNextCursorEmpty = items.value?.nextCursor == ""
            return !isNextCursorEmpty
        }

    init {
         refresh()
    }

    override fun onCleared() {
        compositeDisposable.clear()
        loadMoreDisposable = null
        super.onCleared()
    }

    fun refresh() {
        if (isRefreshing.value == true) return

        val disposable = postService.getTimelinePosts(null, ITEM_REQUESTED)
            .subscribeOn(schedulerManager.backgroundThreadScheduler)
            .observeOn(schedulerManager.uiThreadScheduler)
            .doFinally { isRefreshing.value = false }
            .subscribeWith(object : DisposableSingleObserver<CursorList<Post>>() {
                override fun onStart() {
                    super.onStart()
                    isRefreshing.value = true
                    loadMoreDisposable?.dispose()
                }

                override fun onSuccess(newItems: CursorList<Post>) {
                    items.value = newItems
                }

                override fun onError(e: Throwable) {
                    errorMessage.value = Event(e.message!!)
                }
            })
        compositeDisposable.add(disposable)
    }

    fun loadMore() {
        if (isRefreshing.value == true || isLoadingMore.value == true) return
        if (!hasNextPage) return

        loadMoreDisposable = postService.getTimelinePosts(items.value?.nextCursor, ITEM_REQUESTED)
            .subscribeOn(schedulerManager.backgroundThreadScheduler)
            .observeOn(schedulerManager.uiThreadScheduler)
            .doFinally { isLoadingMore.value = false }
            .subscribeWith(object : DisposableSingleObserver<CursorList<Post>>() {
                override fun onStart() {
                    super.onStart()
                    isLoadingMore.value = true
                }

                override fun onSuccess(newItems: CursorList<Post>) {
                    val addedItems = ArrayList(items.value!!.list)
                    addedItems.addAll(newItems.list)
                    items.value = CursorList(addedItems, newItems.nextCursor)
                }

                override fun onError(e: Throwable) {
                    errorMessage.value = Event(e.message!!)
                }
            })
        compositeDisposable.add(loadMoreDisposable!!)
    }
}