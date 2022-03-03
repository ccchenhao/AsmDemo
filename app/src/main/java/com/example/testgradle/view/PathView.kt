package com.example.testgradle.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import com.example.testgradle.utils.toPx1

class PathView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    val arcRadius = 150f.toPx1()
    val openAngle = 120
    val lineRadius = 120f.toPx1()
    val path = Path().apply {
        fillType = Path.FillType.EVEN_ODD
    }
    val dash = Path()
    val DASH_WIDTH = 2f.toPx1()
    val DASH_LENTH = 10f.toPx1()
    val paint = Paint().apply {
        strokeWidth = 3f.toPx1()
        style = Paint.Style.STROKE
        dash.addRect(0f, 0f, DASH_WIDTH, DASH_LENTH, Path.Direction.CCW)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        path.reset()
        path.addArc(
            width / 2 - arcRadius,
            height / 2 - arcRadius,
            width / 2 + arcRadius,
            height / 2 + arcRadius,
            90f + openAngle / 2,
            360f - openAngle
        )
        pathMeasure.setPath(path, false)
        //必须减去一个间隔，因为最后一个正好画完，就不添加了，
        //所以留下一个DASH_WIDTH正好让他画出就是结尾了
        pathEffect = PathDashPathEffect(
            dash,
            (pathMeasure.length - DASH_WIDTH) / 20f,
            0f,
            PathDashPathEffect.Style.MORPH
        )
    }

    val pathMeasure = PathMeasure()
    private lateinit var pathEffect: PathDashPathEffect
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
        paint.pathEffect = pathEffect
        canvas.drawPath(path, paint)
        paint.pathEffect = null
        canvas.drawLine(
            width / 2f, height / 2f,
            (width / 2f + lineRadius * Math.cos(Math.toRadians((150 + 5 * (360 - 120) / 20).toDouble()))).toFloat(),
            (height / 2f + lineRadius * Math.sin(Math.toRadians((150 + 5 * (360 - 120) / 20).toDouble()))).toFloat(),
            paint
        )
    }
}