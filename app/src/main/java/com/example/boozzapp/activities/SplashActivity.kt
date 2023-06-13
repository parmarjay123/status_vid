package com.example.boozzapp.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import com.example.boozzapp.R
import com.example.boozzapp.utils.StoreUserData

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        activity=this
        storeUserData= StoreUserData(activity)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(activity, HomeActivity::class.java))
            finish()
        }, 1500)

    }
}