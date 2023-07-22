package com.example.boozzapp.activities

import android.os.Bundle
import com.example.boozzapp.R
import com.example.boozzapp.utils.StoreUserData

class SearchActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        activity = this
        storeUserData = StoreUserData(activity)
    }
}