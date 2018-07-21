package com.dew.edward.dewrxjavamvvm.data.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.dew.edward.dewrxjavamvvm.model.VideoModel
import com.dew.edward.dewrxjavamvvm.util.DB_NAME


/**
 * Created by Edward on 7/20/2018.
 */

@Database(entities = [VideoModel::class], version = 1, exportSchema = false)
abstract class VideosDb: RoomDatabase() {
    abstract fun youtubeDao(): YoutubeDao

    companion object {
        fun create(context: Context): VideosDb =
                Room.databaseBuilder(context, VideosDb::class.java, DB_NAME)
                        .fallbackToDestructiveMigration()  // deleting old cached data
                        .build()
    }
}