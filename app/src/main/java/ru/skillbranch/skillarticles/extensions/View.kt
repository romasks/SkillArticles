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
