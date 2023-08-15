package com.example.boozzapp.activities

import NativeAdItem
import android.os.Bundle
import android.os.Environment
import android.util.Log
import com.example.boozzapp.R
import com.example.boozzapp.adapter.MyVideoAdapter
import com.example.boozzapp.utils.StoreUserData
import kotlinx.android.synthetic.main.activity_my_video.*
import java.io.File

class MyVideoActivity : BaseActivity() {
    private val videoList: MutableList<Any?> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_video)
        activity = this
        storeUserData = StoreUserData(activity)

        ivMyVideoBack.setOnClickListener { finish() }
        loadVideoList()
    }

    private fun loadVideoList() {

        val externalDirectory: String =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString()

        val videoDirectoryName = "Boozz_Status_Maker"
        val videoDirectory = File(externalDirectory, videoDirectoryName)

        try {
            val files = videoDirectory.listFiles()
            if (files != null) {
                videoList.clear()
                videoList.addAll(files.toList())
                for ((index, fileData) in files.withIndex()) {
                    fileData?.let { videoList.add(it) }
                    if ((index + 1) % 4 == 0 && index != files.size - 1) {
                        videoList.add(NativeAdItem()) // Add a marker for the native ad
                    }
                }
                val myVideoAdapter = MyVideoAdapter(activity, videoList)
                activity.rvMyVideos.adapter = myVideoAdapter

            } else {
                Log.d("TAG2", "No files found in the directory")
            }
        } catch (e: Exception) {
            Log.e("TAG3", "Error loading video list: ${e.message}")
        }
    }
}