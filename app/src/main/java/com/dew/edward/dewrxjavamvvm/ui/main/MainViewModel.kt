package com.dew.edward.dewrxjavamvvm.ui.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
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
class MainViewModel(private val repo: DataContract.Repository,
                    private val compositeDisposable: CompositeDisposable) : ViewModel() {
    private val TAG = javaClass.simpleName

    private var lastQueryData: QueryData? = null

    val videosOutcome: LiveData<Outcome<List<VideoModel>>> by lazy {
        repo.videoFetchOutcome.toLiveData(compositeDisposable)
    }

    fun getVideos(queryData: QueryData) {
        Log.d(TAG, "getVideos: queryData = $queryData")
        lastQueryData = QueryData(queryData.queryString, queryData.type, queryData.isInitializer)
        Log.d(TAG, "getVideos: lastQueryData = $lastQueryData")
        repo.fetchVideos(queryData)
    }

    fun refreshVideos() {
        repo.refreshVideos()
    }

    fun listScrolled() {
        Log.d(TAG, "listScrolled: out side of if, lastQueryData = $lastQueryData")
        if (lastQueryData != null) {
            lastQueryData?.isInitializer = false
            Log.d(TAG, "listScrolled: lastQueryData = $lastQueryData")
            repo.fetchVideos(lastQueryData!!)
        }
    }

    override fun onCleared() {
        compositeDisposable.clear()

        super.onCleared()
    }
}