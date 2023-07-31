package com.example.boozzapp.app

import androidx.multidex.MultiDexApplication
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import com.google.firebase.FirebaseApp

class MyApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        val config = PRDownloaderConfig.newBuilder()
            .setReadTimeout(30000)
            .setConnectTimeout(30000)
            .build()
        PRDownloader.initialize(applicationContext, config)
        FirebaseApp.initializeApp(applicationContext)

    }
    // ...
}
