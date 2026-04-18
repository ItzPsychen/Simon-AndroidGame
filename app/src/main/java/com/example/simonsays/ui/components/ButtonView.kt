package com.example.simonsays.ui.components

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.media.AudioManager
import android.media.ToneGenerator
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColorInt

class ButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var buttonColorRes: Int = -1
    private var staticColor: Int? = null
    private var label: String = ""
    private var isPressedState = false
    private var customAlpha: Float? = null
    private var showLabel: Boolean = true
    private var glowOffset = 0f

    // tone when any button is pressed
    private var toneGenerator: ToneGenerator? = null

    // buttons transparency (default)
    private val defaultButtonAlpha = 0.6f

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    // strokes are used for borders
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = "#333333".toColorInt()
        strokeWidth = 8f
    }

    // values for text in the buttons
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

    // play tone when anything is pressed
    private fun playSound() {
        try {
            if (toneGenerator == null) {
                toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            }
            toneGenerator?.startTone(ToneGenerator.TONE_SUP_PIP, 50)
        } catch (_: Exception) { }
    }

    // called when the view is removed
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        toneGenerator?.release()
        toneGenerator = null
    }

    // configures the buttons with colors and labels
    fun setConfig(color: Int, label: String, alpha: Float? = null, textSize: Float = 100f, isBold: Boolean = true) {
        if (color in 1..0x01000000) {
            this.buttonColorRes = color
            this.staticColor = null
        } else {
            this.staticColor = color
            this.buttonColorRes = -1
        }
        
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

    // shows or hides the label (depending on mode)
    fun setShowLabel(show: Boolean) {
        this.showLabel = show
        invalidate()
    }

    // glow effect with fade out
    fun glow(duration: Long) {
        ValueAnimator.ofFloat(0.4f, 0f).apply {
            this.duration = duration
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                glowOffset = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    // listener called whenever a button is touched
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isPressedState = true
                playSound()
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

    // draws the buttons (with everything else)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // gap width stroke-button
        val inset = strokePaint.strokeWidth / 2f
        rect.set(inset, inset, width - inset, height - inset)

        // rounded edges
        val cornerRadius = 40f

        // alpha changes (if pressed)
        val baseAlpha = customAlpha ?: defaultButtonAlpha
        val boost = if (isPressedState) 0.25f else glowOffset
        val currentAlpha = (baseAlpha + boost).coerceAtMost(1.0f)
        
        val resolvedColor = if (buttonColorRes != -1) {
            ContextCompat.getColor(context, buttonColorRes)
        } else {
            staticColor ?: Color.GRAY
        }

        // draw everything
        fillPaint.color = ColorUtils.setAlphaComponent(resolvedColor, (currentAlpha * 255).toInt())
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, fillPaint)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, strokePaint)
        
        if (showLabel) {
            canvas.drawText(label, width / 2f, height / 2f - (textPaint.descent() + textPaint.ascent()) / 2f, textPaint)
        }
    }
}
