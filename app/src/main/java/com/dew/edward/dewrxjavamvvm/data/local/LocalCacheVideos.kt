package com.dew.edward.dewrxjavamvvm.data.local

import com.dew.edward.dewrxjavamvvm.data.DataContract
import com.dew.edward.dewrxjavamvvm.data.repository.Scheduler
import com.dew.edward.dewrxjavamvvm.model.VideoModel
import com.dew.edward.dewrxjavamvvm.util.performOnBack
import io.reactivex.Completable
import io.reactivex.Flowable
import javax.inject.Inject


/**
 * Created by Edward on 7/20/2018.
 */
class LocalCacheVideos @Inject constructor(
        private val videosDb: VideosDb,
        private val scheduler: Scheduler
) : DataContract.Local {

    override fun getVideos(): Flowable<List<VideoModel>> = videosDb.youtubeDao().getAll()

    override fun saveVideos(videos: List<VideoModel>) {
        // todo: in the future, set return Completable value, and process in repository
        Completable.fromAction {
            videosDb.youtubeDao().insert(videos)
        }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun deleteVideos() {
        Completable.fromAction {
            videosDb.youtubeDao().deleteAll()
        }
                .performOnBack(scheduler)
                .subscribe()
    }

}