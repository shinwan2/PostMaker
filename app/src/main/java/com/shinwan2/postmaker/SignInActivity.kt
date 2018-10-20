package com.shinwan2.postmaker

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sign_in.signUpButton

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        signUpButton.setOnClickListener { navigateToSignUp() }
    }

    private fun navigateToSignUp() {
        startActivity(SignUpActivity.intent(this))
    }
}
