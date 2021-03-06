package ru.skillbranch.skillarticles.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.Notify
import ru.skillbranch.skillarticles.viewmodels.base.ViewModelDelegate

abstract class BaseActivity<T : BaseViewModel<out IViewModelState>> : AppCompatActivity() {

  protected abstract val binding: Binding
  protected abstract val viewModel: T
  protected abstract val layout: Int

  abstract fun setupViews()
  abstract fun renderNotification(notify: Notify)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(layout)
    setupViews()

    binding.onFinishInflate()
    viewModel.observeState(this) { binding.bind(it) }
    viewModel.observeNotifications(this) { renderNotification(it) }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    viewModel.saveState(outState)
    binding.saveUi(outState)
    super.onSaveInstanceState(outState)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
    viewModel.restoreState(savedInstanceState)
    binding.restoreUi(savedInstanceState)
  }

  internal inline fun <reified T: ViewModel> provideViewModel(arg: Any?) : ViewModelDelegate<T> {
    return ViewModelDelegate(T::class.java, arg)
  }
}
