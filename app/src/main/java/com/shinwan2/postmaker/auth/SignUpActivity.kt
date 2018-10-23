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
import com.shinwan2.postmaker.databinding.ActivitySignUpBinding
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_sign_up.emailEditText
import kotlinx.android.synthetic.main.activity_sign_up.passwordEditText
import kotlinx.android.synthetic.main.activity_sign_up.signInButton
import kotlinx.android.synthetic.main.activity_sign_up.signUpButton
import kotlinx.android.synthetic.main.activity_sign_up.topToolbar
import javax.inject.Inject

class SignUpActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var activitySignUpBinding: ActivitySignUpBinding
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
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SignUpViewModel::class.java)
        activitySignUpBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        activitySignUpBinding.viewModel = viewModel
        activitySignUpBinding.setLifecycleOwner(this)

        setSupportActionBar(topToolbar)
        supportActionBar!!.title = getString(R.string.signup_title)

        signInButton.createSignInButtonText()
        signUpButton.setOnClickListener { viewModel.signUp() }

        emailEditText.isSaveEnabled = false
        emailEditText.setText(viewModel.emailText)
        emailEditText.addTextChangedListener(emailTextWatcher)

        passwordEditText.isSaveEnabled = false
        passwordEditText.setText(viewModel.passwordText)
        passwordEditText.addTextChangedListener(passwordTextWatcher)

        viewModel.hasSignedIn.observe(this, Observer {
            if (it == true) {
                Toast.makeText(
                    this@SignUpActivity,
                    R.string.signin_message_success,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        })

        viewModel.errorMessage.observe(this, Observer {
            val content = it?.getContentIfNotHandled()
            if (content != null) {
                Toast.makeText(this@SignUpActivity, content, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onBackPressed() {
        if (viewModel.isSigningUp.value == true) return
        super.onBackPressed()
    }

    override fun onDestroy() {
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

    companion object {
        fun intent(context: Context) = Intent(context, SignUpActivity::class.java)
    }
}
