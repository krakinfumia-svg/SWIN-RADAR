
package com.swin.radar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import kotlin.math.sqrt

class RadarView(context: Context) : View(context) {

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(12, 28, 25)
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(49, 116, 88)
        style = Paint.Style.STROKE
        strokeWidth = 1.5f
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(160, 225, 190)
        textSize = 12f
        textAlign = Paint.Align.CENTER
    }

    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    var objects: List<RadarObject> = emptyList()
        set(value) {
            field = value
            invalidate()
        }

    var zoom: Float = 1f
        set(value) {
            field = value.coerceIn(0.5f, 2f)
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width / 2f
        val cy = height / 2f
        val radius = minOf(width, height) / 2f * 0.88f

        if (radius <= 0f) return

        canvas.drawCircle(cx, cy, radius, backgroundPaint)

        canvas.save()

        val clip = android.graphics.Path().apply {
            addCircle(
                cx, cy, radius,
                android.graphics.Path.Direction.CW
            )
        }

        canvas.clipPath(clip)

        for (factor in floatArrayOf(0.25f, 0.5f, 0.75f, 1f)) {
            canvas.drawCircle(
                cx, cy, radius * factor, gridPaint
            )
        }

        canvas.drawLine(
            cx - radius, cy, cx + radius, cy, gridPaint
        )

        canvas.drawLine(
            cx, cy - radius, cx, cy + radius, gridPaint
        )

        objects.forEach { item ->
            val px = cx + item.x / 100f * radius * zoom
            val py = cy + item.y / 100f * radius * zoom

            val dx = px - cx
            val dy = py - cy

            if (sqrt(dx * dx + dy * dy) <= radius - 4f) {

                dotPaint.color = when (item.type) {
                    "player" -> Color.WHITE
                    "bot" -> Color.YELLOW
                    "loot" -> Color.CYAN
                    "crate" -> Color.rgb(255, 145, 75)
                    else -> Color.GRAY
                }

                if (item.type == "loot" || item.type == "crate") {
                    canvas.drawRect(
                        RectF(px - 5f, py - 5f, px + 5f, py + 5f),
                        dotPaint
                    )
                } else {
                    canvas.drawCircle(px, py, 6f, dotPaint)
                }
            }
        }

        dotPaint.color = Color.WHITE
        canvas.drawCircle(cx, cy, 7f, dotPaint)

        canvas.restore()

        canvas.drawText(
            "N", cx, cy - radius - 7f, textPaint
        )
    }
}

