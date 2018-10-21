package com.shinwan2.postmaker

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.shinwan2.postmaker.domain.auth.AuthenticationService
import com.shinwan2.postmaker.domain.SchedulerManager
import dagger.android.AndroidInjection
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableCompletableObserver
import kotlinx.android.synthetic.main.activity_sign_in.signInButton
import kotlinx.android.synthetic.main.activity_sign_in.signUpButton
import kotlinx.android.synthetic.main.activity_sign_up.emailEditText
import kotlinx.android.synthetic.main.activity_sign_up.passwordEditText
import timber.log.Timber
import javax.inject.Inject

class SignInActivity : AppCompatActivity() {
    @Inject
    lateinit var authenticationService: AuthenticationService
    @Inject
    lateinit var schedulerManager: SchedulerManager

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        signUpButton.setOnClickListener { navigateToSignUp() }
        signInButton.setOnClickListener { signIn() }
    }

    private fun navigateToSignUp() {
        startActivity(SignUpActivity.intent(this))
    }

    private fun signIn() {
        disposable = authenticationService
            .signIn(
                emailEditText.text.toString(),
                passwordEditText.text.toString()
            )
            .subscribeOn(schedulerManager.backgroundThreadScheduler)
            .observeOn(schedulerManager.uiThreadScheduler)
            .subscribeWith(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    Timber.d("signInWithEmail:success")
                    finish()
                }

                override fun onError(e: Throwable) {
                    Timber.w(e, "signInWithEmail:failure")
                    Toast.makeText(
                        this@SignInActivity,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
