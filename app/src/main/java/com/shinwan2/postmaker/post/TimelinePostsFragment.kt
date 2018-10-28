package com.shinwan2.postmaker.post

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.shinwan2.postmaker.R
import com.shinwan2.postmaker.databinding.FragmentTimelinePostsBinding
import com.shinwan2.postmaker.domain.model.CursorList
import com.shinwan2.postmaker.util.Event
import com.shinwan2.postmaker.widget.ListBoundaryCallback
import com.shinwan2.postmaker.widget.LoadMoreAdapter
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_timeline_posts.recyclerView
import kotlinx.android.synthetic.main.fragment_timeline_posts.swipeRefreshLayout
import javax.inject.Inject

class TimelinePostsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: TimelinePostsViewModel

    private val errorMessageObserver = Observer<Event<String>> {
        if (it == null) return@Observer
        val errorMessage = it.getContentIfNotHandled()
        if (errorMessage != null) showToast(errorMessage)
    }

    private val successMessageObserver = Observer<Event<Int>> {
        if (it == null) return@Observer
        val errorMessage = it.getContentIfNotHandled()
        if (errorMessage != null) showToast(errorMessage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(TimelinePostsViewModel::class.java)
        observeViewModelForever()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentTimelinePostsBinding>(
            inflater, R.layout.fragment_timeline_posts, container, false
        )
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout.setOnRefreshListener { viewModel.refresh() }
        initializeRecyclerView()
        observeConfirmationDialog()
    }

    private fun observeConfirmationDialog() {
        viewModel.isShowingDeleteConfirmationDialog.observe(this, Observer<Boolean> {
            if (it == null || it == false) return@Observer
            MaterialDialog.Builder(requireContext())
                .title(R.string.post_delete_dialog_title)
                .content(R.string.post_delete_dialog_message)
                .positiveText(R.string.all_button_ok)
                .onPositive { _, _ -> viewModel.confirmDeletePost() }
                .negativeText(R.string.all_button_cancel)
                .onNegative { _, _ -> viewModel.cancelDeletePost() }
                .show()
        })
    }

    override fun onDestroyView() {
        deinitializeRecyclerView()
        swipeRefreshLayout.setOnRefreshListener(null)
        swipeRefreshLayout.isRefreshing = false
        swipeRefreshLayout.clearAnimation()
        super.onDestroyView()
    }

    override fun onDestroy() {
        unobserveViewModelForever()
        super.onDestroy()
    }

    private fun initializeRecyclerView() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        val diffAdapter = TimelinePostsAdapter()
        val adapter = LoadMoreAdapter(diffAdapter)
        recyclerView.adapter = adapter

        recyclerView.addItemDecoration(
            HorizontalDividerItemDecoration.Builder(requireContext())
                .color(Color.TRANSPARENT)
                .size(resources.getDimensionPixelSize(R.dimen.divider_size))
                .showLastDivider()
                .build()
        )
        recyclerView.addOnScrollListener(
            ListBoundaryCallback(
                object : ListBoundaryCallback.Continuable {
                    override val hasNextPage: Boolean
                        get() = viewModel.hasNextPage
                },
                object : ListBoundaryCallback.LoadMoreListener {
                    override fun loadMore() {
                        viewModel.loadMore()
                    }
                })
        )

        viewModel.items.observe(this, Observer<CursorList<PostViewModel>> {
            if (it != null) diffAdapter.submitList(it.list)
        })
        viewModel.isLoadingMore.observe(this, Observer<Boolean> {
            if (it != null) adapter.isLoadingMore = it
        })
    }

    private fun observeViewModelForever() {
        viewModel.successMessage.observeForever(successMessageObserver)
        viewModel.errorMessage.observeForever(errorMessageObserver)
    }

    private fun unobserveViewModelForever() {
        viewModel.successMessage.observeForever(successMessageObserver)
        viewModel.errorMessage.removeObserver(errorMessageObserver)
    }

    private fun deinitializeRecyclerView() {
        recyclerView.clearOnScrollListeners()
    }

    private fun showToast(@StringRes stringId: Int) {
        Toast.makeText(requireContext(), stringId, Toast.LENGTH_SHORT).show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}