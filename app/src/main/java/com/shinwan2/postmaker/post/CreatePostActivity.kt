package com.shinwan2.postmaker.post

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.shinwan2.postmaker.R
import com.shinwan2.postmaker.util.tintWithColorStateList
import kotlinx.android.synthetic.main.activity_home.topToolbar

class CreatePostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        setSupportActionBar(topToolbar)
        supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.create_post, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val menuIds = intArrayOf(R.id.menuSubmitPost)
        for (menuId in menuIds) {
            menu.findItem(menuId).also {
                it.tintWithColorStateList(this, R.color.selector_menu_item_tint_dark_background)
                it.isEnabled = true // TODO: bind to ViewModel value
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSubmitPost -> {
                submitPost()
                return true
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun submitPost() {

    }

    companion object {
        fun createPostIntent(context: Context): Intent {
            return Intent(context, CreatePostActivity::class.java)
        }
    }
}