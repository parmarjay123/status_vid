package com.example.boozzapp.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.isVisible
import com.example.boozzapp.R
import com.example.boozzapp.utils.StoreUserData
import kotlinx.android.synthetic.main.activity_edit_video.*
import kotlinx.android.synthetic.main.activity_edit_video.pauseBtn
import kotlinx.android.synthetic.main.activity_edit_video.player
import kotlinx.android.synthetic.main.activity_preview.*
import kotlinx.android.synthetic.main.dialog_watermark.*

class EditVideoActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_video)
        activity = this
        storeUserData = StoreUserData(activity)


        editBack.setOnClickListener { finish() }


        if (intent.getStringExtra("songName") != null || intent.getStringExtra("songName") != "") {
            tvEditSongName.text = intent.getStringExtra("songName")

        }
        if (intent.getStringExtra("videoURL") != null && intent.getStringExtra("videoURL") != "") {
            var videoUrl =
                intent.getStringExtra("videoURL") // Retrieve the video URL from intent extras or any other data source
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

    @SuppressLint("SuspiciousIndentation")
    private fun playVideo(videoUrl: String?) {
        videoUrl?.let {
            player.setVideoPath(videoUrl)
            player.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.start()
            }
        }
    }


}