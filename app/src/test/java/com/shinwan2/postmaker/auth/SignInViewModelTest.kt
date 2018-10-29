package com.shinwan2.postmaker.auth

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.shinwan2.postmaker.InstantSchedulerManager
import com.shinwan2.postmaker.domain.auth.AuthenticationService
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

private const val VALID_EMAIL = "abc@gmail.com"
private const val VALID_PASSWORD = "123456"

@RunWith(MockitoJUnitRunner::class)
class SignInViewModelTest {

    @Mock
    lateinit var authenticationService: AuthenticationService
    @Rule @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SignInViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = SignInViewModel(authenticationService, InstantSchedulerManager())
    }

    @Test
    fun testInitialState_emailAndPasswordNull() {
        assertEquals(null, viewModel.emailText)
        assertEquals(null, viewModel.passwordText)
    }

    @Test
    fun testInitialState_notSignedIn_noProgressBar() {
        assertEquals(false, viewModel.isSigningIn.value)
    }

    @Test
    fun testInitialState_notSignedIn_buttonDisabled() {
        assertEquals(false, viewModel.isButtonEnabled.value)
    }

    @Test
    fun testInitialState_notSignedIn_noErrorTexts() {
        assertEquals(false, viewModel.isErrorEmailRequiredVisible.value)
        assertEquals(false, viewModel.isErrorPasswordRequiredVisible.value)
    }

    @Test
    fun testInitialState_signedIn() {
        whenever(authenticationService.isSignedIn()).thenReturn(true)
        viewModel = SignInViewModel(authenticationService, InstantSchedulerManager())
        val observer = mock<Observer<Boolean>>()
        viewModel.hasSignedIn.observeForever(observer)
        verify(observer).onChanged(true)
    }

    @Test
    fun testTypeEmailEmpty_showEmailRequiredButtonDisabled() {
        viewModel.emailText = ""

        assertEquals(true, viewModel.isErrorEmailRequiredVisible.value)
        assertEquals(false, viewModel.isButtonEnabled.value)
    }

    @Test
    fun testTypePasswordEmpty_showPasswordRequiredButtonDisabled() {
        viewModel.passwordText = ""

        assertEquals(true, viewModel.isErrorPasswordRequiredVisible.value)
        assertEquals(false, viewModel.isButtonEnabled.value)
    }

    @Test
    fun testTypeEmailPasswordNotEmpty_hideErrorButtonEnabled() {
        viewModel.emailText = VALID_EMAIL
        viewModel.passwordText = VALID_PASSWORD

        assertEquals(false, viewModel.isErrorEmailRequiredVisible.value)
        assertEquals(false, viewModel.isErrorPasswordRequiredVisible.value)
        assertEquals(true, viewModel.isButtonEnabled.value)
    }

    @Test
    fun testSignIn_success_showHideProgress() {
        whenever(authenticationService.signIn(VALID_EMAIL, VALID_PASSWORD))
            .thenReturn(Completable.complete())
        val observer = mock<Observer<Boolean>>()
        viewModel.isSigningIn.observeForever(observer)

        viewModel.emailText = VALID_EMAIL
        viewModel.passwordText = VALID_PASSWORD
        viewModel.signIn()

        inOrder(observer).let {
            it.verify(observer).onChanged(false)
            it.verify(observer).onChanged(true)
            it.verify(observer).onChanged(false)
        }
    }

    @Test
    fun testSignIn_cannotStack() {
        whenever(authenticationService.signIn(VALID_EMAIL, VALID_PASSWORD))
            .thenReturn(Completable.never())

        viewModel.emailText = VALID_EMAIL
        viewModel.passwordText = VALID_PASSWORD

        viewModel.signIn()
        viewModel.signIn()

        verify(authenticationService).signIn(any(), any())
    }

    @Test
    fun testSignIn_success_showMessageAndNavigateAway() {
        whenever(authenticationService.signIn(VALID_EMAIL, VALID_PASSWORD))
            .thenReturn(Completable.complete())
        val observer = mock<Observer<Boolean>>()
        viewModel.hasSignedIn.observeForever(observer)

        viewModel.emailText = VALID_EMAIL
        viewModel.passwordText = VALID_PASSWORD
        viewModel.signIn()

        inOrder(observer).let {
            verify(observer).onChanged(false)
            verify(observer).onChanged(true)
        }
    }

    @Test
    fun testSignIn_error_showHideProgress() {
        whenever(authenticationService.signIn(VALID_EMAIL, VALID_PASSWORD))
            .thenReturn(Completable.error(IllegalStateException("ERROR")))
        val observer = mock<Observer<Boolean>>()
        viewModel.isSigningIn.observeForever(observer)

        viewModel.emailText = VALID_EMAIL
        viewModel.passwordText = VALID_PASSWORD
        viewModel.signIn()

        inOrder(observer).let {
            it.verify(observer).onChanged(false)
            it.verify(observer).onChanged(true)
            it.verify(observer).onChanged(false)
        }
    }

    @Test
    fun testSignIn_error_showErrorMessage() {
        val error = "ERROR"
        whenever(authenticationService.signIn(VALID_EMAIL, VALID_PASSWORD))
            .thenReturn(Completable.error(IllegalStateException(error)))
        val observer = mock<Observer<Event<String>>>()
        viewModel.errorMessage.observeForever(observer)

        viewModel.emailText = VALID_EMAIL
        viewModel.passwordText = VALID_PASSWORD
        viewModel.signIn()

        val argumentCaptor = argumentCaptor<Event<String>>()
        verify(observer).onChanged(argumentCaptor.capture())
        assertEquals(error, argumentCaptor.lastValue.peekContent())
    }
}