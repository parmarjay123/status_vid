package com.example.boozzapp.adapter

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.boozzapp.R
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

public class BaseAdapterNative {

    fun populateGoogleNativeAdView(
        nativeAd: NativeAd,
        adView: NativeAdView, context: Context
    ) {
        try {
            (adView.headlineView as TextView?)!!.text = nativeAd.headline
            (adView.callToActionView as Button?)!!.text = nativeAd.callToAction
            if (nativeAd.body != null && adView.bodyView != null) { //            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
                makeExpandable((adView.bodyView as TextView?)!!, nativeAd.body!!, context)
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


    fun makeExpandable(
        tvPostTextHidden: TextView,
        description: String,
        context: Context
    ) {
        tvPostTextHidden.text = description
        tvPostTextHidden.setTextColor(ContextCompat.getColor(context, R.color.white))
        tvPostTextHidden.post {
            try {
                val strmore = SpannableStringBuilder(" ...")
                strmore.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(context, R.color.white)),
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