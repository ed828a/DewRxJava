package com.dew.edward.dewrxjavamvvm.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.dew.edward.dewrxjavamvvm.App
import com.dew.edward.dewrxjavamvvm.data.repository.AppScheduler
import com.dew.edward.dewrxjavamvvm.data.repository.Scheduler
import dagger.Module
import dagger.Provides

import javax.inject.Singleton


/**
 * Created by Edward on 7/19/2018.
 */

@Module
class AppModule(private val application: App) {

    @Singleton
    @Provides
    fun provideContext(): Context = application.applicationContext

    @Provides
    @Singleton
    fun providesSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

}