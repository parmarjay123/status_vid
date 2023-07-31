package com.example.boozzapp.controls

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.AppCompatTextView


class CTextView : AppCompatTextView {
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        applyCustomFont(context, attrs)
    }

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
        if (gravity == Gravity.CENTER || gravity == Gravity.CENTER_HORIZONTAL || gravity == Gravity.CENTER_VERTICAL) {
        } else {
            textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            textDirection = View.TEXT_DIRECTION_LOCALE
        }
    }


    private fun selectTypeface(context: Context, textStyle: Int): Typeface {
        when (textStyle) {
            Typeface.BOLD -> return Typeface.createFromAsset(context.assets, "Poppins-SemiBold.ttf")
            Typeface.NORMAL -> return Typeface.createFromAsset(
                context.assets,
                "Poppins-SemiBoldItalic.ttf"
            )// regular
            else -> return Typeface.createFromAsset(
                context.assets,
                "Poppins-Regular.ttf"
            )// regular
        }
    }


    companion object {

        val ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android"
    }
}