package com.example.boozzapp.activities

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.MediaController
import androidx.core.view.isVisible
import com.example.boozzapp.R
import com.example.boozzapp.utils.StoreUserData
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import kotlinx.android.synthetic.main.activity_my_video_play.*

class MyVideoPlayActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_video_play)
        activity = this
        storeUserData = StoreUserData(activity)

        ivMyVideoPlayBack.setOnClickListener { finish() }

        setupAd()

        val videoPath = intent.getStringExtra("videoPath")

        if (videoPath != null) {
            val videoUri = Uri.parse(videoPath)
            videoView.setVideoURI(videoUri)


            val mediaController = MediaController(this)
            videoView.setMediaController(mediaController)
            mediaController.setAnchorView(videoView)

            videoView.start()
        }


    }

    override fun onResume() {
        super.onResume()
        setupAd()
    }

    private fun setupAd() {
        val adRequest = AdRequest.Builder().build()
        myVideoPlayBannerAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                adMyVideoPlayLoadingText.isVisible = false
                myVideoPlayBannerAdView.isVisible = true
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                Log.i("TAG", "onAdFailedToLoad: MyVideoPlay${loadAdError.message} ")
                Log.i("TAG", "onAdFailedToLoad: MyVideoPlay${loadAdError.code} ")

                adMyVideoPlayLoadingText.isVisible = true
                myVideoPlayBannerAdView.isVisible = false

            }
        }
        myVideoPlayBannerAdView.loadAd(adRequest)
    }
}