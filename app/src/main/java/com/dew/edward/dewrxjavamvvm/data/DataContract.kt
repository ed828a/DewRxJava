package com.dew.edward.dewrxjavamvvm.data

import com.dew.edward.dewrxjavamvvm.model.Outcome
import com.dew.edward.dewrxjavamvvm.model.QueryData
import com.dew.edward.dewrxjavamvvm.model.VideoModel
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject


/**
 * Created by Edward on 7/20/2018.
 */
interface DataContract {
    interface Repository {
        val videoFetchOutcome: PublishSubject<Outcome<List<VideoModel>>>
        fun fetchVideos(query: QueryData)
        fun refreshVideos()
        fun cachingVideos(videos: List<VideoModel>)
        fun deleteCachedVideos()
        fun handleVideoError(error: Throwable)
    }

    interface Local {
        fun getVideos(): Flowable<List<VideoModel>>
        fun saveVideos(videos: List<VideoModel>)
        fun deleteVideos()
    }

    interface Remote {
        fun getVideos(query: QueryData): Single<List<VideoModel>>
    }
}