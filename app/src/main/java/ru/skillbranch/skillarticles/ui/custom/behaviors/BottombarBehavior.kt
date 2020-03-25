package ru.skillbranch.skillarticles.ui.custom.behaviors

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import ru.skillbranch.skillarticles.ui.custom.Bottombar
import java.lang.Float.max
import java.lang.Float.min

class BottombarBehavior : CoordinatorLayout.Behavior<Bottombar>() {

  override fun onStartNestedScroll(
    coordinatorLayout: CoordinatorLayout, child: Bottombar, directTargetChild: View, target: View, axes: Int, type: Int
  ): Boolean {
    return axes == ViewCompat.SCROLL_AXIS_VERTICAL
  }

  @RequiresApi(Build.VERSION_CODES.N)
  override fun onNestedPreScroll(
    coordinatorLayout: CoordinatorLayout, child: Bottombar, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int
  ) {
    child.translationY = max(0f, min(child.height.toFloat(), child.translationY + dy))
    super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
  }
}
