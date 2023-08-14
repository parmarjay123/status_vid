package com.example.boozzapp.adscontrollers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import androidx.appcompat.widget.AppCompatImageView
import android.graphics.RectF
import android.util.AttributeSet

class PartyRoundRectCornerImageView : AppCompatImageView {
    private val radius = 13.0f
    private var path: Path? = null
    private var rect: RectF? = null

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
        init()
    }

    private fun init() {
        path = Path()
    }

    override fun onDraw(canvas: Canvas) {
        rect = RectF(0f, 0f, this.width.toFloat(), this.height.toFloat())
        path!!.addRoundRect(rect!!, radius, radius, Path.Direction.CW)
        canvas.clipPath(path!!)
        super.onDraw(canvas)
    }
}