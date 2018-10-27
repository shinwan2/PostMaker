package com.shinwan2.postmaker.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.shinwan2.postmaker.R

class CreatePostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
    }

    companion object {
        fun createPostIntent(context: Context): Intent {
            return Intent(context, CreatePostActivity::class.java)
        }
    }
}