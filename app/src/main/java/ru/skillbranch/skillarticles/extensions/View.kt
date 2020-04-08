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
