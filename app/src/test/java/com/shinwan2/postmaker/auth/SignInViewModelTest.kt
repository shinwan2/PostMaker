package com.shinwan2.postmaker.auth

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.atLeastOnce
import com.nhaarman.mockito_kotlin.clearInvocations
import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import com.shinwan2.postmaker.InstantSchedulerManager
import com.shinwan2.postmaker.domain.auth.AuthenticationService
import io.reactivex.Completable
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

private const val VALID_EMAIL = "abc@gmail.com"
private const val VALID_PASSWORD = "123456"

class SignInViewModelTest {

    @Mock
    lateinit var authenticationService: AuthenticationService
    @Mock
    private lateinit var listener: SignInViewModel.Listener

    private lateinit var viewModel: SignInViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = SignInViewModel(authenticationService, InstantSchedulerManager())
        listener = mock()
    }

    @Test
    fun testInitialState_emailAndPasswordNull() {
        assertEquals(null, viewModel.emailText)
        assertEquals(null, viewModel.passwordText)
    }

    @Test
    fun testInitialState_notLoggedIn_emailAndPasswordEmpty() {
        viewModel.start(listener)
        verify(listener).setEmailText("")
        verify(listener).setPasswordText("")
    }

    @Test
    fun testInitialState_notLoggedIn_noProgressBar() {
        viewModel.start(listener)
        verify(listener).setProgressVisible(false)
    }

    @Test
    fun testInitialState_notLoggedIn_buttonDisabled() {
        viewModel.start(listener)
        verify(listener).setButtonEnabled(false)
    }

    @Test
    fun testInitialState_notLoggedIn_noErrorTexts() {
        viewModel.start(listener)

        verify(listener).setErrorEmailRequiredVisible(false)
        verify(listener).setErrorPasswordRequiredVisible(false)
    }

    @Test
    fun testInitialState_loggedIn() {
        whenever(authenticationService.isSignedIn()).thenReturn(true)

        viewModel.start(listener)

        verify(listener).showAlreadySignedIn()
        verify(listener).navigateToNextScreen()
        verifyNoMoreInteractions(listener)
    }

    @Test
    fun testTypeEmailEmpty_showEmailRequiredButtonDisabled() {
        viewModel.start(listener)
        clearInvocations(listener)

        viewModel.emailText = ""

        verify(listener).setErrorEmailRequiredVisible(true)
        verify(listener).setButtonEnabled(false)
    }

    @Test
    fun testTypePasswordEmpty_showPasswordRequiredButtonDisabled() {
        viewModel.start(listener)
        clearInvocations(listener)

        viewModel.passwordText = ""

        verify(listener).setErrorPasswordRequiredVisible(true)
        verify(listener).setButtonEnabled(false)
    }

    @Test
    fun testTypeEmailPasswordNotEmpty_hideErrorButtonEnabled() {
        viewModel.start(listener)
        clearInvocations(listener)

        viewModel.emailText = VALID_EMAIL
        viewModel.passwordText = VALID_PASSWORD

        verifyLastValueBoolean(SignInViewModel.Listener::setErrorEmailRequiredVisible, false)
        verifyLastValueBoolean(SignInViewModel.Listener::setErrorPasswordRequiredVisible, false)
        verifyLastValueBoolean(SignInViewModel.Listener::setButtonEnabled, true)
    }

    @Test
    fun testSignIn_success_showHideProgress() {
        whenever(authenticationService.signIn(VALID_EMAIL, VALID_PASSWORD))
            .thenReturn(Completable.complete())

        viewModel.start(listener)
        viewModel.emailText = VALID_EMAIL
        viewModel.passwordText = VALID_PASSWORD
        clearInvocations(listener)

        viewModel.signIn()

        inOrder(listener).let {
            it.verify(listener).setProgressVisible(true)
            it.verify(listener).setProgressVisible(false)
        }
    }

    @Test
    fun testSignIn_cannotStack() {
        whenever(authenticationService.signIn(VALID_EMAIL, VALID_PASSWORD))
            .thenReturn(Completable.never())

        viewModel.start(listener)
        viewModel.emailText = VALID_EMAIL
        viewModel.passwordText = VALID_PASSWORD
        clearInvocations(listener)

        viewModel.signIn()
        viewModel.signIn()

        verify(authenticationService).signIn(any(), any())
    }

    @Test
    fun testSignIn_success_showMessageAndNavigateAway() {
        whenever(authenticationService.signIn(VALID_EMAIL, VALID_PASSWORD))
            .thenReturn(Completable.complete())

        viewModel.start(listener)
        viewModel.emailText = VALID_EMAIL
        viewModel.passwordText = VALID_PASSWORD
        clearInvocations(listener)

        viewModel.signIn()

        verify(listener).showSuccessMessage()
        verify(listener).navigateToNextScreen()
    }

    @Test
    fun testSignIn_error_showHideProgress() {
        whenever(authenticationService.signIn(VALID_EMAIL, VALID_PASSWORD))
            .thenReturn(Completable.error(IllegalStateException("ERROR")))

        viewModel.start(listener)
        viewModel.emailText = VALID_EMAIL
        viewModel.passwordText = VALID_PASSWORD
        clearInvocations(listener)

        viewModel.signIn()

        inOrder(listener).let {
            it.verify(listener).setProgressVisible(true)
            it.verify(listener).setProgressVisible(false)
        }
    }

    @Test
    fun testSignIn_error_showErrorMessage() {
        val error = "ERROR"
        whenever(authenticationService.signIn(VALID_EMAIL, VALID_PASSWORD))
            .thenReturn(Completable.error(IllegalStateException(error)))

        viewModel.start(listener)
        viewModel.emailText = VALID_EMAIL
        viewModel.passwordText = VALID_PASSWORD
        clearInvocations(listener)

        viewModel.signIn()

        verify(listener).showErrorMessage(error)
    }

    private fun verifyLastValueBoolean(
        function: SignInViewModel.Listener.(Boolean) -> Unit,
        value: Boolean
    ) {
        val booleanCaptor = argumentCaptor<Boolean>()
        verify(listener, atLeastOnce()).function(booleanCaptor.capture())
        assertEquals(value, booleanCaptor.lastValue)
    }
}