// --- Archivo: DonutChartView.kt ---
package com.asipion.pfmoviles

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class DonutChartView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = RectF()
    private var data: List<Pair<Float, Int>> = emptyList() // Pares de (porcentaje, color)

    fun setData(newData: List<Pair<Float, Int>>) {
        this.data = newData
        invalidate() // Le dice a la vista que necesita redibujarse
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (data.isEmpty()) {
            return
        }

        val width = width.toFloat()
        val height = height.toFloat()
        val strokeWidth = 40f // Grosor del anillo del gráfico
        val radius = (Math.min(width, height) / 2f) - (strokeWidth / 2f)

        rect.set(
            width / 2 - radius,
            height / 2 - radius,
            width / 2 + radius,
            height / 2 + radius
        )

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        paint.strokeCap = Paint.Cap.ROUND // Extremos redondeados para un look más moderno

        var startAngle = -90f // Empezar desde la parte superior del círculo

        data.forEach { (percentage, color) ->
            paint.color = color
            val sweepAngle = 360 * (percentage / 100f)
            canvas.drawArc(rect, startAngle, sweepAngle, false, paint)
            startAngle += sweepAngle
        }
    }
}