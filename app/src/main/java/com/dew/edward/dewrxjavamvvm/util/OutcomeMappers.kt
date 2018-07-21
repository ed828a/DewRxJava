package com.dew.edward.dewrxjavamvvm.util

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.dew.edward.dewrxjavamvvm.model.Outcome
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject


/**
 * Created by Edward on 7/19/2018.
 */

/**
 * Extension function to convert a Publish subject into a LiveData by subscribing to it.
 **/
fun <T> PublishSubject<T>.toLiveData(compositeDisposable: CompositeDisposable): LiveData<T> {
    val data = MutableLiveData<T>()
    // using post for running on background thread
    compositeDisposable.add(this.subscribe { t: T -> data.postValue(t) })
    return data
}

/**
 * Extension function to push a failed event with an exception to the observing outcome
 * */
fun <T> PublishSubject<Outcome<T>>.failed(e: Throwable) {
    with(this){
        loading(false)
        onNext(Outcome.failure(e))
    }
}

/**
 * Extension function to push  a success event with data to the observing outcome
 * */
fun <T> PublishSubject<Outcome<T>>.success(t: T) {
    with(this){
        loading(false)
        onNext(Outcome.success(t))
    }
}

/**
 * Extension function to push the loading status to the observing outcome
 * */
fun <T> PublishSubject<Outcome<T>>.loading(isLoading: Boolean) {
    this.onNext(Outcome.loading(isLoading))
}