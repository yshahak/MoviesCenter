package com.thedroidboy.moviescenter.view.activities

import android.view.View
import androidx.paging.PagedList
import com.thedroidboy.moviescenter.model.data.MovieItem

/**
 * an interface responsible to handle view related stuff
 */
interface MoviesView {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    fun isTowPane(): Boolean

    /**
     * determine the state of the offine card
     */
    fun setOfflineCardState()

    /**
     * handle a click event to open [MovieItem] details
     */
    fun openItemDetailScreen(item: MovieItem, viewClicked: View)

    /**
     * append new items to the movie list
     */
    fun appendItemsToList(movieItems: PagedList<MovieItem>)
}