package com.dew.edward.dewrxjavamvvm.data.network

import android.util.Log
import com.dew.edward.dewrxjavamvvm.data.DataContract
import com.dew.edward.dewrxjavamvvm.model.*
import com.dew.edward.dewrxjavamvvm.util.extractDate
import io.reactivex.Single
import javax.inject.Inject


/**
 * Created by Edward on 7/20/2018.
 */
class RemoteVideos @Inject constructor(private val youtubeAPI: YoutubeAPI) : DataContract.Remote {

    private val resultPageInfo = ResultPageInfo()

    override fun getVideos(query: QueryData): Single<List<VideoModel>> {
        val response = if (query.isInitializer) {
            resultPageInfo.reset()
            if (query.type == QueryType.RELATED_VIDEO_ID) {
                youtubeAPI.getRelatedVideos(query.queryString)
            } else {
                youtubeAPI.searchVideo(query = query.queryString)
            }
        } else {
            if (query.type == QueryType.RELATED_VIDEO_ID) {
                youtubeAPI.getRelatedVideos(query.queryString, resultPageInfo.nextPage)
            } else {
                youtubeAPI.searchVideo(query.queryString, resultPageInfo.nextPage)
            }
        }



        return response.flatMap { youtubeResponse ->
            Single.create<List<VideoModel>> { emitter ->
                with(resultPageInfo) {
                    prevPage = youtubeResponse.prevPageToken ?: ""
                    nextPage = youtubeResponse.nextPageToken ?: ""
                    totalResults = youtubeResponse.pageInfo.totalResults ?: "0"
                    if (youtubeResponse.items != null) receivedItems += youtubeResponse.items.size
                    Log.d("Remote Videos", "resultPageInfo.nextPage: $nextPage")
                }

                try {
                    // if youtubeResponse.items.isEmpty(), ui doesn't update ui
                    val videoList = youtubeResponse.items.map {
                        VideoModel(it.snippet.title,
                                it.snippet.publishedAt.extractDate(),
                                it.snippet.thumbnails.high.url,
                                it.id.videoId
                        )
                    }
//                    Log.d("Remote Videos", "videoList data: $videoList")
                    emitter.onSuccess(videoList)
                } catch (e: Throwable) {
                    Log.d("RemoteVideos", "getVideos() failed: ${e.message}")
                    emitter.onError(e)
                }
            }


        }
    }
}