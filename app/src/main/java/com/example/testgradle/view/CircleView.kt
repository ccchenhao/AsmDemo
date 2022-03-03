package com.example.testgradle.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.testgradle.utils.toPx1

class CircleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    val drawable:Drawable=ColorDrawable(Color.GREEN)
    var radius=50f.toPx1()
        //java中是setRadius()
        set(value) {
            field=value
            invalidate()
        }
    val paint=Paint().apply{
        color = Color.RED
    }
    var sdf=50f.toPx1()
        //java中是setRadius()
        set(value) {
            Log.d("chlog","111"+value)
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.d("chlog","CircleView onMeasure")
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun layout(l: Int, t: Int, r: Int, b: Int) {
        super.layout(l, t, r, b)
        Log.d("chlog","CircleView onLayout")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(width/2f,height/2f,radius,paint)
        drawable.bounds= Rect(0,0,100f.toPx1().toInt(),100f.toPx1().toInt())
        drawable.draw(canvas)
        drawable.bounds= Rect(0,0,200f.toPx1().toInt(),200f.toPx1().toInt())
    }

}
