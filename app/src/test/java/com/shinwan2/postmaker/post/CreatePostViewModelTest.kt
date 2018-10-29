package com.shinwan2.postmaker.post

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import com.nhaarman.mockito_kotlin.whenever
import com.shinwan2.postmaker.InstantSchedulerManager
import com.shinwan2.postmaker.domain.PostService
import com.shinwan2.postmaker.domain.model.CreatePostRequest
import com.shinwan2.postmaker.util.Event
import io.reactivex.Completable
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

private const val CONTENT_SAMPLE = "SAMPLE"
private const val ERROR_MESSAGE = "ERROR"
private val LONG_CONTENT = buildString { (1..1000).forEach { append(it) } }

@RunWith(MockitoJUnitRunner::class)
class CreatePostViewModelTest {

    @Mock
    lateinit var postService: PostService
    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: CreatePostViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = CreatePostViewModel(postService, InstantSchedulerManager())
    }

    @Test
    fun testInitialState_emptyContent_errorHiddenButtonDisabledNotSubmitting() {
        assertEquals("", viewModel.content)
        assertEquals(false, viewModel.isContentErrorRequiredVisible.value)
        assertEquals(false, viewModel.isButtonSubmitEnabled.value)
        assertEquals(false, viewModel.isSubmitting.value)
    }

    @Test
    fun testInitialState_noEvents() {
        assertEquals(null, viewModel.errorMessage.value)
        assertEquals(null, viewModel.finish.value)
    }

    @Test
    fun testInitialState_submit_noResult() {
        viewModel.submitPost()

        verifyZeroInteractions(postService)
    }

    @Test
    fun testNonEmptyContent_errorShowedButtonEnabledNotSubmitting() {
        viewModel.content = CONTENT_SAMPLE

        assertEquals(false, viewModel.isContentErrorRequiredVisible.value)
        assertEquals(true, viewModel.isButtonSubmitEnabled.value)
        assertEquals(false, viewModel.isSubmitting.value)
    }

    @Test
    fun testNonEmptyContentToEmptyContent_errorShowedButtonDisabledNotSubmitting() {
        viewModel.content = CONTENT_SAMPLE
        viewModel.content = ""

        assertEquals(true, viewModel.isContentErrorRequiredVisible.value)
        assertEquals(false, viewModel.isButtonSubmitEnabled.value)
        assertEquals(false, viewModel.isSubmitting.value)
    }

    @Test
    fun testEmptyContent_submit_noResult() {
        viewModel.content = ""
        viewModel.submitPost()

        verifyZeroInteractions(postService)
    }

    @Test
    fun testLongContent_buttonDisabledNotSubmitting() {
        viewModel.content = LONG_CONTENT

        assertEquals(false, viewModel.isContentErrorRequiredVisible.value)
        assertEquals(false, viewModel.isButtonSubmitEnabled.value)
        assertEquals(false, viewModel.isSubmitting.value)
    }

    @Test
    fun testLongContent_submit_noResult() {
        viewModel.content = LONG_CONTENT
        viewModel.submitPost()

        verifyZeroInteractions(postService)
    }

    @Test
    fun testNonEmptyContent_submitOnProgress_isSubmittingButtonDisabled() {
        whenever(postService.createPost(CreatePostRequest(CONTENT_SAMPLE)))
            .thenReturn(Completable.never())

        viewModel.content = CONTENT_SAMPLE
        viewModel.submitPost()

        assertEquals(true, viewModel.isSubmitting.value)
        assertEquals(false, viewModel.isButtonSubmitEnabled.value)
    }

    @Test
    fun testNonEmptyContent_submitSuccessful() {
        whenever(postService.createPost(CreatePostRequest(CONTENT_SAMPLE)))
            .thenReturn(Completable.complete())
        val observer = mock<Observer<Event<*>>>()
        viewModel.finish.observeForever(observer)

        viewModel.content = CONTENT_SAMPLE
        viewModel.submitPost()

        assertEquals(false, viewModel.isSubmitting.value)
        assertEquals(false, viewModel.isButtonSubmitEnabled.value)
        val captor = argumentCaptor<Event<Any?>>()
        verify(observer).onChanged(captor.capture())
        assertEquals(1, captor.allValues.size)
        assertEquals(null, captor.lastValue.peekContent())
    }

    @Test
    fun testNonEmptyContent_submitFail() {
        whenever(postService.createPost(CreatePostRequest(CONTENT_SAMPLE)))
            .thenReturn(Completable.error(IllegalStateException(ERROR_MESSAGE)))
        val observer = mock<Observer<Event<String>>>()
        viewModel.errorMessage.observeForever(observer)

        viewModel.content = CONTENT_SAMPLE
        viewModel.submitPost()

        assertEquals(false, viewModel.isSubmitting.value)
        assertEquals(true, viewModel.isButtonSubmitEnabled.value)
        val captor = argumentCaptor<Event<String>>()
        verify(observer).onChanged(captor.capture())
        assertEquals(1, captor.allValues.size)
        assertEquals(ERROR_MESSAGE, captor.lastValue.peekContent())
    }
}