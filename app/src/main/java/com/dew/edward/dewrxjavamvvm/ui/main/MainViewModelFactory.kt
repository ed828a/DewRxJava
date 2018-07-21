package com.dew.edward.dewrxjavamvvm.ui.main

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.dew.edward.dewrxjavamvvm.data.DataContract
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject


/**
 * Created by Edward on 7/21/2018.
 */
class MainViewModelFactory @Inject constructor(
        private val repository: DataContract.Repository,
        private val compositeDisposable: CompositeDisposable
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java))
            return MainViewModel(repository, compositeDisposable) as T
        else
            throw IllegalArgumentException("Unknown ViewModel Class")
    }
}