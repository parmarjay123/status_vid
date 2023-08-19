package com.example.boozzapp.activities

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.boozzapp.R
import com.example.boozzapp.adscontrollers.InterstitialAdsHandler
import com.example.boozzapp.utils.StoreUserData
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity() {
    lateinit var interstitialAdsHandler: InterstitialAdsHandler
    var myVideo = false
    var shareApp = false
    var rateApp = false
    var checkUpdate = false
    var privacyPolicy = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        activity = this
        storeUserData = StoreUserData(activity)

        ivSettingBack.setOnClickListener { finish() }
        showInterestitialAds()
        setupAd()

        llMyVideo.setOnClickListener {
            myVideo = true
            interstitialAdsHandler.showNextAd()
        }

        llShareApp.setOnClickListener {
            shareApp = true
            interstitialAdsHandler.showNextAd()
        }

        llRateApp.setOnClickListener {
            rateApp = true
            interstitialAdsHandler.showNextAd()
        }

        llCheckUpdate.setOnClickListener {
            checkUpdate = true
            interstitialAdsHandler.showNextAd()
        }

        llPrivacyPolicy.setOnClickListener {
            privacyPolicy = true
            interstitialAdsHandler.showNextAd()
        }
        ivInstagram.setOnClickListener {
            openInstagramPage(activity)
        }
        ivFacebook.setOnClickListener {
            openFacebookPage(activity)
        }
    }

    override fun onResume() {
        super.onResume()
        setupAd()
    }

    override fun onPause() {
        super.onPause()
        if (::interstitialAdsHandler.isInitialized) {
            interstitialAdsHandler.onDestroy()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        interstitialAdsHandler.onDestroy()

    }

    private fun setupAd() {
        val adRequest = AdRequest.Builder().build()
        adLoadingText.visibility = View.VISIBLE

        settingBannerAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                adLoadingText.isVisible = false
                settingBannerAdView.isVisible = true
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                Log.i("TAG", "onAdFailedToLoad: setting${loadAdError.message} ")
                Log.i("TAG", "onAdFailedToLoad: setting${loadAdError.code} ")

                adLoadingText.isVisible = true
                settingBannerAdView.isVisible = false

            }
        }
        settingBannerAdView.loadAd(adRequest)
    }

    fun showInterestitialAds() {
        interstitialAdsHandler = InterstitialAdsHandler(
            this,
            getString(R.string.GL_Setting_Inter),
            getString(R.string.FB_Setting_Inter)
        )
        interstitialAdsHandler.loadInterstitialAds()
        interstitialAdsHandler.setAdListener(object :
            InterstitialAdsHandler.InterstitialAdListeners {
            override fun onAdClosed() {
                if (myVideo) {
                    activity.startActivity(Intent(activity, MyVideoActivity::class.java))

                } else if (shareApp) {
                    val shareAppIntent = Intent(Intent.ACTION_SEND)
                    shareAppIntent.type = "text/plain"
                    shareAppIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        "Check out this awesome app: https://play.google.com/store/apps/details?id=your.package.name"
                    )

                    val chooserIntent = Intent.createChooser(shareAppIntent, "Share via")
                    if (shareAppIntent.resolveActivity(packageManager) != null) {
                        startActivity(chooserIntent)
                    }
                } else if (rateApp) {
                    openPlayStoreForRating(activity)

                } else if (checkUpdate) {
                    openPlayStoreForRating(activity)

                } else if (privacyPolicy) {
                    goPrivacyPolicy()
                }
                adVariableFalse()


            }


            override fun onAdDismissed() {
                if (myVideo) {
                    activity.startActivity(Intent(activity, MyVideoActivity::class.java))

                } else if (shareApp) {
                    val shareAppIntent = Intent(Intent.ACTION_SEND)
                    shareAppIntent.type = "text/plain"
                    shareAppIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        "Check out this awesome app: https://play.google.com/store/apps/details?id=your.package.name"
                    )
                    val chooserIntent = Intent.createChooser(shareAppIntent, "Share via")
                    if (shareAppIntent.resolveActivity(packageManager) != null) {
                        startActivity(chooserIntent)
                    }
                } else if (rateApp) {
                    openPlayStoreForRating(activity)

                } else if (checkUpdate) {
                    openPlayStoreForRating(activity)

                } else if (privacyPolicy) {
                    goPrivacyPolicy()
                }
                adVariableFalse()
            }
        })

    }

    private fun adVariableFalse() {
        myVideo = false
        shareApp = false
        rateApp = false
        checkUpdate = false
        privacyPolicy = false
    }

    private fun openPlayStoreForRating(context: Context) {
        val packageName = context.packageName
        val playStoreUri = Uri.parse("market://details?id=$packageName")

        val rateIntent = Intent(Intent.ACTION_VIEW, playStoreUri)
        rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            context.startActivity(rateIntent)
        } catch (e: ActivityNotFoundException) {
            val webPlayStoreUri =
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            val webRateIntent = Intent(Intent.ACTION_VIEW, webPlayStoreUri)
            context.startActivity(webRateIntent)
        }

    }

    private fun goPrivacyPolicy() {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(resources.getString(R.string.privacy_policy_url))

            // ActivityInfo activityInfo = intent.resolveActivityInfo(getPackageManager(), intent.getFlags());
            //  if (activityInfo != null && activityInfo.exported) {
            startActivity(intent)
            //  }
        } catch (e: Exception) {
            Toast.makeText(activity, "No Application Found to Open", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openInstagramPage(context: Context) {
        val uri = Uri.parse(getString(R.string.insta_page_url))
        val instagramIntent = Intent(Intent.ACTION_VIEW, uri)

        try {
            context.startActivity(instagramIntent)
        } catch (e: Exception) {
            Toast.makeText(activity, "Fetching Some Error", Toast.LENGTH_LONG).show()

        }
    }

    private fun openFacebookPage(context: Context) {
        val uri = Uri.parse(getString(R.string.fb_page_url))
        val facebookIntent = Intent(Intent.ACTION_VIEW, uri)

        try {
            context.startActivity(facebookIntent)
        } catch (e: Exception) {
            Toast.makeText(activity, "Fetching Some Error", Toast.LENGTH_LONG).show()
        }
    }


}