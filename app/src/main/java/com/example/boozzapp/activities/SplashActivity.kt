package com.example.boozzapp.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.example.boozzapp.R
import com.example.boozzapp.utils.Constants
import com.example.boozzapp.utils.StoreUserData
import com.google.firebase.iid.FirebaseInstanceId

class SplashActivity : BaseActivity() {
    private var isActivityFinishing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        activity = this
        storeUserData = StoreUserData(activity)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(activity) { instanceIdResult ->
            val newToken = instanceIdResult.token
            Log.e("newToken", newToken)
            storeUserData.setString(Constants.USER_FCM, newToken)
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        Handler(Looper.getMainLooper()).postDelayed({
            if (!isActivityFinishing) {
                startActivity(Intent(activity, HomeActivity::class.java))
                finish()
            }
        }, 1500)
    }

    override fun onDestroy() {
        isActivityFinishing = true
        super.onDestroy()
    }
}
