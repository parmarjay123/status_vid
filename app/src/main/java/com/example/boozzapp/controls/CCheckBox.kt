package com.example.boozzapp.controls

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet

import androidx.appcompat.widget.AppCompatCheckBox
import com.example.boozzapp.controls.CButton

class CCheckBox : AppCompatCheckBox {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        applyCustomFont(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        applyCustomFont(context, attrs)

    }

    private fun applyCustomFont(context: Context, attrs: AttributeSet) {
        val textStyle =
            attrs.getAttributeIntValue(CButton.ANDROID_SCHEMA, "textStyle", Typeface.NORMAL)
        typeface = selectTypeface(context, textStyle)
        isAllCaps = false
    }


    private fun selectTypeface(context: Context, textStyle: Int): Typeface {
        when (textStyle) {
            Typeface.BOLD -> return Typeface.createFromAsset(context.assets, "Lato-Bold.ttf")
            Typeface.NORMAL -> return Typeface.createFromAsset(
                context.assets,
                "Lato-Medium.ttf"
            )// regular
            else -> return Typeface.createFromAsset(
                context.assets,
                "Lato-Regular.ttf"
            )// regular
        }
    }


    companion object {

        val ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android"
    }
}