package com.example.boozzapp.activities

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.VideoView
import com.example.boozzapp.R
import com.example.boozzapp.utils.StoreUserData
import kotlinx.android.synthetic.main.activity_preview.*


class PreviewActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        activity = this
        storeUserData = StoreUserData(activity)

        previewBack.setOnClickListener { finish() }

        if(intent.getStringExtra("songName")!=null||intent.getStringExtra("songName")!=""){
            tvSongName.text=intent.getStringExtra("songName")

        }

        var videoUrl =
            intent.getStringExtra("videoURL") // Retrieve the video URL from intent extras or any other data source

        playVideo(videoUrl)
    }

    private fun playVideo(videoUrl: String?) {
        videoUrl?.let {
            player.setVideoPath(videoUrl)
            player.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.start()
            }
        }
    }
}

