package ru.skillbranch.skillarticles.ui

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
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

private const val KEY_IS_SEARCH_OPEN = "IS_SEARCH_OPEN"
private const val KEY_SEARCHED_TEXT = "SEARCHED_TEXT"

class RootActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding
    private lateinit var viewModel: ArticleViewModel

    private var isSearchOpen = false
    private var searchedText = ""

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.root_menu, menu)

        val menuItem = menu?.findItem(R.id.action_search)
        val searchView = menuItem?.actionView as? SearchView

        searchView?.apply {
            queryHint = getString(R.string.search_hint)
            inputType = InputType.TYPE_CLASS_TEXT
            imeOptions = EditorInfo.IME_ACTION_SEARCH
        }

        searchView?.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)?.apply {
            setTextColor(getColor(R.color.color_on_article_bar))
            setHintTextColor(getColor(R.color.color_gray))
        }

        if (isSearchOpen) {
            menuItem?.expandActionView()
            searchView?.setQuery(searchedText, false)
        }

        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchedText = newText ?: ""
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_search) {
            with(binding.toolbar) {
                isSearchOpen = !isSearchOpen
                logo.setVisible(!isSearchOpen, false)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(KEY_IS_SEARCH_OPEN, isSearchOpen)
        outState.putString(KEY_SEARCHED_TEXT, searchedText)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        isSearchOpen = savedInstanceState.getBoolean(KEY_IS_SEARCH_OPEN)
        searchedText = savedInstanceState.getString(KEY_SEARCHED_TEXT) ?: ""
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
            btnTextUp.setOnClickListener { viewModel.handleUpText() }
            btnTextDown.setOnClickListener { viewModel.handleDownText() }
            switchMode.setOnClickListener { viewModel.handleNightMode() }
        }
    }
}