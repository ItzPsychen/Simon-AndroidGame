package com.example.simonsays.ui.components

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils

import com.example.simonsays.R
import com.example.simonsays.logic.ToneConstants
import com.example.simonsays.logic.TonePlayer
import com.example.simonsays.logic.GameManager
import androidx.core.content.withStyledAttributes

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
    private var soundEnabled: Boolean = true

    // frequency for the tone when pressed (Option A: AudioTrack)
    private var frequency: Double = ToneConstants.DEFAULT_FREQUENCY

    // buttons transparency (default)
    private val defaultButtonAlpha = 0.6f

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    // strokes are used for borders
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.button_stroke)
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

        attrs?.let {
            context.withStyledAttributes(it, R.styleable.ButtonView) {
                val strokeColor = getColor(
                    R.styleable.ButtonView_strokeColor,
                    ContextCompat.getColor(context, R.color.button_stroke)
                )
                val strokeWidth = getDimension(R.styleable.ButtonView_strokeWidth, 8f)

                strokePaint.color = strokeColor
                strokePaint.strokeWidth = strokeWidth

            }
        }
    }

    // play tone using TonePlayer (AudioTrack)
    private fun playSound() {
        if (soundEnabled) {
            val volume = GameManager(context).soundVolume / 100f
            TonePlayer.playTone(frequency, 150, volume)
        }
    }

    // sets whether the button should play a sound
    fun setSoundEnabled(enabled: Boolean) {
        this.soundEnabled = enabled
    }

    // sets the frequency for the button's tone
    fun setFrequency(freq: Double) {
        this.frequency = freq
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
        val x = event.x
        val y = event.y
        val isInside = x >= 0 && x <= width && y >= 0 && y <= height

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isPressedState = true
                invalidate()
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (isPressedState != isInside) {
                    isPressedState = isInside
                    invalidate()
                }
                return true
            }

            // tone starts only on release inside
            MotionEvent.ACTION_UP -> {
                if (isInside) {
                    playSound()
                    performClick()
                }
                isPressedState = false
                invalidate()
                return true
            }

            MotionEvent.ACTION_CANCEL -> {
                isPressedState = false
                invalidate()
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
