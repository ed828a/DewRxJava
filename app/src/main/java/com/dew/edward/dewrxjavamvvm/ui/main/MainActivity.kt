package com.dew.edward.dewrxjavamvvm.ui.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import android.widget.Toast
import com.dew.edward.dewrxjavamvvm.App
import com.dew.edward.dewrxjavamvvm.R
import com.dew.edward.dewrxjavamvvm.adapter.MainVideoAdapter
import com.dew.edward.dewrxjavamvvm.model.Outcome
import com.dew.edward.dewrxjavamvvm.model.QueryData
import com.dew.edward.dewrxjavamvvm.util.DEFAULT_QUERY
import com.dew.edward.dewrxjavamvvm.util.KEY_QUERY
import com.dew.edward.dewrxjavamvvm.util.hideKeyboard
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.IOException
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private val TAG = javaClass.simpleName

    @Inject
    lateinit var viewModelFactory: MainViewModelFactory
    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
    }

    private lateinit var adapter: MainVideoAdapter
    private lateinit var preferences: SharedPreferences
    private lateinit var query: String
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).appComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        preferences = getPreferences(Context.MODE_PRIVATE)
        query = preferences.getString(KEY_QUERY, DEFAULT_QUERY)

        initActionBar()
        initRecyclerView()
        initSwipeToRefresh()
        initVideoDataListener()
        fetchVideos(query)
    }

    private fun initActionBar(){
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setIcon(R.mipmap.ic_launcher)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }

    private fun initRecyclerView(){
        adapter = MainVideoAdapter {
            Toast.makeText(this, "You clicked ${it.title} ", Toast.LENGTH_SHORT).show()
//            val intent = Intent(this@MainActivity, ExoVideoPlayActivity::class.java)
//            intent.putExtra(VIDEO_MODEL, it)
//            startActivity(intent)
        }

        mainListView.layoutManager = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
                 GridLayoutManager(this, 2)
        } else {
            LinearLayoutManager(this)
        }

        mainListView.adapter = adapter
        mainListView.setHasFixedSize(true)
    }

    private fun initSwipeToRefresh(){
        swipeRefreshLayout.setOnRefreshListener { viewModel.refreshVideos() }
    }

    private fun initVideoDataListener(){

        viewModel.videosOutcome.observe(this, Observer { outcome ->
            when (outcome){
                is Outcome.Progress -> swipeRefreshLayout.isRefreshing = outcome.loading
                is Outcome.Success -> {
                    if (outcome.data.isNotEmpty()){  // if data is Empty, don't do any thing
                        adapter.setVideoList(outcome.data)
                    }
                }

                is Outcome.Failure -> {
                    if (outcome.e is IOException)
                        Toast.makeText(this, R.string.need_internet, Toast.LENGTH_SHORT)
                                .show()
                    else Toast.makeText(this, R.string.failed_try_again, Toast.LENGTH_SHORT)
                            .show()
                }
            }
        })
    }

    private fun fetchVideos(query: String){
        Log.d(TAG, "getVideos called")

        viewModel.getVideos(QueryData(queryString = query))
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        searchView = menu.findItem(R.id.menu_search).actionView as SearchView
        initSearchView(searchView)

        return true
    }

    private fun initSearchView(searchView: SearchView){
        searchView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        searchView.layoutParams = ActionBar.LayoutParams(Gravity.END) as ViewGroup.LayoutParams?
        searchView.queryHint = "Search Movie ..."

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.trim()?.let {
                    if (it.isNotEmpty()) fetchVideos(it)
                }
                hideKeyboard(this@MainActivity)
                searchView.clearFocus()
                searchView.setQuery("", false)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.menu_search -> {
                // Todo: add search routine here
                true
            }
            android.R.id.home -> {
                finish()
                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
