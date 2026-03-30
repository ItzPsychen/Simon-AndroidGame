package com.example.simonsays.ui.components

import com.example.simonsays.model.SimonColor

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin

import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColorInt

class SimonButtonsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE

        // border color and width
        color = "#333333".toColorInt()
        strokeWidth = 6f
    }

    // text in buttons info
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
        textSize = 70f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val colors = SimonColor.entries.toTypedArray()
    private val buttonPaths = Array(colors.size) { Path() }
    private val labelPositions = Array(colors.size) { PointF() }

    // pixel gap between buttons
    private val gapWidth = 16f
    private val innerRadiusRatio = 0.45f

    // transparency (slightly visible bg)
    private val buttonAlpha = 0.5f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculatePaths(w.toFloat(), h.toFloat())
    }

    private fun calculatePaths(w: Float, h: Float) {
        val centerX = w / 2
        val centerY = h / 2
        val outerRadius = w.coerceAtMost(h) / 2 * 0.85f
        val innerRadius = outerRadius * innerRadiusRatio
        
        val sectionAngle = 360f / colors.size

        for (i in colors.indices) {
            val path = buttonPaths[i]
            path.reset()

            // angles of the gap centers
            val startGapCenterAngle = i * sectionAngle - 90f
            val endGapCenterAngle = (i + 1) * sectionAngle - 90f
            
            // offsets of the angles (for constant gap)
            val startOffsetOuter = Math.toDegrees(asin((gapWidth / 2) / outerRadius).toDouble()).toFloat()
            val startOffsetInner = Math.toDegrees(asin((gapWidth / 2) / innerRadius).toDouble()).toFloat()
            val endOffsetOuter = Math.toDegrees(asin((gapWidth / 2) / outerRadius).toDouble()).toFloat()
            val endOffsetInner = Math.toDegrees(asin((gapWidth / 2) / innerRadius).toDouble()).toFloat()

            val rectOuter = RectF(centerX - outerRadius, centerY - outerRadius, centerX + outerRadius, centerY + outerRadius)
            val rectInner = RectF(centerX - innerRadius, centerY - innerRadius, centerX + innerRadius, centerY + innerRadius)

            // outer arc
            val outerStartAngle = startGapCenterAngle + startOffsetOuter
            val outerSweepAngle = (endGapCenterAngle - endOffsetOuter) - outerStartAngle
            path.arcTo(rectOuter, outerStartAngle, outerSweepAngle)

            // inner arc
            val innerEndAngle = endGapCenterAngle - endOffsetInner
            path.lineTo(
                centerX + innerRadius * cos(Math.toRadians(innerEndAngle.toDouble())).toFloat(),
                centerY + innerRadius * sin(Math.toRadians(innerEndAngle.toDouble())).toFloat()
            )

            // inner arc (backwards)
            val innerStartAngle = startGapCenterAngle + startOffsetInner
            val innerSweepAngle = innerStartAngle - innerEndAngle
            path.arcTo(rectInner, innerEndAngle, innerSweepAngle)

            path.close()

            // label position calculator
            val midAngle = Math.toRadians((startGapCenterAngle + endGapCenterAngle) / 2.0)
            val labelRadius = (outerRadius + innerRadius) / 2
            labelPositions[i].set(
                centerX + labelRadius * cos(midAngle).toFloat(),
                centerY + labelRadius * sin(midAngle).toFloat() - (textPaint.descent() + textPaint.ascent()) / 2
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (i in colors.indices) {
            val color = colors[i]
            val path = buttonPaths[i]
            
            // draw fill, border, label
            fillPaint.color = ColorUtils.setAlphaComponent(color.colorRes, (buttonAlpha * 255).toInt())
            canvas.drawPath(path, fillPaint)
            canvas.drawPath(path, strokePaint)
            canvas.drawText(color.label, labelPositions[i].x, labelPositions[i].y, textPaint)
        }
    }

    init {
        // set the layer type (safe way to not mix)
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }
}
