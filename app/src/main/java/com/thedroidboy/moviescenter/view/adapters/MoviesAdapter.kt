package com.thedroidboy.moviescenter.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.thedroidboy.moviescenter.GlideRequests
import com.thedroidboy.moviescenter.R
import com.thedroidboy.moviescenter.model.data.MovieItem

/**
 * A simple adapter implementation that shows Movies Thumbnail
 */
class MoviesAdapter(private val glide: GlideRequests, private val itemClickListener: View.OnClickListener) : PagedListAdapter<MovieItem, RecyclerView.ViewHolder>(MOVIE_COMPARATOR) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MovieItemViewHolder).bind(getItem(position))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) = onBindViewHolder(holder, position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = MovieItemViewHolder.create(parent, glide, itemClickListener)

    companion object {
        const val TAG = "MoviesAdapter"

        val MOVIE_COMPARATOR = object : DiffUtil.ItemCallback<MovieItem>() {
            override fun areContentsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean =
                oldItem.id == newItem.id

            override fun getChangePayload(oldItem: MovieItem, newItem: MovieItem): Any? {
                return null
            }
        }
    }
}

/**
 * A RecyclerView ViewHolder that displays a movie item thumbnail.
 */
class MovieItemViewHolder(view: View, private val glide: GlideRequests, itemClickListener: View.OnClickListener) : RecyclerView.ViewHolder(view) {
    private val thumbnail: ImageView = view as ImageView
    private var post: MovieItem? = null

    init {
        view.setOnClickListener(itemClickListener)
    }

    fun bind(post: MovieItem?) {
        this.post = post
        post?.let {
            thumbnail.tag = post
            glide.load("https://image.tmdb.org/t/p/w185/${post.poster_path}")
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(thumbnail)
        }
    }

    companion object {
        fun create(parent: ViewGroup, glide: GlideRequests, itemClickListener: View.OnClickListener): MovieItemViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.movie_item, parent, false)
            return MovieItemViewHolder(view, glide, itemClickListener)
        }
    }

}