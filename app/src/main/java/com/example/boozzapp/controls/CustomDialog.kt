package com.example.boozzapp.controls

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import com.example.boozzapp.R
import kotlinx.android.synthetic.main.custom_dialog.*



class CustomDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_dialog)

        btnPositive.setOnClickListener { dismiss() }
        btnNegative.setOnClickListener { dismiss() }
    }

    fun setTitle(message: String?) {
        tvTitle.visibility = View.VISIBLE
        tvTitle.text = message
    }

    fun setMessage(message: String?) {
        tvMessage.text = message
    }

    override fun setTitle(string: Int) {
        tvTitle.visibility = View.VISIBLE
        tvTitle.setText(string)
    }

    fun setMessage(string: Int) {
        tvMessage.setText(string)
    }

    fun setPositiveButton(text: String?) {
        btnPositive.text = text
        btnPositive.visibility = View.VISIBLE
    }

    fun setPositiveButton(string: Int) {
        btnPositive.setText(string)
        btnPositive.visibility = View.VISIBLE
    }

    fun setPositiveButton(text: String, clickListener: View.OnClickListener) {
        btnPositive.text = text
        btnPositive.visibility = View.VISIBLE
        btnPositive.setOnClickListener(clickListener)
    }

    fun setPositiveButton(string: Int, clickListener: View.OnClickListener) {
        btnPositive.setText(string)
        btnPositive.visibility = View.VISIBLE
        btnPositive.setOnClickListener(clickListener)
    }

    fun setNegativeButton(text: String?) {
        btnNegative.text = text
        btnNegative.visibility = View.VISIBLE
    }

    fun setNegativeButton(text: Int) {
        btnNegative.setText(text)
        btnNegative.visibility = View.VISIBLE
    }

    fun setNegativeButton(text: String?, clickListener: View.OnClickListener) {
        btnNegative.text = text
        btnNegative.visibility = View.VISIBLE
        btnNegative.setOnClickListener(clickListener)
    }

    fun setNegativeButton(string: Int, clickListener: View.OnClickListener) {
        btnNegative.setText(string)
        btnNegative.visibility = View.VISIBLE
        btnNegative.setOnClickListener(clickListener)
    }
}