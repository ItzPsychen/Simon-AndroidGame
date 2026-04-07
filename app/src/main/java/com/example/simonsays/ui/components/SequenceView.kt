package com.example.simonsays.ui.components

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class SequenceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val sequence = mutableListOf<Pair<String, Int>>()
    
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.LEFT
        textSize = 60f
        typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
    }

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        alpha = 100
    }

    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 60f
        typeface = Typeface.create("sans-serif-medium", Typeface.BOLD)
    }

    fun addElement(label: String, color: Int) {
        sequence.add(label to color)
        invalidate()
    }

    // used when pressed "Cancel"
    fun clear() {
        sequence.clear()
        invalidate()
    }

    fun getSequenceData(): List<Pair<String, Int>> {
        return sequence.toList()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // background rectangle (even if empty)
        val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        val cornerRadius = 40f
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, bgPaint)

        if (sequence.isEmpty()) return

        val centerY = height / 2f - (textPaint.descent() + textPaint.ascent()) / 2f
        val padding = 40f
        val comma = ", "
        val ellipsis = "... "

        // some width information
        val maxWidth = width.toFloat() - padding * 2
        val commaWidth = textPaint.measureText(comma)
        val ellipsisWidth = dotPaint.measureText(ellipsis)
        var totalWidth = 0f
        var visibleCount = 0

        // calculate max letters (before the "...")
        for (i in sequence.indices.reversed()) {
            val itemWidth = textPaint.measureText(sequence[i].first)
            val needed = itemWidth + (if (i < sequence.size - 1) commaWidth else 0f)
            if (totalWidth + needed + ellipsisWidth > maxWidth && i > 0) break
            totalWidth += needed
            visibleCount++
        }

        var currentX = padding
        if (visibleCount < sequence.size) {
            canvas.drawText(ellipsis, currentX, centerY, dotPaint)
            currentX += ellipsisWidth
        }

        val startIndex = sequence.size - visibleCount
        for (i in startIndex until sequence.size) {
            val item = sequence[i]
            textPaint.color = item.second
            canvas.drawText(item.first, currentX, centerY, textPaint)
            currentX += textPaint.measureText(item.first)
            
            if (i < sequence.size - 1) {
                textPaint.color = Color.WHITE
                canvas.drawText(comma, currentX, centerY, textPaint)
                currentX += commaWidth
            }
        }
    }
}
