package com.shinwan2.postmaker.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.shinwan2.postmaker.R

class HomeActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    companion object {
        fun intent(context: Context) = Intent(context, HomeActivity::class.java)
    }
}