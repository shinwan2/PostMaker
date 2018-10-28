package com.shinwan2.postmaker.post

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.shinwan2.postmaker.R
import com.shinwan2.postmaker.databinding.FragmentTimelinePostsBinding
import com.shinwan2.postmaker.domain.model.CursorList
import com.shinwan2.postmaker.domain.model.Post
import com.shinwan2.postmaker.util.Event
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
        // on load more
        // bind load more indicators
    }

    override fun onDestroyView() {
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
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val adapter = TimelinePostsAdapter()
        recyclerView.adapter = adapter
        viewModel.items.observe(this, Observer<CursorList<Post>> {
            if (it != null) adapter.submitList(it.list)
        })
    }

    private fun observeViewModelForever() {
        viewModel.errorMessage.observeForever(errorMessageObserver)
    }

    private fun unobserveViewModelForever() {
        viewModel.errorMessage.removeObserver(errorMessageObserver)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}