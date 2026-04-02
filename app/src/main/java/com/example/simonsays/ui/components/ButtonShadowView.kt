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

    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        // Use a softer, more natural shadow color
        color = Color.BLACK
        alpha = 90 // Lower alpha (approx 25%) is much smoother
        maskFilter = BlurMaskFilter(35f, BlurMaskFilter.Blur.NORMAL)
    }

    private val rect = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 1. Padding: This is CRITICAL.
        // It gives the blur space to 'fuzz out' without hitting the view edge.
        val padding = 40f

        // 2. Shrink the shadow width:
        // Making the shadow narrower than the button creates a "floating" look.
        val sideInset = 15f

        // 3. Vertical Offset:
        // Pushing the shadow down relative to the button height
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
        // Essential for BlurMaskFilter to render
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }
}