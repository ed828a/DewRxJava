package com.dew.edward.dewrxjavamvvm.ui.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.dew.edward.dewrxjavamvvm.data.DataContract
import com.dew.edward.dewrxjavamvvm.model.Outcome
import com.dew.edward.dewrxjavamvvm.model.QueryData
import com.dew.edward.dewrxjavamvvm.model.VideoModel
import com.dew.edward.dewrxjavamvvm.util.VISIBLE_THRESHOLD
import com.dew.edward.dewrxjavamvvm.util.toLiveData
import io.reactivex.disposables.CompositeDisposable


/**
 * Created by Edward on 7/20/2018.
 */
class MainViewModel (private val repo: DataContract.Repository,
                     private val compositeDisposable: CompositeDisposable) : ViewModel(){
    var lastQueryData: QueryData? = null

    val videosOutcome: LiveData<Outcome<List<VideoModel>>> by lazy {
        repo.videoFetchOutcome.toLiveData(compositeDisposable)
    }

    fun getVideos(queryData: QueryData) {
        lastQueryData = queryData
        if (videosOutcome.value == null)
            repo.fetchVideos(queryData)
    }

    fun refreshVideos() {
        repo.refreshVideos()
    }

    fun listScrolled(
//            visibleItemCount: Int,
//            lastVisibleItemPosition: Int,
//            totalItemCount: Int
    ){
//        if (visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount){
            if (lastQueryData != null) {
                lastQueryData?.isInitializer = false
                repo.fetchVideos(lastQueryData!!)
            }
//        }
    }

    override fun onCleared() {
        compositeDisposable.clear()

        super.onCleared()
    }
}