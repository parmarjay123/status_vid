package com.example.boozzapp.activities

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.example.boozzapp.R
import com.example.boozzapp.adapter.ExploreVideoAdapter
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

class DownloadTemplateActivity : BaseActivity() {
    var downloadVideoSuggestionList = ArrayList<ExploreTemplatesItem?>()
    lateinit var players: SimpleExoPlayer
    private var isPlaying: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_template)
        activity = this
        storeUserData = StoreUserData(activity)

        downloadTempBack.setOnClickListener { finish() }

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
                val exploreVideoListAdapter = ExploreVideoAdapter(
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