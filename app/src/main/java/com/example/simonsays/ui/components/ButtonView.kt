package com.example.simonsays.ui.components

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColorInt

class ButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var buttonColor: Int = Color.GRAY
    private var label: String = ""

    // buttons are slightly transparent
    private val buttonAlpha = 0.75f

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    // stroke used for borders
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = "#333333".toColorInt()
        strokeWidth = 8f
    }

    // label inside each button
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 100f
        typeface = Typeface.create("sans-serif-medium", Typeface.BOLD)
    }

    private val rect = RectF()

    fun setConfig(color: Int, label: String) {
        this.buttonColor = color
        this.label = label
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // gap width stroke-button
        val inset = strokePaint.strokeWidth / 2f
        rect.set(inset, inset, width - inset, height - inset)

        // rounded edges
        val cornerRadius = 40f
        
        // fill, stroke and label
        fillPaint.color = ColorUtils.setAlphaComponent(buttonColor, (buttonAlpha * 255).toInt())
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, fillPaint)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, strokePaint)
        canvas.drawText(label, width / 2f, height / 2f - (textPaint.descent() + textPaint.ascent()) / 2f, textPaint)
    }
}
