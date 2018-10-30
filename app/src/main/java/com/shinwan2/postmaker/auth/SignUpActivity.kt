package com.shinwan2.postmaker.auth

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.TextView
import android.widget.Toast
import com.jakewharton.rxbinding3.widget.afterTextChangeEvents
import com.jakewharton.rxbinding3.widget.editorActions
import com.shinwan2.postmaker.R
import com.shinwan2.postmaker.databinding.ActivitySignUpBinding
import com.shinwan2.postmaker.home.HomeActivity
import com.shinwan2.postmaker.util.Event
import com.shinwan2.postmaker.util.throttleClicks
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_sign_up.emailEditText
import kotlinx.android.synthetic.main.activity_sign_up.passwordEditText
import kotlinx.android.synthetic.main.activity_sign_up.signInButton
import kotlinx.android.synthetic.main.activity_sign_up.signUpButton
import kotlinx.android.synthetic.main.activity_sign_up.topToolbar
import javax.inject.Inject

class SignUpActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: SignUpViewModel
    private lateinit var compositeDisposable: CompositeDisposable

    private val signInObserver = Observer<Boolean> {
        if (it == true) onSignedUp()
    }

    private val errorMessageObserver = Observer<Event<String>> {
        val content = it?.getContentIfNotHandled()
        if (content != null) {
            Toast.makeText(this@SignUpActivity, content, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        compositeDisposable = CompositeDisposable()
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SignUpViewModel::class.java)
        val binding: ActivitySignUpBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)

        setSupportActionBar(topToolbar)
        supportActionBar!!.title = getString(R.string.signup_title)

        initializeViews()
        observeViewModelForever()
    }

    override fun onBackPressed() {
        if (viewModel.isSigningUp.value == true) return
        super.onBackPressed()
    }

    override fun onDestroy() {
        unobserveViewModelForever()
        compositeDisposable.clear()
        super.onDestroy()
    }

    private fun initializeViews() {
        signInButton.createSignInButtonText()
        compositeDisposable.add(
            signUpButton.throttleClicks().subscribe { viewModel.signUp() }
        )

        emailEditText.isSaveEnabled = false
        emailEditText.setText(viewModel.emailText)
        compositeDisposable.add(
            emailEditText.afterTextChangeEvents()
                .skipInitialValue()
                .subscribe { viewModel.emailText = it.editable.toString() }
        )

        passwordEditText.isSaveEnabled = false
        passwordEditText.setText(viewModel.passwordText)
        compositeDisposable.add(
            passwordEditText.afterTextChangeEvents()
                .skipInitialValue()
                .subscribe { viewModel.passwordText = it.editable.toString() }
        )
        compositeDisposable.add(
            passwordEditText.editorActions { it == IME_ACTION_DONE }
                .subscribe { viewModel.signUp() }
        )
    }

    private fun observeViewModelForever() {
        viewModel.hasSignedIn.observeForever(signInObserver)
        viewModel.errorMessage.observeForever(errorMessageObserver)
    }

    private fun unobserveViewModelForever() {
        viewModel.hasSignedIn.removeObserver(signInObserver)
        viewModel.errorMessage.removeObserver(errorMessageObserver)
    }

    private fun onSignedUp() {
        Toast.makeText(
            this@SignUpActivity,
            R.string.signup_message_success,
            Toast.LENGTH_SHORT
        ).show()
        navigateToHomeActivity()
        finish()
    }

    private fun navigateToHomeActivity() {
        startActivity(HomeActivity.intent(this))
    }

    private fun onSignInButtonClicked() {
        navigateToSignIn()
        finish()
    }

    private fun navigateToSignIn() {
        startActivity(SignInActivity.intent(this))
    }

    private fun TextView.createSignInButtonText() {
        val insertedString = getString(R.string.signup_button_signin)
        val spannable = SpannableString(getString(
            R.string.signup_label_haveaccount_format, insertedString
        ))
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                onSignInButtonClicked()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(this@SignUpActivity, R.color.text_link)
                ds.isUnderlineText = true
            }
        }

        val replaceIndex = spannable.indexOf(insertedString)
        spannable.setSpan(clickableSpan,
            replaceIndex, replaceIndex + insertedString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        text = spannable
        movementMethod = LinkMovementMethod.getInstance()
        highlightColor = ContextCompat.getColor(this@SignUpActivity, R.color.button_ripple)
    }

    companion object {
        fun intent(context: Context) = Intent(context, SignUpActivity::class.java)
    }
}
