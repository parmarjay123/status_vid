package com.example.boozzapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.boozzapp.R
import com.example.boozzapp.activities.EditVideoActivity
import com.example.boozzapp.pojo.ImageCommands
import kotlinx.android.synthetic.main.row_images.view.*


class TemplateImageAdapter(
    val activity: AppCompatActivity,
    var data: ArrayList<ImageCommands>,
    val listener: EditVideoActivity.EditVideoActivityListener
) : RecyclerView.Adapter<TemplateImageAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(activity).inflate(
                R.layout.row_images,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pojo = data[position]

        if (pojo.imgName != "text") {
            holder.itemView.iv_video_image.visibility = View.VISIBLE
            holder.itemView.iv_plus.visibility = View.VISIBLE
            holder.itemView.ivPick.visibility = View.GONE
            holder.itemView.tvPick.visibility = View.GONE
            holder.itemView.cv_parent_layout.setCardBackgroundColor(Color.parseColor("#EEEEEE"))
            if (pojo.imgPath == null) {
                Glide.with(activity)
                    .load(pojo.imgPath)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .transform(RoundedCorners(10))
                    .into(holder.itemView.iv_video_image)
                holder.itemView.iv_plus.visibility = View.VISIBLE
            } else {
                Glide.with(activity)
                    .load(pojo.imgPath)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .transform(RoundedCorners(10))
                    .into(holder.itemView.iv_video_image)
                holder.itemView.iv_plus.visibility = View.VISIBLE
            }

            holder.itemView.cv_parent_layout.setOnClickListener {
                listener.onImageChange(pojo)
            }
        } else {
            holder.itemView.iv_video_image.visibility = View.GONE
            holder.itemView.iv_plus.visibility = View.GONE
            holder.itemView.ivPick.visibility = View.VISIBLE
            holder.itemView.tvPick.visibility = View.VISIBLE
            holder.itemView.cv_parent_layout.setCardBackgroundColor(
                ContextCompat.getColor(
                    activity,
                    R.color.black
                )
            )


        }
    }

    override fun getItemCount(): Int {
        return data.size
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}
