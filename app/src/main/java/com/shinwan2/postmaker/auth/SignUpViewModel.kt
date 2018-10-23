package com.shinwan2.postmaker.auth

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.shinwan2.postmaker.domain.SchedulerManager
import com.shinwan2.postmaker.domain.auth.AuthenticationService
import com.shinwan2.postmaker.util.Event
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

    val isSigningUp = MutableLiveData<Boolean>().also { it.value = false }
    val isButtonEnabled = MutableLiveData<Boolean>().also { it.value = false }
    val hasSignedIn = MutableLiveData<Boolean>().also {
        it.value = authenticationService.isSignedIn()
    }

    val isErrorEmailRequiredVisible = MutableLiveData<Boolean>().also { it.value = false }
    val isErrorPasswordRequiredVisible = MutableLiveData<Boolean>().also { it.value = false }
    val errorMessage = MutableLiveData<Event<String>>()

    private var disposable: Disposable? = null

    init {
        validateForm()
    }

    fun signUp() {
        if (isSigningUp.value == true) return

        val email = checkNotNull(emailText)
        val password = checkNotNull(passwordText)

        disposable = authenticationService
            .signUp(email, password)
            .subscribeOn(schedulerManager.backgroundThreadScheduler)
            .observeOn(schedulerManager.uiThreadScheduler)
            .subscribeWith(object : DisposableCompletableObserver() {
                override fun onStart() {
                    super.onStart()
                    isSigningUp.value = true
                }

                override fun onComplete() {
                    Timber.d("signUpWithEmail:success")
                    isSigningUp.value = false
                    hasSignedIn.value = true
                }

                override fun onError(e: Throwable) {
                    Timber.w(e, "signUpWithEmail:failure")
                    isSigningUp.value = false
                    errorMessage.value = Event(e.message!!)
                }
            })
    }

    private fun validateForm() {
        isButtonEnabled.value = !emailText.isNullOrEmpty() && !passwordText.isNullOrEmpty()
        isErrorEmailRequiredVisible.value = emailText?.isEmpty() ?: false
        isErrorPasswordRequiredVisible.value = passwordText?.isEmpty() ?: false
    }
}