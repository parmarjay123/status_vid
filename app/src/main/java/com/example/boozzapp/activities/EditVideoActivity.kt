package com.example.boozzapp.activities

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.example.boozzapp.R
import com.example.boozzapp.pojo.TemplatesItem
import com.example.boozzapp.utils.StoreUserData
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import kotlinx.android.synthetic.main.activity_edit_video.*
import kotlinx.android.synthetic.main.dialog_watermark.*
import kotlinx.android.synthetic.main.layout_preview_controls.view.*

class EditVideoActivity : BaseActivity() {
    lateinit var videoPojo: TemplatesItem
    private var player: SimpleExoPlayer? = null
    private var pause = false
    private var mediaSource: MediaSource? = null
    private var dataSourceFactory: DataSource.Factory? = null
    private val outputVideo = ""
    private var pause_dur: Long = 0
    private val flagExporting = false
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_video)
        activity = this
        storeUserData = StoreUserData(activity)
        videoPojo = intent.getParcelableExtra("videoPojo")!!


//        loadingAnimationUtils = new LoadingAnimationUtils(this);
        dataSourceFactory = buildDataSourceFactory()
        mediaPlayer = MediaPlayer()

        editBack.setOnClickListener { finish() }
        tvEditSongName.text = videoPojo.title


        ivCloseWatermark.setOnClickListener {
            showDialog()
        }
    }

    fun showDialog() {
        val holdDialog = Dialog(activity)
        holdDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        holdDialog.setContentView(R.layout.dialog_watermark)

        // Set the background of the dialog window to transparent
        holdDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Calculate the desired height of the dialog (e.g., half of the screen)
        val windowHeight = activity.window.decorView.height
        val dialogHeight = windowHeight / 2

        // Set the dialog's window layout parameters
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight)
        holdDialog.window?.setLayout(layoutParams.width, layoutParams.height)

        holdDialog.show()

        holdDialog.llRemoveWaterMark.setOnClickListener {
            holdDialog.dismiss()
        }
    }

    private fun buildDataSourceFactory(): DataSource.Factory? {
        return EditVideoActivity().buildDataSourceFactory()
    }

    //endregion
    private fun initializeExoPlayer() {
        if (player == null) {
            player = SimpleExoPlayer.Builder(this).build()
            exoPlayerView.player = player
            exoPlayerView.setBackgroundColor(Color.BLACK)
            exoPlayerView.useController = false
            player!!.addListener(object : Player.EventListener {
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        progressBar_exoplayer.visibility = View.GONE
                        //                        loadingAnimationUtils.dismiss();
                        pause_dur = 0
                        playPausePlayer(true)
                    } else if (playbackState == Player.STATE_BUFFERING) {
                        progressBar_exoplayer.visibility = View.VISIBLE
                        //                        loadingAnimationUtils.show();
                    } else if (playbackState == Player.STATE_READY) {
                        progressBar_exoplayer.visibility = View.GONE
                        //                        loadingAnimationUtils.dismiss();
                    } else if (playbackState == Player.STATE_IDLE) {
                        progressBar_exoplayer.visibility = View.GONE
                        //                        loadingAnimationUtils.dismiss();
                    }
                }
            })
            if (pause) playPausePlayer(
                false
            ) else playPausePlayer(true)
        }
        prepareExoPlayer()
    }

    private fun prepareExoPlayer() {
        mediaSource = dataSourceFactory?.let {
            ProgressiveMediaSource.Factory(it)
                .createMediaSource(Uri.parse(outputVideo))
        }
        player?.prepare(mediaSource as ProgressiveMediaSource, true, false)
    }

    private fun playPausePlayer(play: Boolean) {
        if (player != null) if (play) {
            exo_thumb.visibility = View.GONE
            try {
                player!!.seekTo(pause_dur)
            } catch (e: Exception) {
            }
            player!!.playWhenReady = true
            player!!.playbackState
            preview_controls.rl_preview_control.visibility = View.GONE
        } else {
            try {
                pause_dur = player!!.currentPosition
            } catch (e: Exception) {
                if (e is IllegalStateException) {
                    // bypass IllegalStateException
                    // You can again call the method and make a counter for deadlock situation or implement your own code according to your situation
                    var checkAgain = true
                    var counter = 0
                    var i = 0
                    while (i < 2) {
                        if (checkAgain) {
                            mediaPlayer?.reset()
                            pause_dur = player!!.currentPosition
                            if (pause_dur > 0) {
                                checkAgain = false
                                counter++
                            }
                        } else {
                            if (counter == 0) {
                                throw e
                            }
                        }
                        i++
                    }
                }
            }
            player!!.playWhenReady = false
            player!!.playbackState
            if (!flagExporting) {
                preview_controls.rl_preview_control.visibility = View.VISIBLE
            } else {
                preview_controls.rl_preview_control.visibility = View.GONE
            }
        }
    }

}