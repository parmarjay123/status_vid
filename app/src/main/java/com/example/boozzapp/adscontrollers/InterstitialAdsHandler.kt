package com.example.boozzapp.adscontrollers

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.interstitial.InterstitialAd as GoogleInterstitialAd

class InterstitialAdsHandler(
    private val context: Activity,
    private val googleAdId: String,
    private val facebookAdId: String
) {

    private var mGoogleInterstitialAd: GoogleInterstitialAd? = null
   // private var mFacebookInterstitialAd: InterstitialAd? = null

    private var adListener: InterstitialAdListeners? = null

    interface InterstitialAdListeners {
        fun onAdClosed()
        fun onAdDismissed()
        fun onAdLoaded()
        fun onErrorAds()
    }

    fun setAdListener(listener: InterstitialAdListeners) {
        adListener = listener
    }


    fun loadInterstitialAds() {
        loadGoogleInterstitialAd()
    }


    private fun loadGoogleInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        GoogleInterstitialAd.load(
            context,
            googleAdId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.i("TAG", "onAdFailedToLoadInterAd Message: ${adError.message} ")
                    Log.i("TAG", "onAdFailedToLoadInterAd: Code ${adError.code} ")
                   // loadFacebookInterstitialAd()
                    adListener?.onErrorAds()

                }

                override fun onAdLoaded(interstitialAd: com.google.android.gms.ads.interstitial.InterstitialAd) {
                    mGoogleInterstitialAd = interstitialAd
                    adListener?.onAdLoaded()
                    showGoogleInterstitialAd()
                }
            })
    }

    fun showNextAd() {
        if (mGoogleInterstitialAd != null) {
            showGoogleInterstitialAd()
        }  else {
            loadInterstitialAds()
        }
    }

    fun showGoogleInterstitialAd() {
        mGoogleInterstitialAd?.let {
            it.show(context)
            mGoogleInterstitialAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdClicked() {
                        // Called when a click is recorded for an ad.
                        Log.d("TAG", "Ad was clicked.")
                    }

                    override fun onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        Log.d("TAG", "Ad dismissed fullscreen content.")
                        mGoogleInterstitialAd = null
                        adListener?.onAdDismissed()
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: com.google.android.gms.ads.AdError) {
                        // Called when ad fails to show.
                        Log.e("TAG", "Ad failed to show fullscreen content.")
                        mGoogleInterstitialAd = null
                    }

                }

        }
    }

   /* private fun loadFacebookInterstitialAd() {
        mFacebookInterstitialAd = InterstitialAd(context, facebookAdId)
        val interstitialAdListener: InterstitialAdListener = object : InterstitialAdListener {
            // Facebook Interstitial Ad Callbacks
            override fun onError(p0: Ad?, p1: AdError?) {
                if (p1 != null) {
                    adListener?.onError()
                    Log.d("TAG", p1.errorMessage.toString())
                    Log.i("TAG", "onAdFailedToLoadInterFaceBookAd Message: ${p1.errorMessage} ")
                    Log.i("TAG", "onAdFailedToLoadInterFacebookAd: Code ${p1.errorCode} ")
                }

            }

            override fun onAdLoaded(p0: Ad?) {
                adListener?.onAdLoaded()
                mFacebookInterstitialAd!!.show()
            }

            override fun onAdClicked(p0: Ad?) {
            }

            override fun onLoggingImpression(p0: Ad?) {

            }

            override fun onInterstitialDisplayed(p0: Ad?) {
            }

            override fun onInterstitialDismissed(p0: Ad?) {
                mFacebookInterstitialAd = null
                adListener?.onAdDismissed()
            }
        }

        mFacebookInterstitialAd?.loadAd(
            mFacebookInterstitialAd!!.buildLoadAdConfig()
                .withAdListener(interstitialAdListener)
                .build()
        )
    }*/

    fun onDestroy() {
        // Clean up resources if needed
        mGoogleInterstitialAd = null
       // mFacebookInterstitialAd = null
    }
}
