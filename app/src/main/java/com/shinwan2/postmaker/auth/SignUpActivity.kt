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
import kotlinx.android.synthetic.main.activity_sign_up.emailEditText
import kotlinx.android.synthetic.main.activity_sign_up.emailTil
import kotlinx.android.synthetic.main.activity_sign_up.passwordEditText
import kotlinx.android.synthetic.main.activity_sign_up.passwordTil
import kotlinx.android.synthetic.main.activity_sign_up.progressBar
import kotlinx.android.synthetic.main.activity_sign_up.signInButton
import kotlinx.android.synthetic.main.activity_sign_up.signUpButton
import kotlinx.android.synthetic.main.activity_sign_up.signUpButtonText
import kotlinx.android.synthetic.main.activity_sign_up.topToolbar
import javax.inject.Inject

class SignUpActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: SignUpViewModel

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
        setContentView(R.layout.activity_sign_up)

        setSupportActionBar(topToolbar)
        supportActionBar!!.title = getString(R.string.signup_title)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SignUpViewModel::class.java)

        emailEditText.isSaveEnabled = false
        passwordEditText.isSaveEnabled = false
        signInButton.createSignInButtonText()
        signUpButton.setOnClickListener { viewModel.signUp() }

        viewModel.start(Listener())

        emailEditText.addTextChangedListener(emailTextWatcher)
        passwordEditText.addTextChangedListener(passwordTextWatcher)
    }

    override fun onBackPressed() {
        if (viewModel.isSigningUp) return
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
                navigateToSignIn()
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

    private inner class Listener : SignUpViewModel.Listener {
        override fun setEmailText(emailText: String) {
            emailEditText.setText(emailText)
        }

        override fun setPasswordText(passwordText: String) {
            passwordEditText.setText(passwordText)
        }

        override fun setProgressVisible(visible: Boolean) {
            if (visible) {
                progressBar.visibility = View.VISIBLE
                signUpButtonText.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                signUpButtonText.visibility = View.VISIBLE
            }
        }

        override fun setErrorEmailRequiredVisible(visible: Boolean) {
            emailTil.error = if (visible) {
                getString(R.string.signup_email_required_error)
            } else {
                null
            }
        }

        override fun setErrorPasswordRequiredVisible(visible: Boolean) {
            passwordTil.error = if (visible) {
                getString(R.string.signup_password_required_error)
            } else {
                null
            }
        }

        override fun showErrorMessage(error: String?) {
            Toast.makeText(this@SignUpActivity, error, Toast.LENGTH_SHORT).show()
        }

        override fun setButtonEnabled(enabled: Boolean) {
            signUpButton.isEnabled = enabled
            signUpButtonText.isEnabled = enabled
        }

        override fun showSuccessMessage() {
            Toast.makeText(
                this@SignUpActivity,
                R.string.signup_message_success,
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun showAlreadySignedIn() {
            Toast.makeText(
                this@SignUpActivity,
                R.string.signup_message_alreadysignedin,
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun navigateToNextScreen() {
            finish()
        }
    }

    companion object {
        fun intent(context: Context) = Intent(context, SignUpActivity::class.java)
    }
}
