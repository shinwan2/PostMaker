package com.shinwan2.postmaker.post

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.shinwan2.postmaker.InstantSchedulerManager
import com.shinwan2.postmaker.domain.PostService
import com.shinwan2.postmaker.domain.auth.AuthenticationService
import com.shinwan2.postmaker.domain.model.CursorList
import com.shinwan2.postmaker.domain.model.Post
import com.shinwan2.postmaker.domain.model.User
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

private const val ERROR_MESSAGE = "ERROR_MESSAGE"
private const val ITEM_PER_PAGE = 10
private const val VALID_NEXT_CURSOR = "abcdefghij"

@RunWith(MockitoJUnitRunner::class)
class TimelinePostsViewModelTest {

    @Mock
    lateinit var authenticationService: AuthenticationService
    @Mock
    lateinit var postService: PostService
    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: TimelinePostsViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        whenever(authenticationService.userId).thenReturn(MOCK_USER_23.userId)
    }

    @Test
    fun testInitialState_emptyButNoEmptyState() {
        mockInitialRefreshNeverFinished()

        assertEquals(null, viewModel.items.value)
        assertEquals(null, viewModel.isEmpty.value)
    }

    @Test
    fun testInitialState_isRefreshingNotLoadingMore() {
        mockInitialRefreshNeverFinished()

        assertEquals(true, viewModel.isRefreshing.value)
        assertEquals(false, viewModel.isLoadingMore.value)
    }

    @Test
    fun testInitialState_noSuccessMessageAndErrorMessage() {
        mockInitialRefreshNeverFinished()

        assertEquals(null, viewModel.successMessage.value)
        assertEquals(null, viewModel.errorMessage.value)
    }

    @Test
    fun testInitialState_notShowingDialog() {
        mockInitialRefreshNeverFinished()

        assertEquals(null, viewModel.isShowingDeleteConfirmationDialog.value)
    }

    @Test
    fun testInitialState_cannotSpamRefreshWhileRefreshing() {
        mockInitialRefreshNeverFinished()

        viewModel.refresh()
        viewModel.refresh()

        verify(postService).getTimelinePosts(null, ITEM_PER_PAGE)
    }

    @Test
    fun testInitialState_cannotSpamLoadMoreWhileRefreshing() {
        mockInitialRefreshNeverFinished()

        viewModel.loadMore()
        viewModel.loadMore()

        verify(postService).getTimelinePosts(null, ITEM_PER_PAGE)
    }

    @Test
    fun testRefresh_success_hasItems() {
        mockInitialRefreshSuccessHasNext()

        assertEquals(CursorList(
            list = listOf(PostViewModel(viewModel, MOCK_POST1, true)),
            nextCursor = VALID_NEXT_CURSOR
        ), viewModel.items.value)
    }

    @Test
    fun testRefresh_success_notRefreshingCanLoadMore() {
        mockInitialRefreshSuccessHasNext()

        assertEquals(false, viewModel.isRefreshing.value)
        assertEquals(true, viewModel.hasNextPage)
    }

    @Test
    fun testRefresh_fail_itemsStillNullErrorMessageNotEmpty() {
        mockInitialRefreshError()

        assertEquals(null, viewModel.items.value)
        assertNotEquals(null, viewModel.errorMessage.value)
    }

    @Test
    fun testRefresh_fail_notRefreshingCannotLoadMore() {
        mockInitialRefreshError()

        assertEquals(false, viewModel.isRefreshing.value)
        assertEquals(false, viewModel.hasNextPage)
    }

    @Test
    fun testLoadMore() {
        mockInitialRefreshSuccessHasNext()
        mockLoadMoreNeverFinished()

        viewModel.loadMore()

        assertEquals(false, viewModel.isRefreshing.value)
        assertEquals(true, viewModel.isLoadingMore.value)
    }

    @Test
    fun testLoadMore_cannotSpamLoadMore() {
        mockInitialRefreshSuccessHasNext()
        mockLoadMoreNeverFinished()

        viewModel.loadMore()
        viewModel.loadMore()
        viewModel.loadMore()

        verify(postService).getTimelinePosts(VALID_NEXT_CURSOR, ITEM_PER_PAGE)
    }

    @Test
    fun testLoadMoreSuccess_hasItems() {
        mockInitialRefreshSuccessHasNext()
        mockLoadMoreSuccessHasNext()

        viewModel.loadMore()

        assertEquals(CursorList(
            list = listOf(
                PostViewModel(viewModel, MOCK_POST1, true),
                PostViewModel(viewModel, MOCK_POST2, false)
            ),
            nextCursor = VALID_NEXT_CURSOR
        ), viewModel.items.value)
    }

    @Test
    fun testLoadMoreSuccess_hasNextIsNotLoadingMore() {
        mockInitialRefreshSuccessHasNext()
        mockLoadMoreSuccessHasNext()

        viewModel.loadMore()

        assertEquals(true, viewModel.hasNextPage)
        assertEquals(false, viewModel.isLoadingMore.value)
    }

    @Test
    fun testLoadMoreFail_hasNextIsNotLoadingMore() {
        mockInitialRefreshSuccessHasNext()
        mockLoadMoreErrorHasNext()

        viewModel.loadMore()

        assertEquals(true, viewModel.hasNextPage)
        assertEquals(false, viewModel.isLoadingMore.value)
    }

    @Test
    fun testLoadMoreFail_hasErrorMessage() {
        mockInitialRefreshSuccessHasNext()
        mockLoadMoreErrorHasNext()

        viewModel.loadMore()

        assertNotEquals(null, viewModel.errorMessage.value)
    }

    @Test
    fun testDeletePost_showConfirmationDialog() {
        mockInitialRefreshSuccessHasNext()
        val observer = mock<Observer<Boolean>>()
        viewModel.isShowingDeleteConfirmationDialog.observeForever(observer)

        viewModel.items.value!!.list[0].deletePost()

        val captor = argumentCaptor<Boolean>()
        verify(observer).onChanged(captor.capture())
        assertEquals(1, captor.allValues.size)
        assertEquals(true, viewModel.isShowingDeleteConfirmationDialog.value)
    }

    @Test
    fun testDeletePost_cannotSpam() {
        mockInitialRefreshSuccessHasNext()
        val observer = mock<Observer<Boolean>>()
        viewModel.isShowingDeleteConfirmationDialog.observeForever(observer)

        viewModel.items.value!!.list[0].deletePost()
        viewModel.items.value!!.list[0].deletePost()
        viewModel.items.value!!.list[0].deletePost()

        val captor = argumentCaptor<Boolean>()
        verify(observer).onChanged(captor.capture())
        assertEquals(1, captor.allValues.size)
        assertEquals(true, viewModel.isShowingDeleteConfirmationDialog.value)
    }

    @Test
    fun testDeletePost_cannotDeleteNotOwnedPost() {
        mockInitialRefreshSuccessHasNext()
        mockLoadMoreSuccessHasNext()
        val observer = mock<Observer<Boolean>>()
        viewModel.isShowingDeleteConfirmationDialog.observeForever(observer)
        viewModel.loadMore()

        viewModel.items.value!!.list[1].deletePost()

        val captor = argumentCaptor<Boolean>()
        verify(observer, never()).onChanged(captor.capture())
        assertEquals(0, captor.allValues.size)
        assertEquals(null, viewModel.isShowingDeleteConfirmationDialog.value)
    }

    @Test
    fun testDeletePost_cancel_canDeleteAgain() {
        mockInitialRefreshSuccessHasNext()
        val observer = mock<Observer<Boolean>>()
        viewModel.isShowingDeleteConfirmationDialog.observeForever(observer)

        viewModel.items.value!!.list[0].deletePost()
        viewModel.cancelDeletePost()
        viewModel.items.value!!.list[0].deletePost()

        val captor = argumentCaptor<Boolean>()
        verify(observer, times(3)).onChanged(captor.capture())
        assertEquals(true, captor.firstValue)
        assertEquals(false, captor.secondValue)
        assertEquals(true, captor.lastValue)
    }

    @Test
    fun testDeletePost_confirm_isDeleting() {
        mockInitialRefreshSuccessHasNext()
        val deletedPost = viewModel.items.value!!.list[0]
        whenever(postService.deletePost(deletedPost.postId)).thenReturn(Completable.never())

        deletedPost.deletePost()
        viewModel.confirmDeletePost()

        assertEquals(true, deletedPost.isDeleting.value)
    }

    @Test
    fun testDeletePost_cannotDeleteDeletingPost() {
        mockInitialRefreshSuccessHasNext()
        val deletedPost = viewModel.items.value!!.list[0]
        whenever(postService.deletePost(deletedPost.postId)).thenReturn(Completable.never())
        deletedPost.deletePost()
        viewModel.confirmDeletePost()

        val observer = mock<Observer<Boolean>>()
        viewModel.isShowingDeleteConfirmationDialog.observeForever(observer)

        deletedPost.deletePost()

        verify(observer).onChanged(false)
        assertEquals(false, viewModel.isShowingDeleteConfirmationDialog.value)
    }

    @Test
    fun testDeletePost_success() {
        mockInitialRefreshSuccessHasNext()
        val deletedPost = viewModel.items.value!!.list[0]
        whenever(postService.deletePost(deletedPost.postId)).thenReturn(Completable.complete())
        deletedPost.deletePost()

        viewModel.confirmDeletePost()

        assertNotEquals(null, viewModel.successMessage.value)
        assertEquals(
            CursorList(list = emptyList<PostViewModel>(), nextCursor = VALID_NEXT_CURSOR),
            viewModel.items.value
        )
    }

    @Test
    fun testDeletePost_fail() {
        mockInitialRefreshSuccessHasNext()
        val deletedPost = viewModel.items.value!!.list[0]
        whenever(postService.deletePost(deletedPost.postId))
            .thenReturn(Completable.error(IllegalStateException(ERROR_MESSAGE)))
        deletedPost.deletePost()

        viewModel.confirmDeletePost()

        assertNotEquals(null, viewModel.errorMessage.value)
        assertEquals(1, viewModel.items.value!!.list.size)
    }

    private fun mockLoadMoreNeverFinished() {
        whenever(postService.getTimelinePosts(VALID_NEXT_CURSOR, ITEM_PER_PAGE))
            .thenReturn(Single.never())
    }

    private fun mockLoadMoreSuccessHasNext() {
        whenever(postService.getTimelinePosts(VALID_NEXT_CURSOR, ITEM_PER_PAGE))
            .thenReturn(
                Single.just(
                    CursorList(
                        list = listOf(MOCK_POST2),
                        nextCursor = VALID_NEXT_CURSOR
                    )
                )
            )
    }

    private fun mockLoadMoreErrorHasNext() {
        whenever(postService.getTimelinePosts(VALID_NEXT_CURSOR, ITEM_PER_PAGE))
            .thenReturn(Single.error(IllegalStateException(ERROR_MESSAGE)))
    }

    private fun mockInitialRefreshNeverFinished() {
        whenever(postService.getTimelinePosts(anyOrNull(), eq(ITEM_PER_PAGE)))
            .thenReturn(Single.never())
        viewModel = TimelinePostsViewModel(
            authenticationService,
            postService,
            InstantSchedulerManager()
        )
    }

    private fun mockInitialRefreshSuccessHasNext() {
        whenever(postService.getTimelinePosts(anyOrNull(), eq(ITEM_PER_PAGE)))
            .thenReturn(
                Single.just(
                    CursorList(
                        list = listOf(MOCK_POST1),
                        nextCursor = VALID_NEXT_CURSOR
                    )
                )
            )
        viewModel = TimelinePostsViewModel(
            authenticationService,
            postService,
            InstantSchedulerManager()
        )
    }

    private fun mockInitialRefreshError() {
        whenever(postService.getTimelinePosts(anyOrNull(), eq(ITEM_PER_PAGE)))
            .thenReturn(Single.error(IllegalStateException(ERROR_MESSAGE)))
        viewModel = TimelinePostsViewModel(
            authenticationService,
            postService,
            InstantSchedulerManager()
        )
    }
}

private val MOCK_USER_23 = User(
    userId = "23",
    email = "abcdef@email.com",
    displayName = "abcdef",
    joinTimestamp = 123456,
    aboutMe = "",
    photoUrl = ""
)

private val MOCK_USER_24 = User(
    userId = "24",
    email = "zzzaaa@email.com",
    displayName = "zzzaaa",
    joinTimestamp = 123456,
    aboutMe = "",
    photoUrl = ""
)

private val MOCK_POST1 = Post(
    postId = "1",
    userId = "23",
    user = MOCK_USER_23,
    textContent = "Some post",
    createdTimestamp = 130123938
)

private val MOCK_POST2 = Post(
    postId = "2",
    userId = "24",
    user = MOCK_USER_24,
    textContent = "Other post",
    createdTimestamp = 130123938
)