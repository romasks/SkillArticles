package ru.skillbranch.skillarticles.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.databinding.ActivityRootBinding
import ru.skillbranch.skillarticles.extentions.dpToIntPx
import ru.skillbranch.skillarticles.vm.ArticleState
import ru.skillbranch.skillarticles.vm.ArticleViewModel
import ru.skillbranch.skillarticles.vm.Notify
import ru.skillbranch.skillarticles.vm.ViewModelFactory

class RootActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding
    private lateinit var viewModel: ArticleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRootBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        setupToolbar()
        setupBottomBar()
        setupSubmenu()

        val vmFactory = ViewModelFactory("0")
        viewModel = ViewModelProvider(this, vmFactory).get(ArticleViewModel::class.java)
        viewModel.observeState(this) {
            renderUi(it)
        }
        viewModel.observeNotifications(this) {
            renderNotifications(it)
        }
    }

    private fun renderUi(data: ArticleState) {
        val LOADING = "loading..."
        with(binding) {
            // toolbar
            toolbar.title = data.title ?: LOADING
            toolbar.subtitle = data.category ?: LOADING
            data.categoryIcon?.let { toolbar.logo = AppCompatResources.getDrawable(this@RootActivity, it as Int) }

            // content
            tvTextContent.text = if (data.isLoadingContent) LOADING else data.content.first() as String
            tvTextContent.textSize = if (data.isBigText) 18f else 14f

            // bottom bar
            with(bottombar) {
                btnLike.isChecked = data.isLike
                btnBookmark.isChecked = data.isBookmark
                btnSettings.isChecked = data.isShowMenu.also { if (it) submenu.open() else submenu.close() }
            }

            // submenu
            with(submenu) {
                btnTextUp.isChecked = data.isBigText
                btnTextDown.isChecked = data.isBigText.not()
                switchMode.isChecked = data.isDarkMode
                    .also { delegate.localNightMode = if (it) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO }
            }
        }
    }

    private fun renderNotifications(notify: Notify) {
        val snackbar = Snackbar.make(binding.coordinatorContainer, notify.message, Snackbar.LENGTH_LONG)
            .setAnchorView(binding.bottombar)

        when (notify) {
            is Notify.TextMessage -> {
                /* nothing */
            }

            is Notify.ActionMessage -> {
                with(snackbar) {
                    setActionTextColor(getColor(R.color.color_accent_dark))
                    setAction(notify.actionLabel) {
                        notify.actionHandler.invoke()
                    }
                }
            }

            is Notify.ErrorMessage -> {
                with(snackbar) {
                    setBackgroundTint(getColor(com.google.android.material.R.color.design_default_color_error))
                    setTextColor(getColor(android.R.color.white))
                    setActionTextColor(getColor(android.R.color.white))
                    setAction(notify.errLabel) {
                        notify.errHandler?.invoke()
                    }
                }
            }
        }

        snackbar.show()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val logo = if (toolbar.childCount > 2) toolbar.getChildAt(2) as ImageView else null
        logo?.scaleType = ImageView.ScaleType.CENTER_CROP

        (logo?.layoutParams as? Toolbar.LayoutParams)?.let {
            it.width = this.dpToIntPx(40)
            it.height = this.dpToIntPx(40)
            it.marginEnd = this.dpToIntPx(16)
            logo.layoutParams = it
        }
    }

    private fun setupBottomBar() {
        with(binding.bottombar) {
            btnLike.setOnClickListener { viewModel.handleLike() }
            btnBookmark.setOnClickListener { viewModel.handleBookmark() }
            btnShare.setOnClickListener { viewModel.handleShare() }
            btnSettings.setOnClickListener { viewModel.handleToggleMenu() }
        }
    }

    private fun setupSubmenu() {
        with(binding.submenu) {
            btnTextUp.setOnClickListener { viewModel.handleTextUp() }
            btnTextDown.setOnClickListener { viewModel.handleTextDown() }
            switchMode.setOnClickListener { viewModel.handleNightMode() }
        }
    }
}