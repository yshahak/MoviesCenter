package com.thedroidboy.moviescenter.vm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.thedroidboy.moviescenter.model.data.MoviesRepo

/**
 * A ViewModel class responsible to communicate with [MoviesRepo]
 */
class MoviesViewModel(private val savedStateHandle: SavedStateHandle, private val repository: MoviesRepo) : ViewModel() {

    private val repoResult = savedStateHandle.getLiveData<Int>("page").map { page ->
        repository.getMovies(page)
    }
    val movies = switchMap(repoResult) { it.pagedList }
    val networkState = switchMap(repoResult) { it.networkState }
    val refreshState = switchMap(repoResult) { it.refreshState }

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    /**
     * should load movies or this page already there
     */
    fun shouldLoadMoviesPage(page: Int): Boolean {
        if (savedStateHandle.get<Int>("page") == page) {
            return false
        }
        savedStateHandle.set("page", page)
        return true
    }

}