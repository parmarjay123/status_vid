package com.example.boozzapp.adapter


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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.boozzapp.R
import com.example.boozzapp.activities.PreviewActivity
import com.example.boozzapp.adscontrollers.NativeAdItem
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
                item.loadNativeAd(activity, activity.getString(R.string.gl_native_Videolist))
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
                    nativeAdItem.loadNativeAd(activity, activity.getString(R.string.gl_native_Videolist))
                } else {
                    holder.itemView.ad_view.mediaView = holder.itemView.findViewById<MediaView>(R.id.ad_media)
                    holder.itemView.ad_view.headlineView = holder.itemView.findViewById<View>(R.id.ad_headline)
                    holder.itemView.ad_view.bodyView = holder.itemView.findViewById<View>(R.id.ad_body)
                    holder.itemView.ad_view.callToActionView = holder.itemView.findViewById<View>(R.id.ad_call_to_action)
                    holder.itemView.ad_view.iconView = holder.itemView.findViewById<View>(R.id.ad_icon)
                    holder.itemView.ad_view.priceView = holder.itemView.findViewById<View>(R.id.ad_price)
                    holder.itemView.ad_view.starRatingView = holder.itemView.findViewById<View>(R.id.ad_stars)
                    holder.itemView.ad_view.storeView = holder.itemView.findViewById<View>(R.id.ad_store)
                    holder.itemView.ad_view.advertiserView = holder.itemView.findViewById<View>(R.id.ad_advertiser)
                    populateGoogleNativeAdView(nativeAd, holder.itemView.ad_view)

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

    private fun populateGoogleNativeAdView(
        nativeAd: NativeAd,
        adView: NativeAdView
    ) {
        try {
            (adView.headlineView as TextView?)!!.text = nativeAd.headline
            (adView.callToActionView as Button?)!!.text = nativeAd.callToAction
            if (nativeAd.body != null && adView.bodyView != null) { //            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
                makeExpandable((adView.bodyView as TextView?)!!, nativeAd.body!!)
                (adView.bodyView as TextView).setVisibility(View.GONE)
            }
            val icon = nativeAd.icon
            if (nativeAd.mediaContent != null) {
                adView.mediaView!!.mediaContent = nativeAd.mediaContent
            }
            if (icon == null) {
                adView.iconView!!.visibility = View.GONE
            } else {
                (adView.iconView as ImageView?)!!.setImageDrawable(icon.drawable)
                adView.iconView!!.visibility = View.VISIBLE
            }
            if (nativeAd.price == null) {
                adView.priceView!!.visibility = View.GONE
            } else {
                adView.priceView!!.visibility = View.GONE
                (adView.priceView as TextView?)!!.text = nativeAd.price
            }
            if (nativeAd.store == null) {
                adView.storeView!!.visibility = View.GONE
            } else {
                adView.storeView!!.visibility = View.GONE
                (adView.storeView as TextView?)!!.text = nativeAd.store
            }
            if (nativeAd.starRating == null) {
                adView.starRatingView!!.visibility = View.GONE
            } else {
                (adView.starRatingView as RatingBar?)!!.rating =
                    (nativeAd.starRating.toString() + "").toFloat()
                adView.starRatingView!!.visibility = View.VISIBLE
            }
            if (nativeAd.advertiser == null) {
                adView.advertiserView!!.visibility = View.GONE
            } else {
                (adView.advertiserView as TextView?)!!.text = nativeAd.advertiser
                adView.advertiserView!!.visibility = View.VISIBLE
            }
            // Assign native ad object to the native view.
            adView.setNativeAd(nativeAd)
        } catch (e: Exception) {
        }
    }




    private fun makeExpandable(
        tvPostTextHidden: TextView,
        description: String
    ) {
        tvPostTextHidden.text = description
        tvPostTextHidden.setTextColor(ContextCompat.getColor(activity, R.color.white))
        tvPostTextHidden.post {
            try {
                val strmore = SpannableStringBuilder(" ...")
                strmore.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(activity, R.color.white)),
                    0,
                    strmore.length,
                    0
                )
                strmore.setSpan(
                    StyleSpan(Typeface.BOLD_ITALIC),
                    0,
                    strmore.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                if (tvPostTextHidden.lineCount > 2) {
                    var end = tvPostTextHidden.layout.getLineEnd(1)
                    if (end > 5) {
                        end = end - 4
                    }
                    // Log.e(TAG, "onGlobalLayout: " + end);
                    val builder = SpannableStringBuilder()
                    val str2 = SpannableString(
                        tvPostTextHidden.text.toString().substring(
                            0,
                            end
                        )
                    )
                    builder.append(str2)
                    builder.append(strmore)
                    tvPostTextHidden.setText(builder, TextView.BufferType.SPANNABLE)
                } else if (tvPostTextHidden.lineCount < 2) {
                    tvPostTextHidden.text = description
                    tvPostTextHidden.text = description
                } else {
                    tvPostTextHidden.text = description
                    tvPostTextHidden.text = description
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }
}
