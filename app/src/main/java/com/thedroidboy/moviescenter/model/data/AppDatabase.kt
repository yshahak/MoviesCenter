package com.thedroidboy.moviescenter.model.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MovieItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun moviesDao(): MoviesDao
}