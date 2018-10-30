package com.shinwan2.postmaker.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.shinwan2.postmaker.R
import com.shinwan2.postmaker.post.CreatePostActivity
import com.shinwan2.postmaker.post.TimelinePostsFragment
import com.shinwan2.postmaker.util.throttleClicks
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_home.drawerLayout
import kotlinx.android.synthetic.main.activity_home.fab
import kotlinx.android.synthetic.main.activity_home.topToolbar
import javax.inject.Inject

private const val FRAGMENT_TAG = "FRAGMENT_TAG"

class HomeActivity : AppCompatActivity(), HasSupportFragmentInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        compositeDisposable = CompositeDisposable()

        setSupportActionBar(topToolbar)
        supportActionBar!!.apply {
            setTitle(R.string.home_title)
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

        drawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, topToolbar,
            R.string.home_drawer_contentdescription_open,
            R.string.home_drawer_contentdescription_close
        )
        drawerLayout.addDrawerListener(drawerToggle)

        compositeDisposable.add(
            fab.throttleClicks().subscribe { navigateToCreatePost() }
        )

        setFragment()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle.syncState()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    override fun supportFragmentInjector() = dispatchingAndroidInjector

    private fun navigateToCreatePost() {
        startActivity(CreatePostActivity.createPostIntent(this))
    }

    private fun setFragment() {
        supportFragmentManager.findFragmentByTag(FRAGMENT_TAG) as? TimelinePostsFragment
            ?: TimelinePostsFragment().also {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, it, FRAGMENT_TAG)
                    .commit()
            }
    }

    companion object {
        fun intent(context: Context) = Intent(context, HomeActivity::class.java)
    }
}