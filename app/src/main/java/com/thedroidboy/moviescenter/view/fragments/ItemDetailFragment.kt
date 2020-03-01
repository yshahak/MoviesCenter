package com.thedroidboy.moviescenter.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thedroidboy.moviescenter.GlideRequests
import com.thedroidboy.moviescenter.R
import com.thedroidboy.moviescenter.model.data.MovieItem
import kotlinx.android.synthetic.main.movie_details.view.*
import org.koin.android.ext.android.inject

/**
 * A fragment representing a single movie Item detail screen.
 * This fragment is either contained in a [MoviesListActivity]
 * in two-pane mode (on tablets) or a [MovieItemDetailActivity]
 * on handsets.
 */
class ItemDetailFragment : Fragment() {

    /**
     * The MovieItem content this fragment is presenting.
     */
    private var item: MovieItem? = null

    //glide app injected to ItemDetailFragment
    private val glide : GlideRequests by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM)) {
                item = it.getParcelable(ARG_ITEM)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.movie_details, container, false)
        item?.let {
            rootView.release_date.text = it.release_date
            rootView.description.text = it.overview
            rootView.popularity.text = "${it.vote_average}/10"
            it.poster_path?.let { path ->
                glide.load("https://image.tmdb.org/t/p/w185/$path")
                    .centerCrop()
                    .into(rootView.thumbnail)
            }
        }

        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the item that this fragment
         * represents.
         */
        const val ARG_ITEM = "item_id"
    }
}
