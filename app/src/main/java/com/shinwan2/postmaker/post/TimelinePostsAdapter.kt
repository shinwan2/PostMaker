package com.shinwan2.postmaker.post

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.shinwan2.postmaker.databinding.ItemPostBinding
import com.shinwan2.postmaker.util.buildUserInitialCharImage

internal class TimelinePostsAdapter
    : ListAdapter<PostViewModel, PostViewHolder>(PostDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPostBinding.inflate(inflater, parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class PostDiffUtilCallback : DiffUtil.ItemCallback<PostViewModel>() {
        override fun areItemsTheSame(oldItem: PostViewModel, newItem: PostViewModel): Boolean {
            return oldItem.postId == newItem.postId
        }

        override fun areContentsTheSame(oldItem: PostViewModel, newItem: PostViewModel): Boolean {
            return oldItem.textContent == newItem.textContent &&
                oldItem.createdDateTime == newItem.createdDateTime
        }
    }
}

internal class PostViewHolder(
    private val binding: ItemPostBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: PostViewModel) {
        binding.viewModel = post
        binding.userPhotoImageView.setImageDrawable(
            buildUserInitialCharImage(post.posterDisplayName, post.posterEmail)
        )
        binding.executePendingBindings()
    }
}