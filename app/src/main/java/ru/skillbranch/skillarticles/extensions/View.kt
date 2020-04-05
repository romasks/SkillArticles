package ru.skillbranch.skillarticles.extensions

import androidx.coordinatorlayout.widget.CoordinatorLayout
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
  val lp = layoutParams as CoordinatorLayout.LayoutParams
  lp.setMargins(left, top, right, bottom)
  layoutParams = lp
}

fun String.indexesOf(substr: String, ignoreCase: Boolean = true) : List<Int> {
  if (substr.isEmpty()) return emptyList()
  val content = if (ignoreCase) toLowerCase() else this
  return Regex(substr).findAll(content).map { it.range.first }.toList().filter { it != -1 }
}
