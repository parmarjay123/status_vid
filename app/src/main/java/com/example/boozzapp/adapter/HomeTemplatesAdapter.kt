package com.example.boozzapp.adapter


import NativeAdItem
import android.content.Intent
import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.boozzapp.R
import com.example.boozzapp.activities.PreviewActivity
import com.example.boozzapp.pojo.ExploreTemplatesItem
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.android.synthetic.main.raw_video_native.view.*
import kotlinx.android.synthetic.main.row_home_list.view.*


class HomeTemplatesAdapter(
    val activity: AppCompatActivity,
    private val itemsWithAds: MutableList<Any?> = mutableListOf(),
    recyclerView: RecyclerView,
    var nativeAdType: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mOnLoadMoreListener: OnLoadMoreListener? = null
    private var isLoading = false
    private val visibleThreshold = 5
    private var lastVisibleItem = 0
    private var totalItemCount = 0
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1
    private val VIEW_TYPE_NATIVEAD = 2


    init {
        preloadNativeAds()
        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalItemCount = linearLayoutManager!!.itemCount
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener!!.onLoadMore()
                    }
                    isLoading = true
                }
            }
        })

    }


    private fun preloadNativeAds() {
        for (item in itemsWithAds) {
            if (item is NativeAdItem) {
                item.loadAds(
                    activity,
                    nativeAdType,
                )
            }
        }
    }

    interface OnLoadMoreListener {
        fun onLoadMore()
    }

    fun setOnLoadMoreListener(mOnLoadMoreListener: OnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener
    }

    fun setLoaded() {
        isLoading = false
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val view =
                    LayoutInflater.from(activity).inflate(R.layout.row_home_list, parent, false)
                ViewHolder(view)
            }
            VIEW_TYPE_NATIVEAD -> {
                val view =
                    LayoutInflater.from(activity).inflate(R.layout.raw_video_native, parent, false)
                NativeAdsHolder(view)
            }
            else -> {
                val view =
                    LayoutInflater.from(activity)
                        .inflate(R.layout.layout_loading_item, parent, false)
                LoadingViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val data = itemsWithAds[position] as ExploreTemplatesItem
                Glide.with(activity).load(data.thumbnailUrl).into(holder.itemView.ivItem)
                holder.itemView.ivHot.isVisible = data.isHot == true
                holder.itemView.ivNew.isVisible = data.isNew == true
                holder.itemView.ivPremium.isVisible = data.isPremium == 1
                holder.itemView.setOnClickListener {
                    activity.startActivity(
                        Intent(activity, PreviewActivity::class.java)
                            .putExtra("videoPojo", data)
                    )
                }
            }
            is NativeAdsHolder -> {
                val nativeAdItem = itemsWithAds[position] as NativeAdItem
                val nativeAd = nativeAdItem.nativeAd
                if (nativeAd == null) {
                    nativeAdItem.loadAds(
                        activity,
                        nativeAdType,
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
                    BaseAdapterNative().populateGoogleNativeAdView(nativeAd, holder.itemView.ad_view,activity)

                }
            }
            is LoadingViewHolder -> {

            }
        }
    }


    override fun getItemCount(): Int {
        return itemsWithAds.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (itemsWithAds[position] is ExploreTemplatesItem) VIEW_TYPE_ITEM else if (itemsWithAds[position] != null) VIEW_TYPE_NATIVEAD else VIEW_TYPE_LOADING
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private inner class NativeAdsHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

    }

    private inner class LoadingViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {


    }

}
