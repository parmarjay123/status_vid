package com.example.boozzapp.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.isVisible
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.example.boozzapp.R
import com.example.boozzapp.adscontrollers.InterstitialAdsHandler
import com.example.boozzapp.pojo.ExploreTemplatesItem
import com.example.boozzapp.pojo.TemplateDetailsPojo
import com.example.boozzapp.utils.PartyZipFileManager
import com.example.boozzapp.utils.RetrofitHelper
import com.example.boozzapp.utils.StoreUserData
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.Util
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_download_template.*
import kotlinx.android.synthetic.main.activity_edit_video.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_preview.*
import kotlinx.android.synthetic.main.dialog_download.*
import kotlinx.android.synthetic.main.dialog_premium.*
import kotlinx.android.synthetic.main.dialog_watermark.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class PreviewActivity : BaseActivity() {
    lateinit var players: SimpleExoPlayer
    private var isPlaying: Boolean = true
    private lateinit var videoPojo: ExploreTemplatesItem
    private var zipFilePath: String? = null
    lateinit var holdDialog: Dialog
    private var totalFileSize: Long = 0
    private var videoId = ""
    var hasShareVideo = false
    private var fromShare = false
    private var fromDownload = false
    lateinit var interstitialAdsHandler: InterstitialAdsHandler
    private var rewardedAd: RewardedAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        activity = this
        storeUserData = StoreUserData(activity)

        setupAd()


        val intent = intent
        hasShareVideo = intent?.extras?.getString("videoId") != null

        players = SimpleExoPlayer.Builder(activity).build()
        player.player = players
        player.useController = false
        players.addListener(object : Player.EventListener {
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                when (state) {
                    ExoPlayer.STATE_READY -> {
                        progressBarPreview.isVisible = false
                    }

                    ExoPlayer.STATE_ENDED -> {
                        players.seekTo(0)
                        players.play()
                    }
                    Player.STATE_BUFFERING -> {
                        progressBarPreview.isVisible = true
                    }
                    Player.STATE_IDLE -> {

                    }
                }
            }
        })

        if (hasShareVideo) {
            templateDetails()
        } else {
            videoPojo = intent.getParcelableExtra("videoPojo")!!
            setPlayerData()
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
            fromDownload = true
            if (videoPojo.isPremium == 1) {
                showPremiumDialog()
            } else {
                interstitialAdsHandler.showNextAd()

            }
        }

        previewShare.setOnClickListener {
            fromShare = true
            interstitialAdsHandler.showNextAd()
        }

        showInterestitialAds()


    }

    private fun showRewardAds(adunitID: String) {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            this,
            adunitID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("TAGS", adError.toString())
                    rewardedAd = null
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d("TAGS", "Ad was loaded.")
                    rewardedAd = ad
                    showRewardedAd() // Call the method to show the ad
                }
            })
    }

    private fun showRewardedAd() {
        rewardedAd?.show(this) {

        }

        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d("TAGS", "Ad dismissed fullscreen content.")
                rewardedAd = null
                players.pause()
                showDownloadDialog()
                videoPojo.let {
                    downloadCacheTemplateZip(it.zipUrl!!, it.zip!!)
                }


            }
        }
    }

    fun showInterestitialAds() {
        showInterAdsProgress()
        interstitialAdsHandler = InterstitialAdsHandler(
            this,
            getString(R.string.GL_In_CatagoryTamplate_Inter_1Tap),
            getString(R.string.FB_In_CatagoryTamplate_Inter_1Tap)
        )
        interstitialAdsHandler.loadInterstitialAds()
        interstitialAdsHandler.setAdListener(object :
            InterstitialAdsHandler.InterstitialAdListeners {
            override fun onAdClosed() {
                Log.i("TAG", "onAdClosed: " + "closed")
                // Called when the ad is closed
                // players.play()
                if (fromDownload) {
                    players.pause()
                    showDownloadDialog()
                    videoPojo.let {
                        downloadCacheTemplateZip(it.zipUrl!!, it.zip!!)
                    }
                } else if (fromShare) {
                    videoId = videoPojo.id.toString()
                    val dynamicUrl = "https://buzzoo.in/share/template/$videoId"
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_TEXT, dynamicUrl)
                    startActivity(Intent.createChooser(shareIntent, "Share via"))
                } else {
                    players.play()

                }

                fromShare = false
                fromDownload = false

            }

            override fun onAdDismissed() {
                Log.i("TAG", "onAdClosed: " + "closed")
                // Called when the ad is dismissed
                if (fromDownload) {
                    players.pause()
                    showDownloadDialog()
                    videoPojo.let {
                        downloadCacheTemplateZip(it.zipUrl!!, it.zip!!)
                    }
                } else if (fromShare) {
                    videoId = videoPojo.id.toString()
                    val dynamicUrl = "https://buzzoo.in/share/template/$videoId"
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_TEXT, dynamicUrl)
                    startActivity(Intent.createChooser(shareIntent, "Share via"))
                } else {
                    players.play()

                }

                fromShare = false
                fromDownload = false


            }

            override fun onAdLoaded() {
                dismissInterAdsProgress()
            }

            override fun onError() {
                dismissInterAdsProgress()
            }
        })

    }


    private fun setupAd() {
        val adRequest = AdRequest.Builder().build()
        previewVideoBannerAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                adPreviewVideoLoadingText.isVisible = false
                previewVideoBannerAdView.isVisible = true
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                Log.i("TAG", "onAdFailedToLoad: Preview${loadAdError.message} ")
                Log.i("TAG", "onAdFailedToLoad: Preview${loadAdError.code} ")

                adPreviewVideoLoadingText.isVisible = true
                previewVideoBannerAdView.isVisible = false

            }
        }
        previewVideoBannerAdView.loadAd(adRequest)
    }

    fun setPlayerData() {
        tvSongName.text = videoPojo.title
        val firstItem: MediaItem =
            MediaItem.fromUri(Uri.parse(videoPojo.videoUrl))
        player.player!!.setMediaItem(firstItem)
        player.player!!.prepare()
        player.player!!.play()
        isPlaying = true
    }

    private fun templateDetails() {
        showProgress()
        val retrofitHelper = RetrofitHelper(activity)
        val call: Call<ResponseBody> =
            retrofitHelper.api().templateDetails(
                intent?.extras?.getString("videoId")!!
            )

        retrofitHelper.callApi(activity, call, object : RetrofitHelper.ConnectionCallBack {
            override fun onSuccess(body: Response<ResponseBody>) {
                dismissProgress()
                val responseString = body.body()!!.string()
                Log.i("TAG", "exploreSuggestionList$responseString")
                val suggestionsPojo =
                    Gson().fromJson(responseString, TemplateDetailsPojo::class.java)
                videoPojo = suggestionsPojo.data!!
                setPlayerData()
            }

            override fun onError(code: Int, error: String) {
                dismissProgress()
                Log.i("Error", error)


            }


        })
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

        setupAd()
        if (Util.SDK_INT > 23) {
            if (player != null) {
                player.onResume()
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PRDownloader.cancelAll()
        interstitialAdsHandler.onDestroy()

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (hasShareVideo) {
            startActivity(Intent(activity, HomeActivity::class.java))
            finish()
        } else {
            super.onBackPressed()
        }
        players.release() // Release the player's resources
    }


    private fun downloadCacheTemplateZip(zipUrl: String, fileName: String) {
        zipFilePath = getZipDirectoryPath() + fileName
        Log.i("TAG", "onDownloadComplete:  before" + getZipDirectoryPath()!!)

        PRDownloader.download(zipUrl, getZipDirectoryPath(), fileName)
            .build()
            .setOnProgressListener { progress ->
                val progressPercent = progress.currentBytes * 100 / progress.totalBytes
                holdDialog.progress_download_video.progress = progressPercent.toInt()

                // Store the total file size for later use
                totalFileSize = progress.totalBytes
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    Log.i("TAG", "onDownloadComplete:  after" + getZipDirectoryPath()!!)
                    holdDialog.dismiss()
                    val unzipTask = UnZipFileFromURLs(zipFilePath!!, getZipDirectoryPath()!!) {
                        // This block will be executed after unzipping is completed
                        // Start the EditVideoActivity here
                        activity.startActivity(
                            Intent(activity, EditVideoActivity::class.java)
                                .putExtra("videoPojo", videoPojo)
                        )
                    }
                    unzipTask.execute()


                }

                override fun onError(error: Error) {
                    players.play()

                    Log.i(
                        "TAG",
                        "onDownloadComplete: " + "Download failed" + error.serverErrorMessage
                    )

                }
            })
    }

    private class UnZipFileFromURLs(
        private val zipFilePath: String,
        private val destinationPath: String,
        private val callback: () -> Unit
    ) : AsyncTask<String, String, Boolean>() {

        override fun doInBackground(vararg params: String?): Boolean {
            try {
                PartyZipFileManager.unzip(zipFilePath, destinationPath)
                return true
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }

        override fun onPostExecute(success: Boolean) {
            super.onPostExecute(success)
            // Call the callback to indicate that the unzipping is completed
            callback()
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


    private fun showDownloadDialog() {
        holdDialog = Dialog(activity)
        holdDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        holdDialog.setContentView(R.layout.dialog_download)
        holdDialog.progress_download_video.progress = 0

        // Set the background of the dialog window to transparent
        holdDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Calculate the desired height of the dialog (e.g., half of the screen)
        val windowHeight = activity.window.decorView.height
        val dialogHeight = windowHeight / 2
        holdDialog.setCanceledOnTouchOutside(false)
        holdDialog.nativeADs.isVisible = true

        val adUnitId = getString(R.string.GL_Tamplate_Create_Native)

        var adLoader = AdLoader.Builder(activity, adUnitId)
            .forNativeAd { nativeAd ->
                // Populate the adView with the native ad properties
                holdDialog.nativeADs.setNativeAd(nativeAd)
                holdDialog.nativeADs.removeAllViews()

            }
            .withAdListener(object : AdListener() {
                override fun onAdLoaded() {
                    holdDialog.tvLoad.isVisible = false
                    holdDialog.nativeADs.isVisible = true
                    // Ad loaded successfully
                    Log.d("NativeAd", "Ad loaded successfully")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    holdDialog.tvLoad.isVisible = true
                    holdDialog.nativeADs.isVisible = false
                    // Ad failed to load
                    Log.d("NativeAd", "Ad failed to load. Error code: ${loadAdError.code}")
                    Log.e("NativeAd", "Error message: ${loadAdError.message}")
                }
            })
            .build()
        val adRequest = AdRequest.Builder().build()
        adLoader.loadAd(adRequest)

        // Set the dialog's window layout parameters
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight)
        holdDialog.window?.setLayout(layoutParams.width, layoutParams.height)

        holdDialog.show()

        holdDialog.btnCancel.setOnClickListener {
            holdDialog.nativeADs.removeAllViews()
            holdDialog.dismiss()
            PRDownloader.cancelAll()

        }

    }


    private fun showPremiumDialog() {
        val holdDialog = Dialog(activity)
        holdDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        holdDialog.setContentView(R.layout.dialog_premium)

        // Set the background of the dialog window to transparent
        holdDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Calculate the desired height of the dialog (e.g., half of the screen)
        val windowHeight = activity.window.decorView.height
        val dialogHeight = windowHeight / 2
        holdDialog.setCanceledOnTouchOutside(false)
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight)
        holdDialog.window?.setLayout(layoutParams.width, layoutParams.height)
        holdDialog.show()

        holdDialog.ivCloseDialogs.setOnClickListener {
            holdDialog.dismiss()

        }


        holdDialog.llUnLock.setOnClickListener {
            holdDialog.dismiss()
            showRewardAds(getString(R.string.GL_RewardPremium))
        }


    }

}



