package com.shinwan2.postmaker.post

import android.graphics.Color
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.shinwan2.postmaker.databinding.ItemPostBinding
import com.shinwan2.postmaker.domain.model.Post

private val COLOR_GENERATOR = ColorGenerator.MATERIAL

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
            return oldItem.textContent == newItem.textContent &&
                oldItem.createdTimestamp == newItem.createdTimestamp
        }
    }
}

internal class PostViewHolder(
    private val binding: ItemPostBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.viewModel = post

        val textDrawable = TextDrawable.builder()
            .beginConfig()
                .textColor(Color.WHITE)
                .toUpperCase()
                .bold()
            .endConfig()
            .buildRound(
                post.user?.displayName?.first().toString(),
                COLOR_GENERATOR.getColor(post.user?.email)
            )
        binding.userPhotoImageView.setImageDrawable(textDrawable)

        binding.executePendingBindings()
    }
}