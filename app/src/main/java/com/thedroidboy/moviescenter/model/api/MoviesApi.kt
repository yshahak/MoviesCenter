package com.thedroidboy.moviescenter.model.api

import com.thedroidboy.moviescenter.BuildConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface MoviesApi {

    @GET("3/movie/popular?api_key=${BuildConfig.api_key}")
    fun popularMovies(@Query("page") page: Int): Call<PopularMoviesResponse>
}