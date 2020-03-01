package com.thedroidboy.moviescenter.view.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.thedroidboy.moviescenter.GlideRequests
import com.thedroidboy.moviescenter.R
import com.thedroidboy.moviescenter.isOnline
import com.thedroidboy.moviescenter.model.data.MovieItem
import com.thedroidboy.moviescenter.model.paging.NetworkState
import com.thedroidboy.moviescenter.view.adapters.MoviesAdapter
import com.thedroidboy.moviescenter.view.fragments.ItemDetailFragment
import com.thedroidboy.moviescenter.vm.MoviesViewModel
import kotlinx.android.synthetic.main.activity_item_list.*
import kotlinx.android.synthetic.main.item_list.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.stateViewModel


/**
 * An activity representing a list of MovieItems. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [MovieItemDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class MoviesActivity : AppCompatActivity(), MoviesView {

    //MoviesViewModel injected to MoviesListActivity
    private val moviesVM: MoviesViewModel by stateViewModel()
    //glide app injected to MoviesListActivity
    private val glide : GlideRequests by inject()
    private lateinit var list: RecyclerView
    private lateinit var adapter: MoviesAdapter
    private lateinit var offlineCard: CardView
    lateinit var swipeToRefresh: SwipeRefreshLayout

    private var twoPane: Boolean = false

    private val itemClickListener = View.OnClickListener { v ->
        val item = v.tag as MovieItem
        openItemDetailScreen(item, v)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)

        setSupportActionBar(detail_toolbar)
        detail_toolbar.title = title

        if (item_detail_container != null) {
            // The detail container view will be present only in the large-screen layouts (res/values-w900dp).
            // If this view is present, then the activity should be in two-pane mode.
            twoPane = true
        }
        list = item_list
        swipeToRefresh = swipe_refresh
        offlineCard = offline_card
        setOfflineCardState()
        initAdapter()
        initSwipeToRefresh()
        moviesVM.shouldLoadMoviesPage(page = 1)
    }

    private fun initAdapter() {
        adapter = MoviesAdapter(glide, itemClickListener)
        val layoutManager = GridLayoutManager(this, if (twoPane) 3 else 2)
        list.layoutManager = layoutManager
        list.adapter = adapter
        moviesVM.movies.observe(this, Observer<PagedList<MovieItem>> {
            appendItemsToList(it)
        })
        moviesVM.networkState.observe(this, Observer {
            adapter.notifyItemInserted(adapter.itemCount)
            setOfflineCardState()
        })
    }

    private fun initSwipeToRefresh() {
        moviesVM.refreshState.observe(this, Observer {
            swipe_refresh.isRefreshing = it == NetworkState.LOADING
        })
        swipe_refresh.setOnRefreshListener {
            moviesVM.refresh()
        }
    }

    override fun isTowPane() = twoPane

    override fun setOfflineCardState() {
        offlineCard.visibility = if (isOnline()) View.GONE else View.VISIBLE
    }

    override fun openItemDetailScreen(item: MovieItem, viewClicked: View) {
        if (isTowPane()) {
            val fragment = ItemDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ItemDetailFragment.ARG_ITEM, item)
                }
            }
            this.supportFragmentManager
                .beginTransaction()
                .replace(R.id.item_detail_container, fragment)
                .commit()
        } else {
            //holder for transition animation
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, viewClicked, "thumbnail")
            val intent = Intent(this, MovieItemDetailActivity::class.java).apply {
                putExtra(ItemDetailFragment.ARG_ITEM, item)
            }
            startActivity(intent, options.toBundle())
        }
    }

    override fun appendItemsToList(movieItems: PagedList<MovieItem>) {
        adapter.submitList(movieItems) {
            if (movieItems.isNotEmpty()){
                // Workaround for an issue where RecyclerView incorrectly uses the loading / spinner
                // item added to the end of the list as an anchor during initial load.
                val position = (list.layoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition()
                if (position == 0) {
                    list.scrollToPosition(position)
                }
            }

        }
    }

}
