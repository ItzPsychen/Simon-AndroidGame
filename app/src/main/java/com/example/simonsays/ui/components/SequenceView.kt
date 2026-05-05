package com.example.simonsays.ui.components

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import com.example.simonsays.R

class SequenceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val sequence = mutableListOf<Pair<String, Int>>()
    private var sequenceAlpha = 255
    private var clearAnimator: ValueAnimator? = null

    // text for the sequence
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.LEFT
        textSize = 60f
        typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
    }

    // applies the background color
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    // stroke for borders like the buttons
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.button_stroke)
        strokeWidth = context.resources.getDimension(R.dimen.stroke_width)
    }

    // placeholder that says "Press the buttons"
    private val placeholderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        alpha = 100
        textSize = 50f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("sans-serif-medium", Typeface.ITALIC)
    }

    // add element to the sequence
    fun addElement(label: String, color: Int) {
        // Cancel any ongoing clear animation and reset alpha
        clearAnimator?.cancel()
        sequenceAlpha = 255
        
        sequence.add(label to color)
        invalidate()
    }

    // clears the sequence making the text fading away
    fun clear(fadeAway: Boolean = false) {
        clearAnimator?.cancel()

        if (fadeAway && sequence.isNotEmpty()) {
            clearAnimator = ValueAnimator.ofInt(255, 0).apply {
                duration = 300
                addUpdateListener { animator ->
                    sequenceAlpha = animator.animatedValue as Int
                    invalidate()
                }
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        sequence.clear()
                        sequenceAlpha = 255
                        clearAnimator = null
                        invalidate()
                    }
                })
                start()
            }
        } else {
            sequence.clear()
            sequenceAlpha = 255
            clearAnimator = null
            invalidate()
        }
    }

    // return the sequence data
    fun getSequenceData(): List<Pair<String, Int>> {
        return sequence.toList()
    }

    // draws the sequence with the background
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // update stroke color
        strokePaint.color = ContextCompat.getColor(context, R.color.button_stroke)

        val typedValue = TypedValue()
        context.theme.resolveAttribute(com.google.android.material.R.attr.colorSurface, typedValue, true)
        bgPaint.color = typedValue.data

        // 51 over 256 (20%)
        bgPaint.alpha = 51

        val inset = strokePaint.strokeWidth / 2f
        val rect = RectF(inset, inset, width.toFloat() - inset, height.toFloat() - inset)
        val cornerRadius = 40f

        // draw the label for the sequence
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, bgPaint)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, strokePaint)

        val centerY = height / 2f - (textPaint.descent() + textPaint.ascent()) / 2f
        val centerX = width / 2f

        // show placeholder or sequence
        if (sequence.isEmpty()) {
            val placeholder = context.getString(R.string.placeholder_empty)
            canvas.drawText(placeholder, centerX, centerY, placeholderPaint)
        } else {
            drawSequence(canvas, centerY)
        }
    }

    // calculate the max letters visible and draw the sequence
    private fun drawSequence(canvas: Canvas, centerY: Float) {
        val padding = 40f
        val comma = ", "
        val ellipsis = "... "
        val textPaintLocal = Paint(textPaint)

        val maxWidth = width.toFloat() - padding * 2
        val commaWidth = textPaintLocal.measureText(comma)
        val ellipsisWidth = textPaintLocal.measureText(ellipsis)
        var totalWidth = 0f
        var visibleCount = 0

        for (i in sequence.indices.reversed()) {
            val itemWidth = textPaintLocal.measureText(sequence[i].first)
            val needed = itemWidth + (if (i < sequence.size - 1) commaWidth else 0f)
            if (totalWidth + needed + ellipsisWidth > maxWidth && i > 0) break
            totalWidth += needed
            visibleCount++
        }

        var currentX = padding
        if (visibleCount < sequence.size) {
            textPaintLocal.color = Color.WHITE
            textPaintLocal.alpha = sequenceAlpha
            canvas.drawText(ellipsis, currentX, centerY, textPaintLocal)
            currentX += ellipsisWidth
        }

        val startIndex = sequence.size - visibleCount
        for (i in startIndex until sequence.size) {
            val item = sequence[i]
            textPaintLocal.color = item.second
            textPaintLocal.alpha = sequenceAlpha
            canvas.drawText(item.first, currentX, centerY, textPaintLocal)
            currentX += textPaintLocal.measureText(item.first)
            
            if (i < sequence.size - 1) {
                textPaintLocal.color = Color.WHITE
                textPaintLocal.alpha = sequenceAlpha
                canvas.drawText(comma, currentX, centerY, textPaintLocal)
                currentX += commaWidth
            }
        }
    }
}
