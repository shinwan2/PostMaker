package com.shinwan2.postmaker.post

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.jakewharton.rxbinding3.widget.afterTextChangeEvents
import com.shinwan2.postmaker.R
import com.shinwan2.postmaker.databinding.ActivityCreatePostBinding
import com.shinwan2.postmaker.util.Event
import com.shinwan2.postmaker.util.tintWithColorStateList
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_create_post.postContentTextView
import kotlinx.android.synthetic.main.activity_home.topToolbar
import javax.inject.Inject

private const val BUNDLE_CONTENT = "BUNDLE_CONTENT"

class CreatePostActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: ActivityCreatePostBinding
    private lateinit var viewModel: CreatePostViewModel
    private lateinit var compositeDisposable: CompositeDisposable

    private val finishObserver = Observer<Event<Any?>> {
        if (it == null) return@Observer
        showToast(getString(R.string.post_create_message_success))
        finish()
    }

    private val errorMessageObserver = Observer<Event<String>> {
        if (it == null) return@Observer
        val errorMessage = it.getContentIfNotHandled()
        if (errorMessage != null) showToast(errorMessage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        compositeDisposable = CompositeDisposable()
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(CreatePostViewModel::class.java)
        if (savedInstanceState != null) {
            viewModel.content = savedInstanceState.getString(BUNDLE_CONTENT)
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_post)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)

        setSupportActionBar(topToolbar)
        supportActionBar!!.apply {
            setTitle(R.string.post_create_title)
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

        postContentTextView.isSaveEnabled = false
        postContentTextView.setText(viewModel.content)
        compositeDisposable.add(
            postContentTextView.afterTextChangeEvents()
                .skipInitialValue()
                .subscribe { viewModel.content = it.editable.toString() }
        )

        observeViewModel()
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
                it.isEnabled = viewModel.isButtonSubmitEnabled.value == true
                it.isVisible = !(viewModel.isSubmitting.value ?: false)
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSubmitPost -> {
                viewModel.submitPost()
                return true
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (viewModel.isSubmitting.value == true) return
        super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(BUNDLE_CONTENT, viewModel.content)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        unobserveViewModel()
        super.onDestroy()
    }

    private fun observeViewModel() {
        viewModel.isSubmitting.observe(this, Observer<Boolean> {
            invalidateOptionsMenu()
        })
        viewModel.isButtonSubmitEnabled.observe(this, Observer<Boolean> {
            invalidateOptionsMenu()
        })

        viewModel.finish.observeForever(finishObserver)
        viewModel.errorMessage.observeForever(errorMessageObserver)
    }

    private fun unobserveViewModel() {
        viewModel.finish.removeObserver(finishObserver)
        viewModel.errorMessage.removeObserver(errorMessageObserver)
    }

    private fun showToast(message: String) {
        Toast.makeText(this@CreatePostActivity, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun createPostIntent(context: Context): Intent {
            return Intent(context, CreatePostActivity::class.java)
        }
    }
}