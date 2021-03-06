package com.dew.edward.dewrxjavamvvm

import android.app.Application
import android.support.v4.content.LocalBroadcastManager
import com.dew.edward.dewrxjavamvvm.di.AppComponent
import com.dew.edward.dewrxjavamvvm.di.AppModule
import com.dew.edward.dewrxjavamvvm.di.DaggerAppComponent
import com.dew.edward.dewrxjavamvvm.util.Sync
import com.facebook.stetho.Stetho


/**
 * Created by Edward on 7/19/2018.
 */
class App : Application(){

    lateinit var appComponent: AppComponent
    companion object {
        lateinit var localBroadcastManager: LocalBroadcastManager
    }


    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()

        initSynk()
        initStetho()
        localBroadcastManager = LocalBroadcastManager.getInstance(applicationContext)
    }

    private fun initSynk() {
        Sync.init(this)
    }

    private fun initStetho() {
        if (BuildConfig.DEBUG)
            Stetho.initializeWithDefaults(this)
    }
}