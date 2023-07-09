package com.example.boozzapp.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.example.boozzapp.R
import com.example.boozzapp.pojo.TemplatesItem
import com.example.boozzapp.utils.StoreUserData
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_preview.*
import androidx.core.content.ContextCompat

class PreviewActivity : BaseActivity() {

    lateinit var players: SimpleExoPlayer
    private var isPlaying: Boolean = true
    lateinit var videoPojo: TemplatesItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        activity = this
        storeUserData = StoreUserData(activity)
        videoPojo = intent.getParcelableExtra("videoPojo")!!


        var loader: ProgressBar = progressLoader
        loader.isVisible = true
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

        tvSongName.text = videoPojo.title
        val firstItem: MediaItem =
            MediaItem.fromUri(Uri.parse(videoPojo.videoUrl))
        player.player!!.setMediaItem(firstItem)
        player.player!!.prepare()
        player.player!!.play()
        isPlaying = true




        previewBack.setOnClickListener {
            players.release()
            finish()
        }



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
                    .putExtra("videoPojo", videoPojo)
            )
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



