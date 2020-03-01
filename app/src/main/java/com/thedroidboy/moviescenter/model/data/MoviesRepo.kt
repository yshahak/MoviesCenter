package com.thedroidboy.moviescenter.model.data

import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.thedroidboy.moviescenter.model.api.MoviesApi
import com.thedroidboy.moviescenter.model.api.PopularMoviesResponse
import com.thedroidboy.moviescenter.model.paging.Listing
import com.thedroidboy.moviescenter.model.paging.NetworkState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Repository class for handling data operations.
 */
class MoviesRepo(private val moviesDao: MoviesDao, private val moviesApi: MoviesApi) : PagedList.BoundaryCallback<MovieItem>() {

    companion object {
        const val TAG = "MoviesRepo"
        const val PAGE_SIZE = 20
    }

    private val networkState = MutableLiveData<NetworkState>()
    private var page = 1

    /**
     * get movies from Repository
     * behind the scene the [PagedList] will be used to load data as needed from server and update the db
     */
    fun getMovies(page: Int): Listing<MovieItem> {
        this.page = page
        //will be responsible to handle refresh request from ui
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = refreshTrigger.switchMap {
            refresh(page)
        }
        val livePagedList = moviesDao.getMovies().toLiveData(
            pageSize = PAGE_SIZE,
            boundaryCallback = this
        )

        return Listing(
            pagedList = livePagedList,
            networkState = networkState,
            refresh = {
                refreshTrigger.value = null
            },
            refreshState = refreshState
        )
    }

    private fun refresh(page: Int): LiveData<NetworkState> {
        Log.d(TAG, "refresh\tpage=$page")
        networkState.value = NetworkState.LOADING
        moviesApi.popularMovies(page).enqueue(
            handleMoviesRequest(networkState)
        )
        return networkState
    }

    /**
     * Database returned 0 items. We should query the backend for more items.
     */
    override fun onZeroItemsLoaded() {
        Log.d(TAG, "onZeroItemsLoaded")
        refresh(page)
    }

    /**
     * User reached to the end of the list.
     */
    override fun onItemAtEndLoaded(itemAtEnd: MovieItem) {
        Log.d(TAG, "onItemAtEndLoaded")
        page++
        refresh(page)
    }

    override fun onItemAtFrontLoaded(itemAtFront: MovieItem) {
        Log.d(TAG, "onItemAtFrontLoaded")
    }

    private fun handleMoviesRequest(networkState: MutableLiveData<NetworkState>): Callback<PopularMoviesResponse> {
        return object : Callback<PopularMoviesResponse> {
            override fun onFailure(call: Call<PopularMoviesResponse>, t: Throwable) {
                networkState.value = NetworkState.error(t.message)
            }

            override fun onResponse(call: Call<PopularMoviesResponse>, response: Response<PopularMoviesResponse>) {
                takeIf { response.isSuccessful }?.let {
                    response.body()?.results?.let {
                        AsyncTask.THREAD_POOL_EXECUTOR.execute {
                            moviesDao.insertAll(it.filter { movieItem -> movieItem.poster_path != null })
                        }
                    }
                }
                networkState.postValue(NetworkState.LOADED)
            }
        }
    }
}
