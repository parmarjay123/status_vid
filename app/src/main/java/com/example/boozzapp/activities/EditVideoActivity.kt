package com.example.boozzapp.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.isVisible
import com.example.boozzapp.R
import com.example.boozzapp.pojo.TemplatesItem
import com.example.boozzapp.utils.StoreUserData
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import kotlinx.android.synthetic.main.activity_edit_video.*
import kotlinx.android.synthetic.main.activity_preview.*
import kotlinx.android.synthetic.main.dialog_watermark.*

class EditVideoActivity : BaseActivity() {
    lateinit var videoPojo: TemplatesItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_video)
        activity = this
        storeUserData = StoreUserData(activity)
        videoPojo = intent.getParcelableExtra("videoPojo")!!


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


    //endregion
    //region Player zone...

}