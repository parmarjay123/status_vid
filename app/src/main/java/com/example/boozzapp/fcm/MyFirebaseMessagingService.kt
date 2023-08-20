package com.example.boozzapp.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.example.boozzapp.R
import com.example.boozzapp.activities.PreviewActivity
import com.example.boozzapp.pojo.ExploreTemplatesItem
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import java.util.concurrent.ExecutionException

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i("FCMID>>>", token)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 = NotificationChannel(
                CHANNEL_ID,
                "Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(channel1)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        //  deleteCache(this);
        Log.e("checknotification", "true")
        val data = remoteMessage.data
        Log.i("TAG", "onMessageReceived: $data")
        val title = remoteMessage.data["title"]
        val desc = remoteMessage.data["message"]
        val bundleData = remoteMessage.data["template"]
        sendVideoNotification(applicationContext, title, desc, bundleData)
    }

    private fun sendVideoNotification(
        mContext: Context,
        title: String?,
        description: String?,
        bundleData: String?
    ) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val intent: Intent
        intent = Intent(applicationContext, PreviewActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra("isNotification", true)
        val modelVideoList: ExploreTemplatesItem =
            Gson().fromJson(bundleData, ExploreTemplatesItem::class.java)
        intent.putExtra("videoId", modelVideoList.id.toString())
        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                mContext,
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getActivity(mContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "Video",
                "Video of the day", NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Show video of the day every user"
            channel.importance = NotificationManager.IMPORTANCE_DEFAULT
            channel.enableLights(true)
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
        }
        val futureTarget: FutureTarget<Bitmap> = Glide.with(this)
            .asBitmap()
            .load(modelVideoList.thumbnailUrl)
            .submit()
        var notificationBuilder: NotificationCompat.Builder? = null
        try {
            notificationBuilder = NotificationCompat.Builder(this, "Video")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(
                    NotificationCompat.BigPictureStyle(
                        NotificationCompat.Builder(
                            mContext,
                            "Video"
                        )
                    )
                        .bigPicture(futureTarget.get())
                )
                .setColor(Color.parseColor("#1B5BA8"))
                .setContentTitle(title)
                .setContentText(description)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
        } catch (e: ExecutionException) {
            e.printStackTrace()
            Log.i("TAG", "sendVideoNotification: " + e.message)
        } catch (e: InterruptedException) {
            e.printStackTrace()
            Log.i("TAG", "sendVideoNotification: " + e.message)
        }
        notificationManager.notify("Video", 123, notificationBuilder!!.build())
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
    }

    companion object {
        const val CHANNEL_ID = "channel111"
    }
}