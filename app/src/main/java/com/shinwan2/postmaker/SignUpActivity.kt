package com.shinwan2.postmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.shinwan2.postmaker.domain.AuthenticationService
import com.shinwan2.postmaker.domain.SchedulerManager
import dagger.android.AndroidInjection
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableCompletableObserver
import kotlinx.android.synthetic.main.activity_sign_up.emailEditText
import kotlinx.android.synthetic.main.activity_sign_up.passwordEditText
import kotlinx.android.synthetic.main.activity_sign_up.signUpButton
import timber.log.Timber
import javax.inject.Inject

class SignUpActivity : AppCompatActivity() {
    @Inject
    lateinit var authenticationService: AuthenticationService
    @Inject
    lateinit var schedulerManager: SchedulerManager

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        signUpButton.setOnClickListener { signUp() }
    }

    private fun signUp() {
        disposable = authenticationService
            .signUp(
                emailEditText.text.toString(),
                passwordEditText.text.toString()
            )
            .subscribeOn(schedulerManager.backgroundThreadScheduler)
            .observeOn(schedulerManager.uiThreadScheduler)
            .subscribeWith(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    Timber.d("createUserWithEmail:success")
                    finish()
                }

                override fun onError(e: Throwable) {
                    Timber.w(e, "createUserWithEmail:failure")
                    Toast.makeText(
                        this@SignUpActivity,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    companion object {
        fun intent(context: Context) = Intent(context, SignUpActivity::class.java)
    }
}
