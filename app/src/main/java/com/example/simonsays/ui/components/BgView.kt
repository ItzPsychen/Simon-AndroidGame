package com.example.simonsays.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

import kotlin.math.cos
import kotlin.math.sin

class BgView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // smooth edges (not pixelated)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // clock of animation
    private var time = 0f

    // hue, saturation, value
    private val hsv = floatArrayOf(0f, 0.7f, 0.5f)
    private var shader: RadialGradient? = null

    // draws the background (animated)
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // speed of animation
        time += 0.03f

        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0 || h <= 0) return

        // resolve background color
        val typedValue = TypedValue()
        context.theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true)
        val bgColor = typedValue.data

        canvas.drawColor(bgColor)

        for (i in 0..4) {
            // offset of the position
            val offsetX = (w / 3f) * sin(time * 0.4 + i * 1.5).toFloat()
            val offsetY = (h / 3f) * cos(time * 0.3 + i * 2.1).toFloat()

            // position and dimension
            val centerX = w / 2 + offsetX
            val centerY = h / 2 + offsetY
            val radius = w * 1.5f

            // different colors (5)
            hsv[0] = (time * 8 + i * 72) % 360
            
            // adjustment for night mode saturation
            val isDark = (context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES
            hsv[1] = if (isDark) 0.4f else 0.7f
            hsv[2] = if (isDark) 0.2f else 0.5f

            val color = Color.HSVToColor(140, hsv)

            @SuppressLint("DrawAllocation")
            shader = RadialGradient(
                centerX, centerY, radius,
                color, Color.TRANSPARENT, Shader.TileMode.CLAMP
            )

            paint.shader = shader
            canvas.drawRect(0f, 0f, w, h, paint)
        }

        // invalidate to redraw (replace the old with the new)
        invalidate()
    }
}
