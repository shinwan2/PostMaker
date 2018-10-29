package com.shinwan2.postmaker.widget

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shinwan2.postmaker.R

class LoadMoreAdapter<T : RecyclerView.ViewHolder>(
    val innerAdapter: RecyclerView.Adapter<T>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var isLoadingMore = false
        set(value) {
            val wasVisible = field
            field = value
            val itemSize = innerAdapter.itemCount
            if (wasVisible && !field) {
                notifyItemRemoved(itemSize)
                if (itemSize > 0) {
                    notifyItemChanged(itemSize - 1, java.lang.Boolean.FALSE)
                }
            } else if (!wasVisible && field) {
                notifyItemInserted(itemSize)
                if (itemSize > 0) {
                    notifyItemChanged(itemSize - 1, java.lang.Boolean.FALSE)
                }
            }
        }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder !is ProgressBarViewHolder) {
            @Suppress("UNCHECKED_CAST")
            innerAdapter.onViewAttachedToWindow(holder as T)
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        if (holder !is ProgressBarViewHolder) {
            @Suppress("UNCHECKED_CAST")
            innerAdapter.onViewDetachedFromWindow(holder as T)
        }
        super.onViewDetachedFromWindow(holder)
    }

    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        super.registerAdapterDataObserver(observer)
        innerAdapter.registerAdapterDataObserver(observer)
    }

    override fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        super.unregisterAdapterDataObserver(observer)
        innerAdapter.unregisterAdapterDataObserver(observer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == R.layout.item_load_more) {
            val root = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_load_more, parent, false)
            return ProgressBarViewHolder(root)
        }
        return innerAdapter.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ProgressBarViewHolder) return
        @Suppress("UNCHECKED_CAST")
        innerAdapter.onBindViewHolder(holder as T, position)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int,
        payloads: List<Any>
    ) {
        if (holder is ProgressBarViewHolder) return
        @Suppress("UNCHECKED_CAST")
        innerAdapter.onBindViewHolder(holder as T, position, payloads)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (holder is ProgressBarViewHolder) return
        @Suppress("UNCHECKED_CAST")
        innerAdapter.onViewRecycled(holder as T)
        super.onViewRecycled(holder)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoadingMore && position == innerAdapter.itemCount) {
            R.layout.item_load_more
        } else {
            innerAdapter.getItemViewType(position)
        }
    }

    override fun getItemCount(): Int {
        return innerAdapter.itemCount + if (isLoadingMore) 1 else 0
    }

    internal class ProgressBarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
