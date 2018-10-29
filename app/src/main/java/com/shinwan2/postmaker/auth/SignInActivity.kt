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
import android.widget.TextView
import android.widget.Toast
import com.jakewharton.rxbinding3.widget.afterTextChangeEvents
import com.shinwan2.postmaker.R
import com.shinwan2.postmaker.databinding.ActivitySignInBinding
import com.shinwan2.postmaker.home.HomeActivity
import com.shinwan2.postmaker.util.Event
import com.shinwan2.postmaker.util.debounceClicks
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_sign_in.emailEditText
import kotlinx.android.synthetic.main.activity_sign_in.passwordEditText
import kotlinx.android.synthetic.main.activity_sign_in.signInButton
import kotlinx.android.synthetic.main.activity_sign_in.signUpButton
import kotlinx.android.synthetic.main.activity_sign_in.topToolbar
import javax.inject.Inject

class SignInActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: SignInViewModel
    private lateinit var compositeDisposable: CompositeDisposable

    private val signInObserver = Observer<Boolean> {
        if (it == true) onSignedIn()
    }

    private val errorMessageObserver = Observer<Event<String>> {
        val content = it?.getContentIfNotHandled()
        if (content != null) {
            Toast.makeText(this@SignInActivity, content, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        compositeDisposable = CompositeDisposable()
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SignInViewModel::class.java)
        val binding: ActivitySignInBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)

        setSupportActionBar(topToolbar)
        supportActionBar!!.title = getString(R.string.signin_title)

        initializeViews()
        observeViewModelForever()
    }

    override fun onBackPressed() {
        if (viewModel.isSigningIn.value == true) return
        super.onBackPressed()
    }

    override fun onDestroy() {
        unobserveViewModelForever()
        compositeDisposable.clear()
        super.onDestroy()
    }

    private fun initializeViews() {
        signUpButton.createSignUpButtonText()
        compositeDisposable.add(
            signInButton.debounceClicks().subscribe { viewModel.signIn() }
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
    }

    private fun observeViewModelForever() {
        viewModel.hasSignedIn.observeForever(signInObserver)
        viewModel.errorMessage.observeForever(errorMessageObserver)
    }

    private fun unobserveViewModelForever() {
        viewModel.hasSignedIn.removeObserver(signInObserver)
        viewModel.errorMessage.removeObserver(errorMessageObserver)
    }

    private fun onSignUpButtonClicked() {
        navigateToSignUp()
        finish()
    }

    private fun navigateToSignUp() {
        startActivity(SignUpActivity.intent(this))
    }

    private fun onSignedIn() {
        Toast.makeText(
            this@SignInActivity,
            R.string.signin_message_success,
            Toast.LENGTH_SHORT
        ).show()
        navigateToHomeActivity()
        finish()
    }

    private fun navigateToHomeActivity() {
        startActivity(HomeActivity.intent(this))
    }

    private fun TextView.createSignUpButtonText() {
        val insertedString = getString(R.string.signin_button_signup)
        val spannable = SpannableString(getString(
            R.string.signin_label_noaccount_format, insertedString
        ))
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                onSignUpButtonClicked()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(this@SignInActivity, R.color.text_link)
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
        highlightColor = ContextCompat.getColor(this@SignInActivity, R.color.button_ripple)
    }

    companion object {
        fun intent(context: Context) = Intent(context, SignInActivity::class.java)
    }
}
