package com.example.boozzapp.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.example.boozzapp.R
import com.example.boozzapp.adscontrollers.InterstitialAdsHandler
import com.example.boozzapp.utils.SessionManager
import com.example.boozzapp.utils.StoreUserData
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import kotlinx.android.synthetic.main.activity_preview_quotes.*
import kotlinx.android.synthetic.main.dialog_download.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PreviewQuotesActivity : BaseActivity() {
    private var image: String? = ""
    private var holdDialog: Dialog? = null
    private var downloadedImageFile: File? = null

    private var activityOpenCount: Int = 0
    private lateinit var sessionManager: SessionManager
    lateinit var interstitialAdsHandler: InterstitialAdsHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_quotes)
        activity = this
        storeUserData = StoreUserData(activity)
        sessionManager = SessionManager(activity)

        setupAd()

        if (intent.getStringExtra("imageURL") != null) {
            image = intent.getStringExtra("imageURL")
            Glide.with(activity).load(image).into(ivQuotesFullImage)
        }

        previewQuotesBack.setOnClickListener {
            finish()
        }
        llDownload.setOnClickListener { image?.let { it1 -> downloadQuote(it1, false) } }
        llShare.setOnClickListener {
            if (downloadedImageFile != null) {
                shareDownloadedQuote(downloadedImageFile!!.absolutePath);
            } else {
                downloadQuote(image, true)
            }
        }

        showInterestitialSecondTap()
    }

    override fun onResume() {
       super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::interstitialAdsHandler.isInitialized) {
            interstitialAdsHandler.onDestroy()
        }
    }


    override fun onBackPressed() {
        if (intent.getBooleanExtra("isNotification", false)) {
            startActivity(Intent(activity, HomeActivity::class.java))
            finish()
        } else {
            super.onBackPressed()
        }

    }

    fun showInterestitialSecondTap() {

        if (sessionManager.isNewSession()) {
            storeUserData.setInt(
                com.example.boozzapp.utils.Constants.ADS_COUNT_DASHBOARD_CLICK,
                0
            )
            sessionManager.updateSessionStartTime()
        }
        activityOpenCount =
            storeUserData.getInt(com.example.boozzapp.utils.Constants.ADS_COUNT_DASHBOARD_CLICK)
        if (activityOpenCount == 4) {
            showInterAdsProgress()
            interstitialAdsHandler = InterstitialAdsHandler(
                this,
                getString(R.string.GL_INQuotecatagory_Inter_2TAP),
                ""
            )
            interstitialAdsHandler.loadInterstitialAds()
            interstitialAdsHandler.setAdListener(object :
                InterstitialAdsHandler.InterstitialAdListeners {
                override fun onAdClosed() {
                    Log.i("TAG", "onAdClosed: " + "closed")
                    // Called when the ad is closed
                }

                override fun onAdDismissed() {
                    Log.i("TAG", "onAdClosed: " + "closed")
                    // Called when the ad is dismissed
                }

                override fun onAdLoaded() {
                    dismissInterAdsProgress()
                }

                override fun onErrorAds() {
                    dismissInterAdsProgress()
                }
            })

            storeUserData.setInt(
                com.example.boozzapp.utils.Constants.ADS_COUNT_DASHBOARD_CLICK,
                0
            )

        } else {
            activityOpenCount++
            storeUserData.setInt(
                com.example.boozzapp.utils.Constants.ADS_COUNT_DASHBOARD_CLICK,
                activityOpenCount
            )
        }
    }

    private fun setupAd() {
        val adRequest = AdRequest.Builder().build()
        previewBannerAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                adPreviewLoadingText.isVisible = false
                previewBannerAdView.isVisible = true
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                Log.i("TAG", "onAdFailedToLoad: previewQuotes${loadAdError.message} ")
                Log.i("TAG", "onAdFailedToLoad: previewQuotes${loadAdError.code} ")

                adPreviewLoadingText.isVisible = true
                previewBannerAdView.isVisible = false

            }
        }
        previewBannerAdView.loadAd(adRequest)
    }

    private fun downloadQuote(imageUrl: String?, isShare: Boolean) {
        // Show download dialog
        showDownloadDialog()
        val fileName = getCurrentTime() + ".jpg"
        val downloadPath = getDownloadPath()


        downloadedImageFile = File(downloadPath, fileName)
        if (downloadedImageFile!!.exists()) {
            Log.i("TAG", "File already exists: " + downloadedImageFile!!.absolutePath)
            // Handle already downloaded file here if needed
            return
        }

        PRDownloader.download(imageUrl, downloadPath, fileName)
            .build()
            .setOnProgressListener { progress ->
                val progressPercent = (progress.currentBytes * 100 / progress.totalBytes).toInt()
                holdDialog?.progress_download_video?.progress = progressPercent
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    holdDialog?.dismiss()
                    // Set the downloaded image file
                    downloadedImageFile = File(downloadPath, fileName)

                    refreshGallery(activity, downloadedImageFile!!)
                    if (isShare) {
                        shareDownloadedQuote(downloadedImageFile!!.absolutePath);
                    } else {
                        Toast.makeText(activity, "Download Successfully", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(error: Error) {
                    Log.i("TAG", "Download error: $error")
                    holdDialog?.dismiss()
                    // Handle download error here
                }
            })
    }

    private fun shareDownloadedQuote(imagePath: String?) {
        if (imagePath != null) {
            val imageFile = File(imagePath)
            if (imageFile.exists()) {
                val imageUri = FileProvider.getUriForFile(
                    this,
                    "$packageName.provider",
                    imageFile
                )
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "image/*"
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Add this line
                startActivity(Intent.createChooser(shareIntent, "Share Image"))
            }
        }
    }

    fun getDownloadPath(): String? {
        val externalDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString()
        // }
        val dir = File(
            externalDirectory + File.separator + "Buzo_quotes"
        )
        if (!dir.exists()) dir.mkdirs()
        return dir.absolutePath + File.separator
    }


    private fun refreshGallery(mContext: Activity, file: File) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri = Uri.fromFile(file)
        mediaScanIntent.data = contentUri
        mContext.sendBroadcast(mediaScanIntent)

    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun showDownloadDialog() {
        holdDialog = Dialog(activity)
        holdDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        holdDialog!!.setContentView(R.layout.dialog_download)
        holdDialog!!.progress_download_video.progress = 0

        // Set the background of the dialog window to transparent
        holdDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Calculate the desired height of the dialog (e.g., half of the screen)
        val windowHeight = windowManager.defaultDisplay.height
        val dialogHeight = windowHeight / 2
        holdDialog!!.setCanceledOnTouchOutside(false)

        // Set the dialog's window layout parameters
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight)
        holdDialog!!.window!!.setLayout(layoutParams.width, layoutParams.height)
        holdDialog!!.show()
        holdDialog!!.btnCancel.setOnClickListener(View.OnClickListener {
            holdDialog!!.dismiss()
            PRDownloader.cancelAll()
        })
    }

}