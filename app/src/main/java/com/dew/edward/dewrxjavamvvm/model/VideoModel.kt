package com.dew.edward.dewrxjavamvvm.model

import android.annotation.SuppressLint
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Edward on 7/19/2018.
 */
@Entity(tableName = "video_info", indices = [(Index(value = ["video_id"], unique = true))])
@Parcelize
@SuppressLint("ParcelCreator")
data class VideoModel(var title: String = "",
                      var date: String = "",
                      var thumbnail: String = "",
                      @PrimaryKey
                      @ColumnInfo(name = "video_id")
                      var videoId: String = "",
                      var relatedToVideoId: String = "",
        // indexResponse: to be consistent with changing backend order
                      var indexResponse: Int = -1) : Parcelable
