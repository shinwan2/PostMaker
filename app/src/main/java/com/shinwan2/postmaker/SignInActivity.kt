package com.shinwan2.postmaker

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.signInButton
import kotlinx.android.synthetic.main.activity_sign_in.signUpButton
import kotlinx.android.synthetic.main.activity_sign_up.emailEditText
import kotlinx.android.synthetic.main.activity_sign_up.passwordEditText
import timber.log.Timber

class SignInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_sign_in)
        signUpButton.setOnClickListener { navigateToSignUp() }
        signInButton.setOnClickListener { signIn() }
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if (user != null) {
            // user is already loggedIn
            Toast.makeText(this, "User is already logged in", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun navigateToSignUp() {
        startActivity(SignUpActivity.intent(this))
    }

    private fun signIn() {
        auth.signInWithEmailAndPassword(
            emailEditText.text.toString(),
            passwordEditText.text.toString()
        ).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Timber.d("signInWithEmail:success")
                finish()
            } else {
                // If sign in fails, display a message to the user.
                Timber.w(task.exception, "signInWithEmail:failure")
                Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
