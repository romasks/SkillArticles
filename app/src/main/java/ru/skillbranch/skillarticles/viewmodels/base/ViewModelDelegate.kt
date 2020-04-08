package ru.skillbranch.skillarticles.viewmodels.base

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import ru.skillbranch.skillarticles.viewmodels.ArticleViewModel
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewModelDelegate<T : ViewModel>(private val clazz: Class<T>, private val arg: Any?) :
  ReadOnlyProperty<FragmentActivity, T> {

  private var value: T? = null

  override fun getValue(thisRef: FragmentActivity, property: KProperty<*>): T {
    if (value == null) {
      if (clazz.isAssignableFrom(ArticleViewModel::class.java)) value = ArticleViewModel(arg as String) as T
      else throw IllegalArgumentException("Unknown ViewModel class")
    }
    return value!!
  }
}
