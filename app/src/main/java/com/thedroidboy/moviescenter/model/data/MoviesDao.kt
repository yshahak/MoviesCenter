package com.thedroidboy.moviescenter.model.data

import androidx.paging.DataSource
import androidx.room.*

@Dao
interface MoviesDao {

    @Query("SELECT * FROM movies")
    fun getAll(): List<MovieItem>

    // The Int type parameter tells Room to use a PositionalDataSource object.
    @Query("SELECT * FROM movies ORDER BY popularity DESC")
    fun getMovies(): DataSource.Factory<Int, MovieItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(movies: List<MovieItem>)

    @Delete
    fun delete(movie: MovieItem)
}