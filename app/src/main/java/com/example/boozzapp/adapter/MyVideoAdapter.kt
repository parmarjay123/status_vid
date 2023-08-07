package com.example.boozzapp.adapter

import android.content.Intent
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.boozzapp.R
import com.example.boozzapp.activities.MyVideoPlayActivity
import kotlinx.android.synthetic.main.row_explore_quote_list.view.*
import java.io.File


class MyVideoAdapter(
    val activity: AppCompatActivity,
    var data: ArrayList<File>,

    ) : RecyclerView.Adapter<MyVideoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(activity).inflate(
                R.layout.row_explore_quote_list,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pojo = data[position]


        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(pojo.absolutePath)
        val thumbnail = retriever.frameAtTime
        holder.itemView.ivExploreItem.setImageBitmap(thumbnail)
        retriever.release()

        holder.itemView.setOnClickListener {
            activity.startActivity(
                Intent(
                    activity,
                    MyVideoPlayActivity::class.java
                ).putExtra("videoPath", pojo.absolutePath)
            )
        }


    }


    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}
