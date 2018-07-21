package com.dew.edward.dewrxjavamvvm.di

import com.dew.edward.dewrxjavamvvm.ui.main.MainActivity
import dagger.Component
import javax.inject.Singleton


/**
 * Created by Edward on 7/19/2018.
 */

@Singleton
@Component(modules = [AppModule::class, DataModule::class, ViewModelModule::class])
interface AppComponent {

    fun inject(target: MainActivity)
}