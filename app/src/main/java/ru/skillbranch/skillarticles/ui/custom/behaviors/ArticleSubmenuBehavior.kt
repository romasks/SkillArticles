package ru.skillbranch.skillarticles.ui.custom.behaviors

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.marginRight
import ru.skillbranch.skillarticles.ui.custom.ArticleSubmenu
import ru.skillbranch.skillarticles.ui.custom.BottomBar

class ArticleSubmenuBehavior : CoordinatorLayout.Behavior<ArticleSubmenu>() {

    override fun layoutDependsOn(parent: CoordinatorLayout, child: ArticleSubmenu, dependency: View): Boolean {
        return dependency is BottomBar
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: ArticleSubmenu, dependency: View): Boolean {
        return if (dependency is BottomBar && dependency.translationY >= 0) {
            animate(child, dependency)
            true
        } else {
            false
        }
    }

    private fun animate(child: ArticleSubmenu, dependency: BottomBar) {
        val fraction = dependency.translationY / dependency.height
        child.translationX = (child.width + child.marginRight) * fraction
    }
}