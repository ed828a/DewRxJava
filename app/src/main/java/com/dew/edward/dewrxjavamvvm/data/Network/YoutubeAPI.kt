package com.dew.edward.dewrxjavamvvm.data.Network

import android.util.Log
import com.dew.edward.dewrxjavamvvm.model.YoutubeResponse
import com.dew.edward.dewrxjavamvvm.util.API_KEY
import com.dew.edward.dewrxjavamvvm.util.BASE_URL
import com.dew.edward.dewrxjavamvvm.util.NETWORK_PAGE_SIZE
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Single
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url


/**
 * Created by Edward on 7/19/2018.
 */
interface YoutubeAPI {
    @GET("search")
    fun searchVideo(@Query("q") query: String = "",
                    @Query("pageToken") pageToken: String ="",
                    @Query("part") part: String = "snippet",
                    @Query("maxResults") maxResults: String = "$NETWORK_PAGE_SIZE",
                    @Query("type") type: String = "video",
                    @Query("key") key: String = API_KEY): Single<YoutubeResponse>

    @GET("search")
    fun getRelatedVideos(@Query("relatedToVideoId") relatedToVideoId: String = "",
                         @Query("pageToken") pageToken: String = "",
                         @Query("part") part: String = "snippet",
                         @Query("maxResults") maxResults: String = "$NETWORK_PAGE_SIZE",
                         @Query("type") type: String = "video",
                         @Query("key") key: String = API_KEY): Single<YoutubeResponse>


    @Streaming
    @GET
    fun downloadByUrlStream(@Url url: String): Call<ResponseBody>

    companion object {

        fun create(): YoutubeAPI = createYoutubeApi(HttpUrl.parse(BASE_URL)!!)
        private fun createYoutubeApi(httpUrl: HttpUrl): YoutubeAPI {
            val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                Log.d("YoutubeAPI", it)
            })
            logger.level = HttpLoggingInterceptor.Level.BASIC

            val okHttpClient = OkHttpClient.Builder().addInterceptor(logger).build()

            return Retrofit.Builder()
                    .baseUrl(httpUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
                    .create(YoutubeAPI::class.java)
        }
    }
}