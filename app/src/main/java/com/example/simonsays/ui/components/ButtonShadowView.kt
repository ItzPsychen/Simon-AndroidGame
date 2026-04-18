package com.example.simonsays.ui.components

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class ButtonShadowView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // shadow object with values
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        alpha = 90
        maskFilter = BlurMaskFilter(35f, BlurMaskFilter.Blur.NORMAL)
    }

    private val rect = RectF()

    // draws the shadows in the buttons
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val padding = 40f
        val sideInset = 15f
        val verticalOffset = 30f

        rect.set(
            padding + sideInset,
            padding + verticalOffset,
            width - padding - sideInset,
            height - padding + verticalOffset
        )

        val cornerRadius = 40f
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, shadowPaint)
    }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }
}