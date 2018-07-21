package com.dew.edward.dewrxjavamvvm.data.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.dew.edward.dewrxjavamvvm.model.VideoModel
import io.reactivex.Flowable


/**
 * Created by Edward on 7/19/2018.
 */

@Dao
interface YoutubeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(videos: List<VideoModel>)

    @Query("SELECT * FROM video_info ORDER BY indexResponse ASC")
    fun getAll(): Flowable<List<VideoModel>>

    @Query("DELETE FROM video_info")
    fun deleteAll()
}