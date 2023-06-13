package com.example.boozzapp.controls

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import com.example.boozzapp.R
import kotlinx.android.synthetic.main.custom_progress_dialog.*



class CustomProgressDialog(context: Context) : Dialog(context) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_progress_dialog)

    }

    fun setTitle(message: String?) {
        tv_title.visibility = View.VISIBLE
        tv_title.text = message
    }

    override fun setTitle(string: Int) {
        tv_title.visibility = View.VISIBLE
        tv_title.setText(string)
    }
}