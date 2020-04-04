package ru.skillbranch.skillarticles.ui

import android.os.Bundle
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.text.getSpans
import androidx.core.view.MenuItemCompat
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.android.synthetic.main.layout_bottom_bar.*
import kotlinx.android.synthetic.main.layout_submenu.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.setMarginOptionally
import ru.skillbranch.skillarticles.ui.base.BaseActivity
import ru.skillbranch.skillarticles.ui.custom.SearchFocusSpan
import ru.skillbranch.skillarticles.ui.custom.SearchSpan
import ru.skillbranch.skillarticles.ui.delegates.AttrValue
import ru.skillbranch.skillarticles.viewmodels.ArticleState
import ru.skillbranch.skillarticles.viewmodels.ArticleViewModel
import ru.skillbranch.skillarticles.viewmodels.base.Notify
import ru.skillbranch.skillarticles.viewmodels.base.ViewModelFactory

class RootActivity : BaseActivity<ArticleViewModel>(), IArticleView {

  override lateinit var viewModel: ArticleViewModel

  private var isSearchMode: Boolean = false
  private var queryString: String? = null

  private val bgColor by AttrValue(R.attr.colorSecondary)
  private val fgColor by AttrValue(R.attr.colorOnSecondary)

  override var layout = R.layout.activity_root

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val vmFactory = ViewModelFactory("0")
    viewModel = ViewModelProviders.of(this, vmFactory).get(ArticleViewModel::class.java)
    viewModel.observeState(this) {
      renderUi(it)
    }
    viewModel.observeNotifications(this) {
      renderNotification(it)
    }
  }

  override fun setupViews() {
    setupToolbar()
    setupBottombar()
    setupSubmenu()
  }

  override fun renderSearchResult(searchResult: List<Pair<Int, Int>>) {
    val content = tv_text_content.text as Spannable

    clearSearchResult()

    searchResult.forEach { (start, end) ->
      content.setSpan(
        SearchSpan(bgColor, fgColor),
        start,
        end,
        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
      )
    }

    renderSearchPosition(0)
  }

  override fun renderSearchPosition(searchPosition: Int) {
    val content = tv_text_content.text as Spannable

    val spans = content.getSpans<SearchSpan>()
    content.getSpans<SearchFocusSpan>().forEach { content.removeSpan(it) }

    if (spans.isNotEmpty()) {
      val result = spans[searchPosition]
      Selection.setSelection(content, content.getSpanStart(result))
      content.setSpan(
        SearchFocusSpan(bgColor, fgColor),
        content.getSpanStart(result),
        content.getSpanEnd(result),
        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
      )
    }
  }

  override fun clearSearchResult() {
    val content = tv_text_content.text as Spannable
    content.getSpans<SearchSpan>().forEach { content.removeSpan(it) }
  }

  override fun showSearchBar() {
    bottombar.setSearchState(true)
    scroll.setMarginOptionally(bottom = dpToIntPx(56))
  }

  override fun hideSearchBar() {
    bottombar.setSearchState(false)
    scroll.setMarginOptionally(bottom = dpToIntPx(0))
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.menu_search, menu)
    val searchItem = menu?.findItem(R.id.action_search)
    val searchView = MenuItemCompat.getActionView(searchItem) as SearchView
    searchView.queryHint = getString(R.string.article_search_placeholder)

    if (isSearchMode) {
      searchItem?.expandActionView()
      searchView.setQuery(queryString, false)
      searchView.clearFocus()
    }

    searchItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
      override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
        viewModel.handleSearchMode(true)
        return true
      }

      override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
        viewModel.handleSearchMode(false)
        return true
      }
    })

    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(query: String?): Boolean {
        viewModel.handleSearch(query)
        return true
      }

      override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.handleSearch(newText)
        return true
      }
    })

    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      android.R.id.home -> {
        onBackPressed()
      }
      R.id.action_search -> {
        Toast.makeText(this, "Search Selected", Toast.LENGTH_SHORT).show()
      }
    }
    return true
  }

  override fun onBackPressed() {
    Toast.makeText(this, "Back", Toast.LENGTH_SHORT).show()
  }

  private fun setupToolbar() {
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    val logo = if (toolbar.childCount > 2) toolbar.getChildAt(2) as ImageView else null
    logo?.scaleType = ImageView.ScaleType.CENTER_CROP

    (logo?.layoutParams as Toolbar.LayoutParams).let {
      it.width = this.dpToIntPx(40)
      it.height = this.dpToIntPx(40)
      it.marginEnd = this.dpToIntPx(16)
      logo.layoutParams = it
    }
  }

  private fun setupSubmenu() {
    btn_text_up.setOnClickListener { viewModel.handleUpText() }
    btn_text_down.setOnClickListener { viewModel.handleDownText() }
    switch_mode.setOnClickListener { viewModel.handleNightMode() }
  }

  private fun setupBottombar() {
    btn_like.setOnClickListener { viewModel.handleLike() }
    btn_bookmark.setOnClickListener { viewModel.handleBookmark() }
    btn_share.setOnClickListener { viewModel.handleShare() }
    btn_settings.setOnClickListener { viewModel.handleToggleMenu() }

    btn_result_up.setOnClickListener {
      viewModel.handleUpResult()
    }
    btn_result_down.setOnClickListener {
      viewModel.handleDownResult()
    }
    btn_search_close.setOnClickListener {
      viewModel.handleSearchMode(false)
      invalidateOptionsMenu()
    }
  }

  private fun renderUi(data: ArticleState) {
    // bind search view
    isSearchMode = data.isSearch
    queryString = data.searchQuery
    if (data.isSearch) showSearchBar() else hideSearchBar()
    if (data.searchResult.isNotEmpty()) renderSearchResult(data.searchResult)
    if (data.searchResult.isNotEmpty()) renderSearchPosition(data.searchPosition)
    bottombar.bindSearchInfo(data.searchResult.size, data.searchPosition)

    // bind submenu state
    btn_settings.isChecked = data.isShowMenu
    if (data.isShowMenu) submenu.open() else submenu.close()

    // bind article personal data
    btn_like.isChecked = data.isLike
    btn_bookmark.isChecked = data.isBookmark

    // bind submenu views
    switch_mode.isChecked = data.isDarkMode
    delegate.localNightMode =
      if (data.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO

    if (data.isBigText) {
      tv_text_content.textSize = 18f
      btn_text_up.isChecked = true
      btn_text_down.isChecked = false
    } else {
      tv_text_content.textSize = 14f
      btn_text_up.isChecked = false
      btn_text_down.isChecked = true
    }

    // bind content
    if (data.isLoadingContent) {
      tv_text_content.text = "loading"
    } else if (tv_text_content.text == "loading") {
      val content = data.content.first() as String
      tv_text_content.setText(content, TextView.BufferType.SPANNABLE)
      tv_text_content.movementMethod = ScrollingMovementMethod()
    }

    // bind toolbar
    toolbar.title = data.title ?: "Skill Articles"
    toolbar.subtitle = data.category ?: "loading..."
    if (data.categoryIcon != null) toolbar.logo = getDrawable(data.categoryIcon as Int)
  }

  private fun renderNotification(notify: Notify) {
    val snackbar = Snackbar.make(coordinator_container, notify.message, Snackbar.LENGTH_LONG)
      .setAnchorView(bottombar)

    when (notify) {
      is Notify.TextMessage -> {
        /*nothing*/
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
          setBackgroundTint(getColor(R.color.design_default_color_error))
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
}
