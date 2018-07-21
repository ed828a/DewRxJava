package com.dew.edward.dewrxjavamvvm.di

import com.dew.edward.dewrxjavamvvm.data.DataContract
import com.dew.edward.dewrxjavamvvm.ui.main.MainViewModelFactory
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable


/**
 * Created by Edward on 7/21/2018.
 */
@Module
class ViewModelModule {

    @Provides
    fun provideMainViewModelFactory(
            repository: DataContract.Repository,
            compositeDisposable: CompositeDisposable): MainViewModelFactory {
        return MainViewModelFactory(repository, compositeDisposable)
    }

}