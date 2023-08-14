package com.example.boozzapp.adscontrollers

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd

class NativeAdItem {
    var nativeAd: NativeAd? = null

    fun loadNativeAd(context: Context, adUnitId: String) {
        val builder = AdLoader.Builder(context, adUnitId)
            .forNativeAd { ad: NativeAd ->
                nativeAd = ad
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    print("AdError${adError}")
                }
            })
        val adLoader = builder.build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
}