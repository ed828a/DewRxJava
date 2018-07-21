package com.dew.edward.dewrxjavamvvm.data.repository

import android.util.Log
import com.dew.edward.dewrxjavamvvm.data.DataContract
import com.dew.edward.dewrxjavamvvm.model.Outcome
import com.dew.edward.dewrxjavamvvm.model.QueryData
import com.dew.edward.dewrxjavamvvm.model.VideoModel
import com.dew.edward.dewrxjavamvvm.util.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * Created by Edward on 7/20/2018.
 */
class VideoRepository @Inject constructor(
        private val local: DataContract.Local,
        private val remote: DataContract.Remote,
        private val scheduler: Scheduler,
        private val compositeDisposable: CompositeDisposable
) : DataContract.Repository {
    private val TAG = javaClass.simpleName

    private var queryStub: QueryData? = null

    override val videoFetchOutcome: PublishSubject<Outcome<List<VideoModel>>> =
            PublishSubject.create<Outcome<List<VideoModel>>>()


    override fun fetchVideos(query: QueryData) {
        queryStub = query
        videoFetchOutcome.loading(true)
        // Observe changes to the Db
        if (query.isInitializer) {
            remoteFetch(query)
            queryStub?.isInitializer = false
        } else {
            local.getVideos()
                    .performOnBackOutOnMain(scheduler)
                    .doAfterNext {
                        if (Sync.shouldSync(SyncKeys.INIT_QUERY, 2, TimeUnit.HOURS) ||
                                Sync.shouldSync(SyncKeys.INIT_QUERY, 2, TimeUnit.HOURS))
                            refreshVideos()
                    }
                    .subscribe(
                            { videoList ->
                                if (videoList.isNotEmpty()) {
                                    videoFetchOutcome.success(videoList)
                                } else {
                                    remoteFetch(query)
                                }
                            },
                            { error -> handleVideoError(error) }
                    )
                    .addTo(compositeDisposable)
        }

    }

    private fun remoteFetch(query: QueryData) {
        videoFetchOutcome.loading(true)

        remote.getVideos(query)
                .performOnBackOutOnMain(scheduler)
                .doAfterSuccess { videoList ->
                    if (videoList.isNotEmpty()) {
                        deleteCachedVideos()
                        cachingVideos(videoList)
                    }
                }
                .updateSyncStatus(key = SyncKeys.INIT_QUERY)
                .subscribe({ videoList ->
//                    Log.d(TAG, "remote.getVideos subscribe success: videoList = ${videoList?.toString()}")

                    videoFetchOutcome.success(videoList)
                }, { error -> handleVideoError(error) })
                .addTo(compositeDisposable)
    }


    override fun refreshVideos() {
        if (queryStub != null) {
            remoteFetch(queryStub!!)
        }
    }

    override fun cachingVideos(videos: List<VideoModel>) {
        local.saveVideos(videos)
    }

    override fun deleteCachedVideos() {
        local.deleteVideos()
    }

    override fun handleVideoError(error: Throwable) {
        videoFetchOutcome.failed(error)
    }
}