package com.example.boozzapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import com.example.boozzapp.R
import com.example.boozzapp.utils.StoreUserData
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        activity = this
        storeUserData = StoreUserData(activity)

        llFilter.setOnClickListener {
            flBottom.isVisible = false
            llBottom.isVisible = true
        }

        ivClose.setOnClickListener {
            flBottom.isVisible = true
            llBottom.isVisible = false
        }

        llSetting.setOnClickListener {
            startActivity(Intent(activity, SettingActivity::class.java))
        }


    }
}