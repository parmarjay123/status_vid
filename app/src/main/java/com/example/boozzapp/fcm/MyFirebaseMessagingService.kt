package com.example.boozzapp.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.boozzapp.R
import com.example.boozzapp.activities.MainActivity
import com.example.boozzapp.utils.Constants
import com.example.boozzapp.utils.StoreUserData
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        StoreUserData(this).setString(Constants.USER_FCM, token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // sendNotification("Test")

        Log.d(
            TAG,
            "From: " + remoteMessage.data
        )
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(
                TAG,
                "Message data payload: " + remoteMessage.data["message"]
            )
            sendNotification(
                remoteMessage.data["title"],
                remoteMessage.data["message"],
                remoteMessage.data["template"],

            )
        }
    }

    private fun sendNotification(
        title: String?, message: String?, template: String?
    ) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("isNotification", true)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "signals"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channelName: CharSequence = getString(R.string.app_name)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, channelName, importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern = longArrayOf(100, 200)
            notificationManager.createNotificationChannel(notificationChannel)
        }


        val defaultSoundUri =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(100, 200))
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
        notificationId++


        notificationManager.notify(
            notificationId,
            notificationBuilder.build()

        )
    }

    companion object {
        internal var notificationId = 0
        private const val TAG = "MyFirebaseMsgService"
    }
}