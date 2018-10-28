package com.shinwan2.postmaker.post

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.shinwan2.postmaker.databinding.ItemPostBinding
import com.shinwan2.postmaker.domain.model.Post

internal class TimelinePostsAdapter : ListAdapter<Post, PostViewHolder>(PostDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPostBinding.inflate(inflater, parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class PostDiffUtilCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.postId == newItem.postId
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            // TODO("use display name, text content, timestamp")
            return oldItem.textContent == newItem.textContent ||
                oldItem.createdTimestamp == newItem.createdTimestamp
        }
    }
}

internal class PostViewHolder(
    private val binding: ItemPostBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.viewModel = post
        binding.executePendingBindings()
    }
}