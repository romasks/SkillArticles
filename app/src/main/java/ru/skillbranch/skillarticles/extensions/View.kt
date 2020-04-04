package ru.skillbranch.skillarticles.extensions

import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.widget.NestedScrollView

fun NestedScrollView.setMarginOptionally(
  left: Int = marginLeft,
  top: Int = marginTop,
  right: Int = marginRight,
  bottom: Int = marginBottom
) {
  this.left = left
  this.top = top
  this.right = right
  this.bottom = bottom
}

fun String.indexesOf(substr: String, ignoreCase: Boolean = true) : List<Int> {
  val content = if (ignoreCase) toLowerCase() else this
  return Regex(substr).findAll(content).map { it.range.first }.toList().filter { it != -1 }
}
