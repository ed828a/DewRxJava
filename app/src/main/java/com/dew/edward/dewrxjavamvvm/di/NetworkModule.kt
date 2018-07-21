package com.dew.edward.dewrxjavamvvm.di

import dagger.Module


/**
 * Created by Edward on 7/19/2018.
 */
@Module
class NetworkModule {



//    @Provides
//    @Singleton
//    fun providesPicasso(context: Context, okHttp3Downloader: OkHttp3Downloader): Picasso {
//        return Picasso.Builder(context)
//                .downloader(okHttp3Downloader)
//                .build()
//    }

//    @Provides
//    @Singleton
//    fun providesOkhttp3Downloader(okHttpClient: OkHttpClient): OkHttp3Downloader {
//        return OkHttp3Downloader(okHttpClient)
//    }
//
//    @Provides
//    @Singleton
//    fun providesOkHttpClient(cache: Cache): OkHttpClient {
//        val client = OkHttpClient.Builder()
//                .cache(cache)
//                .connectTimeout(10, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(10, TimeUnit.SECONDS)
//
//        if (BuildConfig.DEBUG)
//            client.addNetworkInterceptor(StethoInterceptor())
//
//        return client.build()
//    }
//
//    @Provides
//    @Singleton
//    fun providesOkhttpCache(context: Context): Cache {
//        val cacheSize = 3 * 1024 * 1024 // 3 MB
//        return Cache(context.cacheDir, cacheSize.toLong())
//    }




}