package com.shinwan2.postmaker.auth

import android.arch.lifecycle.ViewModel
import com.shinwan2.postmaker.domain.SchedulerManager
import com.shinwan2.postmaker.domain.auth.AuthenticationService
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableCompletableObserver
import timber.log.Timber

class SignUpViewModel(
    private val authenticationService: AuthenticationService,
    private val schedulerManager: SchedulerManager
) : ViewModel() {

    var emailText: String? = null
        set(value) {
            field = value
            validateForm()
        }

    var passwordText: String? = null
        set(value) {
            field = value
            validateForm()
        }

    val isSigningUp get() = _isSigningUp
    private var _isSigningUp = false

    private var buttonState: Boolean = false
    private var listener: Listener? = null
    private var disposable: Disposable? = null

    private val isSignedIn: Boolean
        get() = authenticationService.isSignedIn()

    init {
        validateForm()
    }

    fun start(listener: Listener) {
        this.listener = listener
        listener.sync()
    }

    fun stop() {
        this.listener = null
    }

    fun signUp() {
        if (_isSigningUp) return

        val email = checkNotNull(emailText)
        val password = checkNotNull(passwordText)

        disposable = authenticationService
            .signUp(email, password)
            .subscribeOn(schedulerManager.backgroundThreadScheduler)
            .observeOn(schedulerManager.uiThreadScheduler)
            .subscribeWith(object : DisposableCompletableObserver() {
                override fun onStart() {
                    super.onStart()
                    _isSigningUp = true
                    listener?.setProgressVisible(true)
                }

                override fun onComplete() {
                    Timber.d("signUpWithEmail:success")
                    _isSigningUp = false
                    listener?.let {
                        it.setProgressVisible(false)
                        it.showSuccessMessage()
                        it.navigateToNextScreen()
                    }
                }

                override fun onError(e: Throwable) {
                    Timber.w(e, "signUpWithEmail:failure")
                    _isSigningUp = false
                    listener?.let {
                        it.setProgressVisible(false)
                        it.showErrorMessage(e.message)
                    }
                }
            })
    }

    private fun validateForm() {
        buttonState = !emailText.isNullOrEmpty() && !passwordText.isNullOrEmpty()
        listener?.let {
            it.setErrorEmailRequiredVisible(emailText?.isEmpty() ?: false)
            it.setErrorPasswordRequiredVisible(passwordText?.isEmpty() ?: false)
            it.setButtonEnabled(buttonState)
        }
    }

    private fun Listener.sync() {
        if (isSignedIn) {
            showAlreadySignedIn()
            navigateToNextScreen()
            return
        }

        setEmailText(emailText.orEmpty())
        setPasswordText(passwordText.orEmpty())
        setProgressVisible(isSigningUp)
        validateForm()
    }

    interface Listener {
        fun setEmailText(emailText: String)
        fun setPasswordText(passwordText: String)

        fun setProgressVisible(visible: Boolean)
        fun setErrorEmailRequiredVisible(visible: Boolean)
        fun setErrorPasswordRequiredVisible(visible: Boolean)
        fun showErrorMessage(error: String?)
        fun setButtonEnabled(enabled: Boolean)

        fun showSuccessMessage()
        fun showAlreadySignedIn()
        fun navigateToNextScreen()
    }
}