package com.example.boozzapp.adapter

import NativeAdItem
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.boozzapp.R
import com.example.boozzapp.activities.MyVideoPlayActivity
import com.google.android.gms.ads.nativead.MediaView
import kotlinx.android.synthetic.main.raw_video_native.view.*
import kotlinx.android.synthetic.main.row_my_video.view.*
import java.io.File


class MyVideoAdapter(
    val activity: AppCompatActivity,
    private val itemsWithAds: MutableList<Any?> = mutableListOf(),
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_NATIVEAD = 1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {

        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                ViewHolder(
                    LayoutInflater.from(activity).inflate(
                        R.layout.row_my_video,
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_NATIVEAD -> {
                NativeAdsHolder(
                    LayoutInflater.from(activity).inflate(
                        R.layout.raw_video_native,
                        parent,
                        false
                    )
                )

            }
            else -> {
                NativeAdsHolder(
                    LayoutInflater.from(activity).inflate(
                        R.layout.raw_video_native,
                        parent,
                        false
                    )
                )
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is ViewHolder -> {
                val pojo = itemsWithAds[position] as File
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(pojo.absolutePath)
                val thumbnail = retriever.frameAtTime
                holder.itemView.ivMyVideoItem.setImageBitmap(thumbnail)
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
            is NativeAdsHolder -> {
                val nativeAdItem = itemsWithAds[position] as NativeAdItem
                val nativeAd = nativeAdItem.nativeAd
                if (nativeAd == null) {
                    nativeAdItem.loadAds(
                        activity,
                        activity.getString(R.string.GL_Setting_MyVideoList_Native),
                    )

                } else {
                    holder.itemView.ad_view.mediaView =
                        holder.itemView.findViewById<MediaView>(R.id.ad_media)
                    holder.itemView.ad_view.headlineView =
                        holder.itemView.findViewById<View>(R.id.ad_headline)
                    holder.itemView.ad_view.bodyView =
                        holder.itemView.findViewById<View>(R.id.ad_body)
                    holder.itemView.ad_view.callToActionView =
                        holder.itemView.findViewById<View>(R.id.ad_call_to_action)
                    holder.itemView.ad_view.iconView =
                        holder.itemView.findViewById<View>(R.id.ad_icon)
                    holder.itemView.ad_view.priceView =
                        holder.itemView.findViewById<View>(R.id.ad_price)
                    holder.itemView.ad_view.starRatingView =
                        holder.itemView.findViewById<View>(R.id.ad_stars)
                    holder.itemView.ad_view.storeView =
                        holder.itemView.findViewById<View>(R.id.ad_store)
                    holder.itemView.ad_view.advertiserView =
                        holder.itemView.findViewById<View>(R.id.ad_advertiser)
                    holder.itemView.rladView.isVisible = true
                    holder.itemView.rlNoadView.isVisible = false
                    BaseAdapterNative().populateGoogleNativeAdView(
                        nativeAd,
                        holder.itemView.ad_view,
                        activity
                    )

                }
            }
        }


    }


    override fun getItemCount(): Int {
        return itemsWithAds.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (itemsWithAds[position] is File) VIEW_TYPE_ITEM else VIEW_TYPE_NATIVEAD
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private inner class NativeAdsHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView)

}
