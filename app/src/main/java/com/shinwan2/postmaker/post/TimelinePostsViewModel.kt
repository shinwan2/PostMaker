package com.shinwan2.postmaker.post

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.shinwan2.postmaker.R
import com.shinwan2.postmaker.domain.PostService
import com.shinwan2.postmaker.domain.SchedulerManager
import com.shinwan2.postmaker.domain.auth.AuthenticationService
import com.shinwan2.postmaker.domain.model.CursorList
import com.shinwan2.postmaker.domain.model.Post
import com.shinwan2.postmaker.util.Event
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver

private const val ITEM_REQUESTED = 10

class TimelinePostsViewModel(
    authenticationService: AuthenticationService,
    private val postService: PostService,
    private val schedulerManager: SchedulerManager
) : ViewModel(), DeletePostDelegate {

    val isRefreshing = MutableLiveData<Boolean>()
    val isLoadingMore = MutableLiveData<Boolean>()

    private val deletingPostIds = mutableSetOf<String>()
    private val postToDelete = MutableLiveData<PostViewModel>()
    val isShowingDeleteConfirmationDialog = Transformations.map(postToDelete) { it != null }

    val items = MutableLiveData<CursorList<PostViewModel>>()
    val isEmpty: LiveData<Boolean> = Transformations.map(items) { it.list.isEmpty() }

    val successMessage = MutableLiveData<Event<Int>>()
    val errorMessage = MutableLiveData<Event<String>>()

    private val currentUserId = authenticationService.userId
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

    override fun deletePost(post: PostViewModel) {
        if (postToDelete.value != null || deletingPostIds.contains(post.postId)) return
        postToDelete.value = post
    }

    fun refresh() {
        if (isRefreshing.value == true) return

        val disposable = postService.getTimelinePosts(null, ITEM_REQUESTED)
            .map { cursorList -> mapToViewModel(cursorList) }
            .subscribeOn(schedulerManager.backgroundThreadScheduler)
            .observeOn(schedulerManager.uiThreadScheduler)
            .doFinally { isRefreshing.value = false }
            .subscribeWith(object : DisposableSingleObserver<CursorList<PostViewModel>>() {
                override fun onStart() {
                    super.onStart()
                    isRefreshing.value = true
                    loadMoreDisposable?.dispose()
                }

                override fun onSuccess(newItems: CursorList<PostViewModel>) {
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
            .map { cursorList -> mapToViewModel(cursorList) }
            .subscribeOn(schedulerManager.backgroundThreadScheduler)
            .observeOn(schedulerManager.uiThreadScheduler)
            .doFinally { isLoadingMore.value = false }
            .subscribeWith(object : DisposableSingleObserver<CursorList<PostViewModel>>() {
                override fun onStart() {
                    super.onStart()
                    isLoadingMore.value = true
                }

                override fun onSuccess(newItems: CursorList<PostViewModel>) {
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

    fun cancelDeletePost() {
        postToDelete.value = null
    }

    fun confirmDeletePost() {
        val post = postToDelete.value ?: return

        val disposable = postService.deletePost(post.postId)
            .subscribeOn(schedulerManager.backgroundThreadScheduler)
            .observeOn(schedulerManager.uiThreadScheduler)
            .subscribeWith(object : DisposableCompletableObserver() {
                override fun onStart() {
                    super.onStart()
                    deletingPostIds.add(post.postId)
                    post.isDeleting.value = true
                }

                override fun onComplete() {
                    postToDelete.value = null
                    deletingPostIds.remove(post.postId)
                    removePostWithId(post.postId)
                    successMessage.value = Event(R.string.post_delete_message_success)
                }

                override fun onError(e: Throwable) {
                    postToDelete.value = null
                    deletingPostIds.remove(post.postId)
                    errorMessage.value = Event(e.message!!)
                }
            })
        compositeDisposable.add(disposable)
    }

    private fun removePostWithId(postId: String) {
        val currentCursorList = items.value ?: return
        val newCursorList = CursorList(
            list = currentCursorList.list.filterNot { it.postId == postId },
            nextCursor = currentCursorList.nextCursor
        )
        items.value = newCursorList
    }

    private fun mapToViewModel(cursorList: CursorList<Post>): CursorList<PostViewModel> {
        return CursorList(
            list = cursorList.list.map { post ->
                PostViewModel(
                    deletePostDelegate = this@TimelinePostsViewModel,
                    post = post,
                    isDeletable = post.userId == currentUserId
                ).apply {
                    isDeleting.postValue(deletingPostIds.contains(post.postId))
                }
            },
            nextCursor = cursorList.nextCursor
        )
    }

}