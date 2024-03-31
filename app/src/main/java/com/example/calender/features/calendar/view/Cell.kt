package com.example.calender.features.calendar.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.Gravity
import android.widget.TextView
import kotlin.properties.Delegates

class Cell(context: Context) : androidx.appcompat.widget.AppCompatTextView(context) {

    var size by Delegates.notNull<Int>()
    var date:String = ""

    val roundRectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    var rectColor:Int? = null
        set(value) {
            roundRectPaint.color = value ?: Color.GRAY
            field = value
        }

    init {
        textAlignment = TEXT_ALIGNMENT_GRAVITY
        gravity = Gravity.CENTER
        setTextColor(Color.BLACK)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //super.onMeasure(widthMeasureSpec, widthMeasureSpec)
        setMeasuredDimension(size,size)
    }

    override fun onDraw(canvas: Canvas) {
        with(canvas) {
            if (rectColor != null) {

                drawRoundRect(
                    5f,
                    5f,
                    97f,
                    97f,
                    10f,
                    10f,
                    roundRectPaint
                )
                paint.textAlign = Paint.Align.CENTER
            }

        drawText(date, width / 2f, height / 2f, paint)
    }
        super.onDraw(canvas)
    }


}