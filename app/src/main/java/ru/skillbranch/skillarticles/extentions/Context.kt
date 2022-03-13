package ru.skillbranch.skillarticles.extentions

import android.content.Context
import android.util.TypedValue

fun Context.dpToPx(dp: Int): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    dp.toFloat(),
    this.resources.displayMetrics
)

fun Context.dpToIntPx(dp: Int): Int = dpToPx(dp).toInt()
