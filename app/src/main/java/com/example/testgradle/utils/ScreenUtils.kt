package com.example.testgradle.utils

import android.content.res.Resources
import android.util.TypedValue

fun Float.toPx1() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this,
    Resources.getSystem().displayMetrics
)