package com.thedroidboy.moviescenter.model.di

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import com.thedroidboy.moviescenter.GlideApp
import com.thedroidboy.moviescenter.model.api.MoviesApi
import com.thedroidboy.moviescenter.model.data.AppDatabase
import com.thedroidboy.moviescenter.model.data.MoviesRepo
import com.thedroidboy.moviescenter.vm.MoviesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * di module to provide. As this is small app no scoping really needed
 * implemented via Koin!
 */
val dataModule = module {

    viewModel { (handle: SavedStateHandle) -> MoviesViewModel(handle, get()) }

    //holder for AppDatabase
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java, "movies-db"
        ).build()
    }

    //holder for retrofit MoviesApi
    single {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(MoviesApi::class.java)
    }

    //holder for Glide
    single { GlideApp.with(get<Context>()) }

    //holder for MoviesRepo
    single { MoviesRepo(get<AppDatabase>().moviesDao(), get()) }


}