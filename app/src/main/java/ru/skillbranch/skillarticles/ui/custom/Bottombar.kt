package ru.skillbranch.skillarticles.ui.custom

import android.animation.Animator
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.ViewAnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import com.google.android.material.shape.MaterialShapeDrawable
import kotlinx.android.synthetic.main.layout_bottom_bar.view.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.ui.custom.behaviors.BottombarBehavior
import kotlin.math.hypot

class Bottombar @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), CoordinatorLayout.AttachedBehavior {

  var isSearchMode = false

  init {
    View.inflate(context, R.layout.layout_bottom_bar, this)
    val materialBg = MaterialShapeDrawable.createWithElevationOverlay(context)
    materialBg.elevation = elevation
    background = materialBg
  }

  override fun getBehavior(): CoordinatorLayout.Behavior<*> = BottombarBehavior()

  // save state
  override fun onSaveInstanceState(): Parcelable? {
    val savedState = SavedState(super.onSaveInstanceState())
    savedState.ssIsSearchMode = isSearchMode
    return savedState
  }

  // restore state
  override fun onRestoreInstanceState(state: Parcelable) {
    super.onRestoreInstanceState(state)
    if (state is SavedState) {
      isSearchMode = state.ssIsSearchMode
      reveal.isVisible = isSearchMode
      group_bottom.isVisible = !isSearchMode
    }
  }

  fun setSearchState(search: Boolean) {
    if (isSearchMode == search || !isAttachedToWindow) return
    isSearchMode = search
    if (isSearchMode) animateShowSearchPanel()
    else animateHideSearchPanel()
  }

  fun bindSearchInfo(searchCount: Int = 0, position: Int = 0) {
    tv_search_result.text = if (searchCount == 0) "Not found" else "${position.inc()} of $searchCount"
    btn_result_up.isEnabled = searchCount != 0 && position != 0
    btn_result_down.isEnabled = searchCount != 0 && position != searchCount - 1
  }

  private fun animateShowSearchPanel() {
    reveal.isVisible = true
    createAnimator(true).apply {
      doOnEnd { group_bottom.isVisible = false }
      start()
    }
  }

  private fun animateHideSearchPanel() {
    group_bottom.isVisible = true
    createAnimator(false).apply {
      doOnEnd { reveal.isVisible = false }
      start()
    }
  }

  private fun createAnimator(show: Boolean): Animator {
    hypot(width.toFloat(), height / 2f).also {
      val startRadius = if (show) 0f else it
      val endRadius = if (show) it else 0f
      return ViewAnimationUtils.createCircularReveal(reveal, width, height / 2, startRadius, endRadius)
    }
  }

  private class SavedState : BaseSavedState, Parcelable {
    var ssIsSearchMode: Boolean = false

    constructor(superState: Parcelable?) : super(superState)

    constructor(src: Parcel) : super(src) {
      ssIsSearchMode = src.readInt() == 1
    }

    override fun writeToParcel(dst: Parcel, flags: Int) {
      super.writeToParcel(dst, flags)
      dst.writeInt(if (ssIsSearchMode) 1 else 0)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<SavedState> {
      override fun createFromParcel(parcel: Parcel) = SavedState(parcel)
      override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
    }
  }
}
