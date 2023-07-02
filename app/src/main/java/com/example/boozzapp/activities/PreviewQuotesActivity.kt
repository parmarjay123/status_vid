package com.example.boozzapp.activities

import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.boozzapp.R
import com.example.boozzapp.utils.StoreUserData
import kotlinx.android.synthetic.main.activity_preview_quotes.*

class PreviewQuotesActivity : BaseActivity() {
    var image = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_quotes)
        activity = this
        storeUserData = StoreUserData(activity)

        previewQuotesBack.setOnClickListener { finish() }

        if (intent.getStringExtra("imageURL") != null) {
            image = intent.getStringExtra("imageURL").toString()
            Glide.with(activity).load(image).into(ivQuotesFullImage)
        }


    }
}