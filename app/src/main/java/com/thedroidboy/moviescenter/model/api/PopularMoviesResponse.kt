package com.thedroidboy.moviescenter.model.api

import com.thedroidboy.moviescenter.model.data.MovieItem

data class PopularMoviesResponse(
    val page: Int,
    val total_results: Int,
    val total_pages: Int,
    val results: List<MovieItem>
)