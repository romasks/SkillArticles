package ru.skillbranch.skillarticles.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.shape.MaterialShapeDrawable
import ru.skillbranch.skillarticles.databinding.LayoutBottombarBinding
import ru.skillbranch.skillarticles.ui.custom.behaviors.BottombarBehavior

class Bottombar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), CoordinatorLayout.AttachedBehavior {

    private var binding = LayoutBottombarBinding.inflate(LayoutInflater.from(context), this)

    val btnLike
        get() = binding.btnLike

    val btnBookmark
        get() = binding.btnBookmark

    val btnShare
        get() = binding.btnShare

    val btnSettings
        get() = binding.btnSettings

    init {
//        View.inflate(context, R.layout.layout_bottombar, this)
        // add material bg for handle elevation and color surface
        val materialBg = MaterialShapeDrawable.createWithElevationOverlay(context)
        materialBg.elevation = elevation
        background = materialBg
    }

    override fun getBehavior(): CoordinatorLayout.Behavior<*> = BottombarBehavior()
}