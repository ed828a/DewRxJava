package com.dew.edward.dewrxjavamvvm.adapter

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dew.edward.dewrxjavamvvm.App.Companion.localBroadcastManager
import com.dew.edward.dewrxjavamvvm.R
import com.dew.edward.dewrxjavamvvm.model.VideoModel
import com.dew.edward.dewrxjavamvvm.ui.main.MainViewModel
import com.dew.edward.dewrxjavamvvm.util.SCROLL_TO_END
import com.dew.edward.dewrxjavamvvm.util.VISIBLE_THRESHOLD
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cell_video.view.*


/**
 * Created by Edward on 7/21/2018.
 */
class MainVideoAdapter(
        val viewModel: MainViewModel,
        val function: (VideoModel) -> Unit
) : RecyclerView.Adapter<MainVideoAdapter.MainViewHolder>() {

    private var videoList = arrayListOf<VideoModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.cell_video, parent, false)

        return MainViewHolder(view)
    }

    override fun getItemCount(): Int = videoList.count()

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(videoList[position])
        if (position == itemCount - VISIBLE_THRESHOLD) {
            viewModel.listScrolled()
//            localBroadcastManager.sendBroadcast(Intent(SCROLL_TO_END))
            Log.d("MainVideoAdapter", "onBindViewHolder, remote itemCount = $itemCount")
        }
    }

    fun setVideoList(videos: List<VideoModel>) {
        if (videos.isNotEmpty()) {
            videoList.addAll(videos)
            notifyDataSetChanged()
        }
    }

    fun resetVideoList(){
        videoList.clear()
        notifyDataSetChanged()
    }

    inner class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleView = itemView.textViewTitle
        private val thumbnailView = itemView.imageViewThumb
        private val dateView = itemView.textViewDate

        fun bind(video: VideoModel) {
            titleView.text = video.title
            dateView.text = video.date
            Picasso.with(itemView.context).load(video.thumbnail).into(thumbnailView)

            itemView.setOnClickListener {
                function(video)
            }
        }
    }
}