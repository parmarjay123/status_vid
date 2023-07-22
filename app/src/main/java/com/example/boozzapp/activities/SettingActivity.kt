package com.example.boozzapp.activities

import android.os.Bundle
import com.example.boozzapp.R
import com.example.boozzapp.utils.StoreUserData
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        activity = this
        storeUserData = StoreUserData(activity)

        ivSettingBack.setOnClickListener { finish() }
    }
}