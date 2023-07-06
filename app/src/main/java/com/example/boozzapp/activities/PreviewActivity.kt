package com.example.boozzapp.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.example.boozzapp.R
import com.example.boozzapp.utils.StoreUserData
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_preview.*

class PreviewActivity : BaseActivity() {

    lateinit var players: SimpleExoPlayer
    private var isPlaying: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        activity = this
        storeUserData = StoreUserData(activity)


        previewBack.setOnClickListener {
            players.release()
            finish()
        }

        var loader: ProgressBar = progressLoader
        loader.isVisible = true
        var songName = ""
        var videoUrl = ""
        players = SimpleExoPlayer.Builder(activity).build()
        player.player = players
        player.useController = false
        players.addListener(object : Player.EventListener {
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                when (state) {
                    ExoPlayer.STATE_READY -> {
                        loader.isVisible = false
                    }
                    ExoPlayer.STATE_ENDED -> {
                        players.seekTo(0)
                    }
                }
            }
        })

        mainView.setOnClickListener {
            if (isPlaying) {
                players.pause()
                pauseBtn.isVisible = true
                isPlaying = false
            } else {
                players.play()
                pauseBtn.isVisible = false
                isPlaying = true
            }
        }

        previewEdit.setOnClickListener {
            activity.startActivity(
                Intent(activity, EditVideoActivity::class.java)
                    .putExtra("songName", songName)
                    .putExtra("videoURL", videoUrl)
            )
        }

        if (intent.getStringExtra("songName") != null || intent.getStringExtra("songName") != "") {
            songName = intent.getStringExtra("songName").toString()
            tvSongName.text = songName
        }

        if (intent.getStringExtra("videoURL") != null && intent.getStringExtra("videoURL") != "") {
            val firstItem: MediaItem =
                MediaItem.fromUri(Uri.parse(intent.getStringExtra("videoURL")))
            player.player!!.setMediaItem(firstItem)
            player.player!!.prepare()
            player.player!!.play()
            isPlaying = true
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT > 23) {
            if (player != null) {
                player.player!!.pause()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT > 23) {
            if (player != null) {
                player.onResume()
            }

        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        players.release() // Release the player's resources
    }
}



