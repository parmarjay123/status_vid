package com.example.boozzapp.app

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import com.facebook.FacebookSdk
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*

class MyApplication : MultiDexApplication(), Application.ActivityLifecycleCallbacks,
    LifecycleObserver {
    private val AD_UNIT_ID = "ca-app-pub-4706162076647622/9735382349"
    private lateinit var appOpenAdManager: AppOpenAdManager
    private var currentActivity: Activity? = null

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)

        val config = PRDownloaderConfig.newBuilder()
            .setReadTimeout(30000)
            .setConnectTimeout(30000)
            .build()
        FacebookSdk.sdkInitialize(applicationContext)

        PRDownloader.initialize(applicationContext, config)
        FirebaseApp.initializeApp(applicationContext)
        //        setAutoLogAppEventsEnabled(true);

        FacebookSdk.setAutoInitEnabled(true)
        FacebookSdk.fullyInitialize()


        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (!packageName.equals(
                        getProcessName(),
                        ignoreCase = true
                    ) && getProcessName() != null
                ) WebView.setDataDirectorySuffix(getProcessName())
            }

            MobileAds.initialize(this)
            ProcessLifecycleOwner.get().lifecycle.addObserver(this)
            appOpenAdManager = AppOpenAdManager()


            //AudienceNetworkAds.initialize(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        setNotificationChannel()
        subscribeTopic()

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        val currentActivityClassName = currentActivity?.javaClass?.name
        val activitiesWhereAdShouldNotShow = listOf(
            "com.example.boozzapp.activities.EditVideoActivity",
            // Add more activity class names as needed
        )

        if (!activitiesWhereAdShouldNotShow.contains(currentActivityClassName)) {
            // Show the ad (if available) when the app moves to foreground.
            currentActivity?.let { appOpenAdManager.showAdIfAvailable(it) }
        }
        // Show the ad (if available) when the app moves to foreground.
        currentActivity?.let { appOpenAdManager.showAdIfAvailable(it) }
    }

    private fun subscribeTopic() {
        try {
            FirebaseApp.initializeApp(this)
            //            FirebaseMessaging.getInstance().subscribeToTopic("test_noti").addOnCompleteListener(task -> {
            FirebaseMessaging.getInstance().subscribeToTopic("boozz")
                .addOnCompleteListener { task: Task<Void?> ->
                    var msg = "boozz" + " subscribed"
                    if (!task.isSuccessful) {
                        msg = "Not subscribed!"
                    }
                    Log.d("FCM>>>", msg)
                }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private inner class AppOpenAdManager {

        private var appOpenAd: AppOpenAd? = null
        private var isLoadingAd = false
        var isShowingAd = false

        /** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad. */
        private var loadTime: Long = 0

        /**
         * Load an ad.
         *
         * @param context the context of the activity that loads the ad
         */
        fun loadAd(context: Context) {
            // Do not load ad if there is an unused ad or one is already loading.
            if (isLoadingAd || isAdAvailable()) {
                return
            }

            isLoadingAd = true
            val request = AdRequest.Builder().build()
            AppOpenAd.load(
                context,
                AD_UNIT_ID,
                request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    /**
                     * Called when an app open ad has loaded.
                     *
                     * @param ad the loaded app open ad.
                     */
                    override fun onAdLoaded(ad: AppOpenAd) {
                        appOpenAd = ad
                        isLoadingAd = false
                        loadTime = Date().time
                        Log.d("LOG_TAG", "onAdLoaded.")
                    }

                    /**
                     * Called when an app open ad has failed to load.
                     *
                     * @param loadAdError the error.
                     */
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        isLoadingAd = false
                        Log.d("LOG_TAG", "onAdFailedToLoad: " + loadAdError.message)
                    }
                }
            )
        }

        /** Check if ad was loaded more than n hours ago. */
        private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
            val dateDifference: Long = Date().time - loadTime
            val numMilliSecondsPerHour: Long = 3600000
            return dateDifference < numMilliSecondsPerHour * numHours
        }

        /** Check if ad exists and can be shown. */
        private fun isAdAvailable(): Boolean {
            // Ad references in the app open beta will time out after four hours, but this time limit
            // may change in future beta versions. For details, see:
            // https://support.google.com/admob/answer/9341964?hl=en
            return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
        }

        /**
         * Show the ad if one isn't already showing.
         *
         * @param activity the activity that shows the app open ad
         */
        fun showAdIfAvailable(activity: Activity) {
            showAdIfAvailable(
                activity,
                object : OnShowAdCompleteListener {
                    override fun onShowAdComplete() {
                        // Empty because the user will go back to the activity that shows the ad.
                    }
                }
            )
        }

        /**
         * Show the ad if one isn't already showing.
         *
         * @param activity the activity that shows the app open ad
         * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
         */
        fun showAdIfAvailable(
            activity: Activity,
            onShowAdCompleteListener: OnShowAdCompleteListener
        ) {
            // If the app open ad is already showing, do not show the ad again.
            if (isShowingAd) {
                Log.d("LOG_TAG", "The app open ad is already showing.")
                return
            }

            // If the app open ad is not available yet, invoke the callback then load the ad.
            if (!isAdAvailable()) {
                Log.d("LOG_TAG", "The app open ad is not ready yet.")
                onShowAdCompleteListener.onShowAdComplete()
                loadAd(activity)
                return
            }

            Log.d("LOG_TAG", "Will show ad.")

            appOpenAd!!.setFullScreenContentCallback(
                object : FullScreenContentCallback() {
                    /** Called when full screen content is dismissed. */
                    override fun onAdDismissedFullScreenContent() {
                        // Set the reference to null so isAdAvailable() returns false.
                        appOpenAd = null
                        isShowingAd = false
                        Log.d("LOG_TAG", "onAdDismissedFullScreenContent.")


                        onShowAdCompleteListener.onShowAdComplete()
                        loadAd(activity)
                    }

                    /** Called when fullscreen content failed to show. */
                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        appOpenAd = null
                        isShowingAd = false
                        Log.d("LOG_TAG", "onAdFailedToShowFullScreenContent: " + adError.message)


                        onShowAdCompleteListener.onShowAdComplete()
                        loadAd(activity)
                    }

                    /** Called when fullscreen content is shown. */
                    override fun onAdShowedFullScreenContent() {
                        Log.d("LOG_TAG", "onAdShowedFullScreenContent.")
                    }
                }
            )
            isShowingAd = true
            appOpenAd!!.show(activity)
        }
    }

    private fun setNotificationChannel() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var channelList: List<NotificationChannel?>? = null
            if (notificationManager != null) {
                channelList = notificationManager.notificationChannels
            }
            if (channelList != null && channelList.size != 1) {
                val channel = NotificationChannel(
                    "Video",
                    "Video of the day",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                channel.description = "Show video of the day every user"
                channel.importance = NotificationManager.IMPORTANCE_DEFAULT
                channel.enableLights(true)
                channel.enableVibration(true)
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    interface OnShowAdCompleteListener {
        fun onShowAdComplete()
    }

    fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener) {
        // We wrap the showAdIfAvailable to enforce that other classes only interact with MyApplication
        // class.
        appOpenAdManager.showAdIfAvailable(activity, onShowAdCompleteListener)
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {

    }

    override fun onActivityStarted(p0: Activity) {
        if (!appOpenAdManager.isShowingAd) {
            currentActivity = p0
        }
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityStopped(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }


}
