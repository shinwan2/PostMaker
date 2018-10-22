package com.shinwan2.postmaker.auth

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.shinwan2.postmaker.R
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_sign_in.emailEditText
import kotlinx.android.synthetic.main.activity_sign_in.emailTil
import kotlinx.android.synthetic.main.activity_sign_in.passwordEditText
import kotlinx.android.synthetic.main.activity_sign_in.passwordTil
import kotlinx.android.synthetic.main.activity_sign_in.progressBar
import kotlinx.android.synthetic.main.activity_sign_in.signInButton
import kotlinx.android.synthetic.main.activity_sign_in.signInButtonText
import kotlinx.android.synthetic.main.activity_sign_in.signUpButton
import kotlinx.android.synthetic.main.activity_sign_in.topToolbar
import javax.inject.Inject

class SignInActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: SignInViewModel

    private val emailTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            viewModel.emailText = s.toString()
        }
    }
    private val passwordTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            viewModel.passwordText = s.toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        setSupportActionBar(topToolbar)
        supportActionBar!!.title = getString(R.string.signin_title)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SignInViewModel::class.java)

        emailEditText.isSaveEnabled = false
        passwordEditText.isSaveEnabled = false
        signUpButton.createSignUpButtonText()
        signInButton.setOnClickListener { viewModel.signIn() }

        viewModel.start(Listener())

        emailEditText.addTextChangedListener(emailTextWatcher)
        passwordEditText.addTextChangedListener(passwordTextWatcher)
    }

    override fun onBackPressed() {
        if (viewModel.isSigningIn) return
        super.onBackPressed()
    }

    override fun onDestroy() {
        viewModel.stop()
        signUpButton.setOnClickListener(null)
        signInButton.setOnClickListener(null)
        emailEditText.removeTextChangedListener(emailTextWatcher)
        passwordEditText.removeTextChangedListener(passwordTextWatcher)
        super.onDestroy()
    }

    private fun navigateToSignUp() {
        startActivity(SignUpActivity.intent(this))
    }

    private fun TextView.createSignUpButtonText() {
        val insertedString = getString(R.string.signin_button_signup)
        val spannable = SpannableString(getString(
            R.string.signin_label_noaccount_format, insertedString
        ))
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                navigateToSignUp()
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

    private inner class Listener : SignInViewModel.Listener {
        override fun setEmailText(emailText: String) {
            emailEditText.setText(emailText)
        }

        override fun setPasswordText(passwordText: String) {
            passwordEditText.setText(passwordText)
        }

        override fun setProgressVisible(visible: Boolean) {
            if (visible) {
                progressBar.visibility = View.VISIBLE
                signInButtonText.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                signInButtonText.visibility = View.VISIBLE
            }
        }

        override fun setErrorEmailRequiredVisible(visible: Boolean) {
            emailTil.error = if (visible) {
                getString(R.string.signin_email_required_error)
            } else {
                null
            }
        }

        override fun setErrorPasswordRequiredVisible(visible: Boolean) {
            passwordTil.error = if (visible) {
                getString(R.string.signin_password_required_error)
            } else {
                null
            }
        }

        override fun showErrorMessage(error: String?) {
            Toast.makeText(this@SignInActivity, error, Toast.LENGTH_SHORT).show()
        }

        override fun setButtonEnabled(enabled: Boolean) {
            signInButton.isEnabled = enabled
            signInButtonText.isEnabled = enabled
        }

        override fun showSuccessMessage() {
            Toast.makeText(
                this@SignInActivity,
                R.string.signin_message_success,
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun showAlreadySignedIn() {
            Toast.makeText(
                this@SignInActivity,
                R.string.signin_message_alreadysignedin,
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun navigateToNextScreen() {
            finish()
        }
    }

    companion object {
        fun intent(context: Context) = Intent(context, SignInActivity::class.java)
    }
}
