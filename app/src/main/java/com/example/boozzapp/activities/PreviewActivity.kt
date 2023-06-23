package com.example.boozzapp.activities

import android.annotation.SuppressLint
import android.content.Intent
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
        var songName=""
        var videoUrl=""


        previewBack.setOnClickListener { finish() }

        previewEdit.setOnClickListener {
            activity.startActivity(
                Intent(activity, EditVideoActivity::class.java).putExtra("songName", songName).putExtra("videoURL", videoUrl))
        }

        if (intent.getStringExtra("songName") != null || intent.getStringExtra("songName") != "") {
            songName=intent.getStringExtra("songName").toString()
            tvSongName.text = songName

        }
        if (intent.getStringExtra("videoURL") != null && intent.getStringExtra("videoURL") != "") {
             videoUrl =
                intent.getStringExtra("videoURL").toString() // Retrieve the video URL from intent extras or any other data source
            playVideo(videoUrl)

        }


        player.setOnClickListener {
            if (player.isPlaying) {
                player.pause()
                pauseBtn.isVisible = true
            } else {
                player.start()
                pauseBtn.isVisible = false
            }
        }

    }

    @SuppressLint("SuspiciousIndentation")
    private fun playVideo(videoUrl: String?) {
        videoUrl?.let {
            player.setVideoPath(videoUrl)
            player.setOnPreparedListener { mediaPlayer ->
                progressLoader.isVisible = false
                mediaPlayer.start()
            }
        }
    }
}

