package com.example.boozzapp.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.example.boozzapp.R
import com.example.boozzapp.adapter.ExploreVideoAdapter
import com.example.boozzapp.adapter.SuggestedVideoAdapter
import com.example.boozzapp.pojo.ExploreTemplatesItem
import com.example.boozzapp.pojo.ExploreVideoPojo
import com.example.boozzapp.utils.Constants
import com.example.boozzapp.utils.RetrofitHelper
import com.example.boozzapp.utils.StoreUserData
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.Util
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_download_template.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class DownloadTemplateActivity : BaseActivity() {
    var downloadVideoSuggestionList = ArrayList<ExploreTemplatesItem?>()
    lateinit var players: SimpleExoPlayer
    private var isPlaying: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_template)
        activity = this
        storeUserData = StoreUserData(activity)
        exoDownloadPlayerView.setBackgroundColor(Color.BLACK)

        downloadTempBack.setOnClickListener { finish() }

        tvHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }


        ivDownloadWtsApp.setOnClickListener {
            val videoUri: Uri? = intent.getParcelableExtra("uri")
            if (videoUri!=null){
                val savedVideoUri = videoUri?.let { it1 -> saveVideoToLocalStorage(it1) }
                if (savedVideoUri != null) {
                    shareVideoToWhatsApp(savedVideoUri)


                 /*   if (isWhatsAppInstalled()) {

                    } else {
                        Toast.makeText(
                            activity,
                            "Whats App is not Installed, Please Install it first.",
                            Toast.LENGTH_LONG
                        ).show()
                    }*/

                }
            }else{
                Toast.makeText(
                    activity,
                    "Something Went Wrong...",
                    Toast.LENGTH_LONG
                ).show()
            }


        }

        ivDownloadInsta.setOnClickListener {
            val videoUri: Uri? = intent.getParcelableExtra("uri")
            if (videoUri!=null){
                val savedVideoUri = videoUri?.let { it1 -> saveVideoToLocalStorage(it1) }
                if (savedVideoUri != null) {
                    shareToInstagramStories(savedVideoUri)


                    /*   if (isWhatsAppInstalled()) {

                       } else {
                           Toast.makeText(
                               activity,
                               "Whats App is not Installed, Please Install it first.",
                               Toast.LENGTH_LONG
                           ).show()
                       }*/

                }
            }else{
                Toast.makeText(
                    activity,
                    "Something Went Wrong...",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        ivDownloadFB.setOnClickListener {
            val videoUri: Uri? = intent.getParcelableExtra("uri")
            if (videoUri!=null){
                val savedVideoUri = videoUri?.let { it1 -> saveVideoToLocalStorage(it1) }
                if (savedVideoUri != null) {
                    shareVideoToFacebook(savedVideoUri)

                }
            }else{
                Toast.makeText(
                    activity,
                    "Something Went Wrong...",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        ivDownloadShare.setOnClickListener {
            val videoUri: Uri? = intent.getParcelableExtra("uri")
            val savedVideoUri = videoUri?.let { it1 -> saveVideoToLocalStorage(it1) }

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "video/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, savedVideoUri)
            startActivity(Intent.createChooser(shareIntent, "Share Video"))
        }

        val loader: ProgressBar = progressLoaders
        loader.isVisible = true
        players = SimpleExoPlayer.Builder(activity).build()
        exoDownloadPlayerView.player = players
        exoDownloadPlayerView.useController = false
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
                    Player.STATE_BUFFERING -> {

                    }
                    Player.STATE_IDLE -> {

                    }
                }
            }
        })


        val intent = intent

        // Get the URI from the Intent's extra
        val uri: Uri? = intent.getParcelableExtra("uri")

        // Check if the URI is not null before using it
        if (uri != null) {
            val firstItem: MediaItem =
                MediaItem.fromUri(uri)
            exoDownloadPlayerView.player!!.setMediaItem(firstItem)
            exoDownloadPlayerView.player!!.prepare()
            exoDownloadPlayerView.player!!.play()
        }


        llMain.setOnClickListener {
            if (isPlaying) {
                players.pause()
                pauseButtons.isVisible = true
                isPlaying = false
            } else {
                players.play()
                pauseButtons.isVisible = false
                isPlaying = true
            }
        }


        downloadSuggestionList()

    }


    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT > 23) {
            if (exoDownloadPlayerView != null) {
                exoDownloadPlayerView.player!!.pause()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT > 23) {
            if (exoDownloadPlayerView != null) {
                exoDownloadPlayerView.onResume()
            }

        }
    }

    private fun saveVideoToLocalStorage(videoUri: Uri): Uri? {
        val filename = "my_video.mp4"
        val folder = File(getExternalFilesDir(Environment.DIRECTORY_MOVIES), "MyAppFolder")
        folder.mkdirs()

        val file = File(folder, filename)
        try {
            val inputStream = contentResolver.openInputStream(videoUri)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.flush()
            outputStream.close()
            return FileProvider.getUriForFile(this, "$packageName.provider", file)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun shareVideoToWhatsApp(videoUri: Uri) {
        val intent = Intent("android.intent.action.SEND")
        intent.type = "video/*"
        intent.putExtra(Intent.EXTRA_STREAM, videoUri)
        intent.setPackage("com.whatsapp")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(intent, "Share Video via WhatsApp"))

    }

    private fun shareToInstagramStories(videoUri: Uri) {
        val intent = Intent("com.instagram.share.ADD_TO_STORY")
        intent.setDataAndType(videoUri, "video/*")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(intent)
    }

    private fun shareToStories(videoUri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "video/*"
        intent.putExtra(Intent.EXTRA_STREAM, videoUri)

        val chooserIntent = Intent.createChooser(intent, "Share video to Stories")
        val activities = packageManager.queryIntentActivities(chooserIntent, 0)
        val targetShareIntents = ArrayList<Intent>()

        for (ri in activities) {
            val packageName = ri.activityInfo.packageName
            if (packageName.contains("com.facebook.katana")) {
                val targetIntent = Intent(Intent.ACTION_SEND)
                targetIntent.type = "video/*"
                targetIntent.putExtra(Intent.EXTRA_STREAM, videoUri)
                targetIntent.setPackage(packageName)
                targetShareIntents.add(targetIntent)
            }
        }

        if (targetShareIntents.isNotEmpty()) {
            val chooser = Intent.createChooser(targetShareIntents.removeAt(0), "Share video to Stories")
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toTypedArray())
            startActivity(chooser)
        }
    }

    private fun shareVideoToFacebook(videoUri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "video/*"
        intent.putExtra(Intent.EXTRA_STREAM, videoUri)
        intent.setPackage("com.facebook.katana") // Use the Facebook app's package name
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Facebook app is not installed, handle accordingly
            Toast.makeText(this, "Facebook app is not installed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isWhatsAppInstalled(): Boolean {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("smsto:")
        intent.setPackage("com.whatsapp")
        return packageManager.resolveActivity(intent, 0) != null
    }

    private fun downloadSuggestionList() {
        showProgress()
        val retrofitHelper = RetrofitHelper(activity)
        val call: Call<ResponseBody> =
            retrofitHelper.api().exploreVideoSuggestions(
                storeUserData.getString(Constants.USER_TOKEN),
            )

        retrofitHelper.callApi(activity, call, object : RetrofitHelper.ConnectionCallBack {
            override fun onSuccess(body: Response<ResponseBody>) {
                dismissProgress()
                val responseString = body.body()!!.string()
                Log.i("TAG", "exploreSuggestionList$responseString")
                val suggestionsPojo = Gson().fromJson(responseString, ExploreVideoPojo::class.java)
                suggestionsPojo.data!!.templates?.let { downloadVideoSuggestionList.addAll(it) }
                val exploreVideoListAdapter = SuggestedVideoAdapter(
                    activity,
                    downloadVideoSuggestionList,
                )

                rvDownloadVideo.adapter = exploreVideoListAdapter


            }

            override fun onError(code: Int, error: String) {
                dismissProgress()
                Log.i("Error", error)


            }


        })
    }
}