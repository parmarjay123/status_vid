package com.example.boozzapp.activities

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import com.example.boozzapp.R
import com.example.boozzapp.utils.StoreUserData
import kotlinx.android.synthetic.main.activity_my_video_play.*

class MyVideoPlayActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_video_play)
        activity = this
        storeUserData = StoreUserData(activity)

        ivMyVideoPlayBack.setOnClickListener { finish() }


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
}