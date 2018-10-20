package com.shinwan2.postmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.emailEditText
import kotlinx.android.synthetic.main.activity_sign_up.passwordEditText
import kotlinx.android.synthetic.main.activity_sign_up.signUpButton
import timber.log.Timber

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        signUpButton.setOnClickListener { signUp() }
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

    private fun signUp() {
        auth.createUserWithEmailAndPassword(
            emailEditText.text.toString(),
            passwordEditText.text.toString()
        ).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Timber.d("createUserWithEmail:success")
                finish()
            } else {
                // If sign in fails, display a message to the user.
                Timber.w(task.exception, "createUserWithEmail:failure")
                Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        fun intent(context: Context) = Intent(context, SignUpActivity::class.java)
    }
}
