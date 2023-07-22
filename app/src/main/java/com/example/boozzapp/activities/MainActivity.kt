package com.example.boozzapp.activities

import android.os.Bundle
import com.example.boozzapp.R
import com.example.boozzapp.utils.StoreUserData

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activity = this
        storeUserData = StoreUserData(activity)
    }
}