package com.example.boozzapp.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.os.Build
import android.util.Log
import android.webkit.WebView
import androidx.multidex.MultiDexApplication
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import com.facebook.FacebookSdk
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class MyApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        val config = PRDownloaderConfig.newBuilder()
            .setReadTimeout(30000)
            .setConnectTimeout(30000)
            .build()
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


            //AudienceNetworkAds.initialize(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        setNotificationChannel()
        subscribeTopic()

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
}
