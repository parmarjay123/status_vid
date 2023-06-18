package com.example.boozzapp.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

object AnimationUtils {
    fun shrinkView(view: View) {
        val scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1.0f, 0.5f)
        val scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1.0f, 0.5f)

        val animatorSet = AnimatorSet()
        animatorSet.duration = 300
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.playTogether(scaleX, scaleY)
        animatorSet.start()
    }
}