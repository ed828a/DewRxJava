package com.dew.edward.dewrxjavamvvm.di

import android.content.Context
import com.dew.edward.dewrxjavamvvm.data.DataContract
import com.dew.edward.dewrxjavamvvm.data.network.RemoteVideos
import com.dew.edward.dewrxjavamvvm.data.network.YoutubeAPI
import com.dew.edward.dewrxjavamvvm.data.local.LocalCacheVideos
import com.dew.edward.dewrxjavamvvm.data.local.VideosDb
import com.dew.edward.dewrxjavamvvm.data.repository.AppScheduler
import com.dew.edward.dewrxjavamvvm.data.repository.Scheduler
import com.dew.edward.dewrxjavamvvm.data.repository.VideoRepository
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton


/**
 * Created by Edward on 7/21/2018.
 */

@Module
class DataModule {

    @Singleton
    @Provides
    fun provideDataRepository(
            local: DataContract.Local,
            remote: DataContract.Remote,
            scheduler: Scheduler,
            compositeDisposable: CompositeDisposable): DataContract.Repository {

        return VideoRepository(local, remote, scheduler, compositeDisposable)
    }

    @Provides
    @Singleton
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Singleton
    @Provides
    fun provideScheduler(): Scheduler = AppScheduler()

    @Singleton
    @Provides
    fun provideDataLocal(
            videosDb: VideosDb,
            scheduler: Scheduler): DataContract.Local {
        return LocalCacheVideos(videosDb, scheduler)
    }

    @Singleton
    @Provides
    fun provideDataRemote(youtubeAPI: YoutubeAPI): DataContract.Remote {

        return RemoteVideos(youtubeAPI)
    }

    @Singleton
    @Provides
    fun provideYoutubeApi(): YoutubeAPI = YoutubeAPI.create()

    @Singleton
    @Provides
    fun provideVidosDb(context: Context): VideosDb{
        return VideosDb.create(context)
    }
}