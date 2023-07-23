package com.example.boozzapp.activities

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.example.boozzapp.R
import com.example.boozzapp.pojo.ExploreTemplatesItem
import com.example.boozzapp.utils.PartyZipFileManager
import com.example.boozzapp.utils.StoreUserData
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_preview.*
import java.io.File

class PreviewActivity : BaseActivity() {
    lateinit var players: SimpleExoPlayer
    private var isPlaying: Boolean = true
    private lateinit var videoPojo: ExploreTemplatesItem
    private var zipFilePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        activity = this
        storeUserData = StoreUserData(activity)
        videoPojo = intent.getParcelableExtra("videoPojo")!!


        frameLoader.isVisible = true
        players = SimpleExoPlayer.Builder(activity).build()
        player.player = players
        player.useController = false
        players.addListener(object : Player.EventListener {
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                when (state) {
                    ExoPlayer.STATE_READY -> {
                        players.pause()
                    }

                    ExoPlayer.STATE_ENDED -> {
                        players.seekTo(0)
                    }
                    Player.STATE_BUFFERING -> {

                    }
                    Player.STATE_IDLE -> {

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



        previewLoader.setProgressVector(resources.getDrawable(R.drawable.black_three_dot_circle))
        previewLoader.setTextViewVisibility(true)
        previewLoader.setTextStyle(true)
        previewLoader.setTextColor(Color.WHITE)
        previewLoader.setBackgroundColor(Color.BLACK)
        previewLoader.setTextSize(12F)
        previewLoader.setTextMsg("Downloading Video Please Wait...")
        previewLoader.setEnlarge(5)


        videoPojo.let {
            frameLoader.isVisible = true
            downloadCacheTemplateZip(it.zipUrl!!, it.zip!!)
        }


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

    override fun onDestroy() {
        super.onDestroy()
        PRDownloader.cancelAll()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        players.release() // Release the player's resources
    }


    private fun downloadCacheTemplateZip(zipUrl: String, fileName: String) {
        zipFilePath = getZipDirectoryPath() + fileName
        Log.i("TAG", "onDownloadComplete:  before" + getZipDirectoryPath()!!)

        PRDownloader.download(zipUrl, getZipDirectoryPath(), fileName)
            .build()
            .setOnProgressListener {
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    Log.i("TAG", "onDownloadComplete:  after" + getZipDirectoryPath()!!)
                    val unzipTask = UnZipFileFromURLs(this@PreviewActivity::getZipDirectoryPath)
                    unzipTask.execute(zipFilePath)
                    frameLoader.isVisible = false
                    llBottomMenu.isVisible = true
                    players.play()


                }

                override fun onError(error: Error) {
                    frameLoader.isVisible = false
                    players.play()

                    Log.i(
                        "TAG",
                        "onDownloadComplete: " + "Download failed" + error.serverErrorMessage
                    )

                }
            })
    }


    private class UnZipFileFromURLs(
        private val getZipDirectoryPath: () -> String?
    ) : AsyncTask<String?, String?, String?>() {
        // Rest of the code...
        override fun doInBackground(vararg p0: String?): String? {
            try {
                PartyZipFileManager.unzip(p0[0], getZipDirectoryPath())
                // Rest of the code...
            } catch (e: Exception) {
                e.printStackTrace()
                return "Error"
            }
            return null
        }
    }


    fun getZipDirectoryPath(): String? {
        val externalDirectory = activity.filesDir.absolutePath
        //        String externalDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        val dir = File(
            externalDirectory + File.separator +
                    activity.resources.getString(R.string.zip_directory)
        )
        if (!dir.exists()) dir.mkdirs()
        return dir.absolutePath + File.separator
    }
}



