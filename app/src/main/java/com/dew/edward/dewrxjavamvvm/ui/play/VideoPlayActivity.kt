package com.dew.edward.dewrxjavamvvm.ui.play

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import com.commit451.youtubeextractor.YouTubeExtraction
import com.commit451.youtubeextractor.YouTubeExtractor
import com.dew.edward.dewrxjavamvvm.App
import com.dew.edward.dewrxjavamvvm.App.Companion.localBroadcastManager
import com.dew.edward.dewrxjavamvvm.R
import com.dew.edward.dewrxjavamvvm.adapter.MainVideoAdapter
import com.dew.edward.dewrxjavamvvm.model.Outcome
import com.dew.edward.dewrxjavamvvm.model.QueryData
import com.dew.edward.dewrxjavamvvm.model.QueryType
import com.dew.edward.dewrxjavamvvm.model.VideoModel
import com.dew.edward.dewrxjavamvvm.ui.main.MainViewModel
import com.dew.edward.dewrxjavamvvm.ui.main.MainViewModelFactory
import com.dew.edward.dewrxjavamvvm.util.*
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_video_play.*
import kotlinx.android.synthetic.main.content_list.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.IOException
import javax.inject.Inject

class VideoPlayActivity : AppCompatActivity() {
    private val TAG = javaClass.simpleName

    @Inject
    lateinit var viewModelFactory: MainViewModelFactory
    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
    }

    private lateinit var mAdapter: MainVideoAdapter
    private lateinit var videoModel: VideoModel
    private lateinit var extractor: YouTubeExtractor

    // bandwidth meter to measure and estimate bandwidth
    private var player: SimpleExoPlayer? = null
    private var playbackPosition: Long = 0
    private var currentWindow: Int = 0
    private var playWhenReady = true
    private var videoUrl: String = ""
    private val backStack = DewStack<QueryData>(BACKING_STEPS)
    private var resetListWhenGotNewData = false

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)

        if (ContextCompat.checkSelfPermission(this@VideoPlayActivity,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@VideoPlayActivity,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        videoModel = intent.getParcelableExtra(VIDEO_MODEL)

        extractor = YouTubeExtractor.Builder().okHttpClientBuilder(null).build()

        if (savedInstanceState != null) { // when Rotation, no need to search on the net.
            playbackPosition = savedInstanceState.getLong(PLAYBACK_POSITION)
            videoUrl = savedInstanceState.getString(VIDEO_URL)

        } else {
            extractUrl(videoModel.videoId)
        }


        if (resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
            textVideoPlayTitle?.text = videoModel.title

            initRelatedList()
            initSearch()
            initVideoDataListener()

            processVideoId(videoModel.videoId)
        }
    }

    private fun processVideoId(videoId: String){
        val queryData = QueryData(videoId, type = QueryType.RELATED_VIDEO_ID)
        viewModel.getVideos(queryData)
        backStack.push(queryData)
    }

    private fun extractUrl(videoId: String) {
        extractor.extract(videoId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { extraction ->
                            bindVideoToPlayer(extraction)
                        },
                        { error ->
                            errorHandler(error)
                        }
                )
    }

    private fun initRelatedList() {
        mAdapter = MainVideoAdapter(viewModel) {
            Log.d(TAG, "listView onClick: videoId: ${it.videoId}")
            extractUrl(it.videoId)
            processVideoId(it.videoId)
            textVideoPlayTitle?.text = it.title
            intent.putExtra(VIDEO_MODEL, it)
            resetListWhenGotNewData = true
        }

        with(recyclerRelatedListView) {
            adapter = mAdapter
            layoutManager = GridLayoutManager(context, 2) as RecyclerView.LayoutManager?
            setHasFixedSize(true)
        }
    }

    private fun initSearch() {
        buttonSearch.setOnSearchClickListener {
            buttonDownload.visibility = View.GONE
            textVideoPlayTitle.visibility = View.GONE

            buttonSearch.onActionViewExpanded()
        }

        buttonSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.trim()?.let {
                    if (it.isNotEmpty()) {
                        viewModel.getVideos(QueryData(queryString = it))
                        mAdapter.resetVideoList()
                        backStack.push(QueryData(it, type = QueryType.QUERY_STRING))
                        recyclerRelatedListView.scrollToPosition(0)
                    }
                }

                buttonSearch.onActionViewCollapsed()
                buttonDownload.visibility = View.VISIBLE
                textVideoPlayTitle.visibility = View.VISIBLE

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        val closeButton = buttonSearch.findViewById<ImageView>(R.id.search_close_btn)
        closeButton.setOnClickListener {
            buttonSearch.onActionViewCollapsed()
            buttonDownload.visibility = View.VISIBLE
            textVideoPlayTitle.visibility = View.VISIBLE
        }
    }

    private fun bindVideoToPlayer(result: YouTubeExtraction) {
        if (result.videoStreams.isEmpty()) {
            Toast.makeText(this@VideoPlayActivity,
                    "This video isn't playable. Please try others.", Toast.LENGTH_LONG).show()
            return
        }

        videoUrl = result.videoStreams.first().url
        playbackPosition = 0  // new video start
        Log.d(TAG, "bindVideoToPlayer() videoUrl: $videoUrl")
        if (player != null) {
            releasePlayer()
        }
        initializePlayer(this, videoUrl)
    }

    private fun errorHandler(t: Throwable) {
        t.printStackTrace()
        Toast.makeText(this, "It failed to extract URL from YouTube.", Toast.LENGTH_SHORT).show()
    }

    private fun initializePlayer(context: Context, videoUrl: String) {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(
                    DefaultRenderersFactory(this@VideoPlayActivity),
                    DefaultTrackSelector(),
                    DefaultLoadControl())

            videoView.player = player
            player!!.playWhenReady = playWhenReady
            Log.d(TAG, "initializePlayer, playbackPosition = $playbackPosition, \n videoUrl = $videoUrl")
            player!!.seekTo(currentWindow, playbackPosition)

        }
        val uri = Uri.parse(videoUrl)
        val mediaSource =
                ExtractorMediaSource.Factory(
                        DefaultHttpDataSourceFactory("exoPlayer"))
                        .createMediaSource(uri)
        player!!.prepare(mediaSource, false, false)
    }

    private fun releasePlayer() {
        if (player != null) {
            currentWindow = player!!.currentWindowIndex
            playWhenReady = player!!.playWhenReady
            player!!.release()
            player = null
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (Util.SDK_INT > 23) {
            playbackPosition = player?.currentPosition ?: 0
        }
        outState?.putLong(PLAYBACK_POSITION, playbackPosition)
        outState?.putString(VIDEO_URL, videoUrl)
    }

    public override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            Log.d(TAG, "onStart, videoUrl = $videoUrl")
            initializePlayer(this, videoUrl)
        }
    }

    public override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || player == null) {
            Log.d(TAG, "onResume, videoUrl = $videoUrl")
            initializePlayer(this, videoUrl)
        }
    }

    public override fun onPause() {
        if (Util.SDK_INT <= 23) {

            playbackPosition = player?.currentPosition ?: 0

            Log.d(TAG, "onPause, playbackPosition = $playbackPosition")
            releasePlayer()
        }

        super.onPause()
    }

    public override fun onStop() {
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
        super.onStop()
    }

    fun fullscreen(view: View) {
        requestedOrientation = if (resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun onBackPressed() {

        val backList = backStack.pop()
        if (backList == null) {
            super.onBackPressed()
        } else {
            viewModel.getVideos(backList)
            recyclerRelatedListView.scrollToPosition(0)
            (recyclerRelatedListView.adapter as? MainVideoAdapter)?.resetVideoList()
        }
    }

    private fun initVideoDataListener(){

        viewModel.videosOutcome.observe(this, Observer { outcome ->
            Log.d(TAG, "initVideoDataListener, outcome: $outcome")
            when (outcome){

                is Outcome.Success -> {

                    if (outcome.data.isNotEmpty()){
                        if (resetListWhenGotNewData){
                            recyclerRelatedListView.scrollToPosition(0)
                            mAdapter.resetVideoList()
                            resetListWhenGotNewData = false
                        }
                        mAdapter.setVideoList(outcome.data)

                    } else {
                        Toast.makeText(this, "No more result", Toast.LENGTH_SHORT).show()
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
}
