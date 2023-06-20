package com.example.boozzapp.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.example.boozzapp.R
import com.example.boozzapp.R.id
import com.example.boozzapp.utils.StoreUserData
import kotlinx.android.synthetic.main.activity_preview.*


class PreviewActivity : BaseActivity() {


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        activity = this
        storeUserData = StoreUserData(activity)
        var loader: ProgressBar = findViewById(id.progressLoader)
        loader.isVisible = true


        previewBack.setOnClickListener { finish() }

        if (intent.getStringExtra("songName") != null || intent.getStringExtra("songName") != "") {
            tvSongName.text = intent.getStringExtra("songName")

        }
        if (intent.getStringExtra("videoURL") != null && intent.getStringExtra("videoURL") != "") {
            var videoUrl =
                intent.getStringExtra("videoURL") // Retrieve the video URL from intent extras or any other data source
            playVideo(videoUrl)

        }

    }

    private fun playVideo(videoUrl: String?) {
        videoUrl?.let {
            player.setVideoPath(videoUrl)
            player.setOnPreparedListener { mediaPlayer ->
              progressLoader.isVisible=false
                mediaPlayer.start()
            }
        }
    }
}

