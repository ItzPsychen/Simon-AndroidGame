package com.example.simonsays.ui.components

import android.content.Context
import android.graphics.*
import android.media.AudioManager
import android.media.ToneGenerator
import android.util.AttributeSet
import android.view.MotionEvent
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
    private var isPressedState = false
    private var customAlpha: Float? = null
    private var showLabel: Boolean = true

    // tone when any button is pressed
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

    // buttons slightly transparent
    private val defaultButtonAlpha = 0.75f

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

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        isClickable = true
    }

    fun setConfig(color: Int, label: String, alpha: Float? = null, textSize: Float = 100f, isBold: Boolean = true) {
        this.buttonColor = color
        this.label = label
        this.customAlpha = alpha
        this.textPaint.textSize = textSize
        this.textPaint.typeface = if (isBold) {
            Typeface.create("sans-serif-medium", Typeface.BOLD)
        } else {
            Typeface.create("sans-serif-medium", Typeface.NORMAL)
        }
        invalidate()
    }

    fun setShowLabel(show: Boolean) {
        this.showLabel = show
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isPressedState = true
                toneGenerator.startTone(ToneGenerator.TONE_SUP_PIP, 50)
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isPressedState = false
                invalidate()
                if (event.action == MotionEvent.ACTION_UP) {
                    performClick()
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // gap width stroke-button
        val inset = strokePaint.strokeWidth / 2f
        rect.set(inset, inset, width - inset, height - inset)

        // rounded edges
        val cornerRadius = 40f

        // draw fill (alpha changed if pressed)
        val baseAlpha = customAlpha ?: defaultButtonAlpha
        val currentAlpha = if (isPressedState) 1.0f.coerceAtMost(baseAlpha + 0.15f) else baseAlpha
        fillPaint.color = ColorUtils.setAlphaComponent(buttonColor, (currentAlpha * 255).toInt())
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, fillPaint)

        // draw stroke and label
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, strokePaint)
        
        if (showLabel) {
            canvas.drawText(label, width / 2f, height / 2f - (textPaint.descent() + textPaint.ascent()) / 2f, textPaint)
        }
    }
}
