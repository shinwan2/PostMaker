package com.shinwan2.postmaker.widget

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

class ListBoundaryCallback(
    private val continuable: Continuable,
    private val loadMoreListener: LoadMoreListener
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return

        val nextPageAvailable = continuable.hasNextPage
        val totalItemCount = recyclerView.adapter.itemCount
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

        if (nextPageAvailable && lastVisibleItemPosition >= totalItemCount - 1) {
            recyclerView.post { loadMoreListener.loadMore() }
        }
    }

    interface Continuable {
        val hasNextPage: Boolean
    }

    interface LoadMoreListener {
        fun loadMore()
    }
}
