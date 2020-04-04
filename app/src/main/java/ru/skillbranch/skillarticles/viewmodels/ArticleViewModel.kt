package ru.skillbranch.skillarticles.viewmodels

import android.os.Bundle
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.extensions.data.toAppSettings
import ru.skillbranch.skillarticles.extensions.data.toArticlePersonalInfo
import ru.skillbranch.skillarticles.extensions.format
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.Notify

class ArticleViewModel(private val articleId: String) : BaseViewModel<ArticleState>(ArticleState()), IArticleViewModel {

  private val repository = ArticleRepository

  init {
    subscribeOnDataSource(getArticleData()) { article, state ->
      article ?: return@subscribeOnDataSource null
      state.copy(
        shareLink = article.shareLink,
        title = article.title,
        category = article.category,
        categoryIcon = article.categoryIcon,
        date = article.date.format(),
        author = article.author
      )
    }

    subscribeOnDataSource(getArticleContent()) { content, state ->
      content ?: return@subscribeOnDataSource null
      state.copy(
        isLoadingContent = false,
        content = content
      )
    }

    subscribeOnDataSource(getArticlePersonalInfo()) { info, state ->
      info ?: return@subscribeOnDataSource null
      state.copy(
        isBookmark = info.isBookmark,
        isLike = info.isLike
      )
    }

    subscribeOnDataSource(repository.getAppSettings()) { settings, state ->
      state.copy(
        isDarkMode = settings.isDarkMode,
        isBigText = settings.isBigText
      )
    }
  }

  // load text from network
  private fun getArticleContent() = repository.loadArticleContent(articleId)

  // load data from mdb
  private fun getArticleData() = repository.getArticle(articleId)

  // load data from db
  private fun getArticlePersonalInfo() = repository.loadArticlePersonalInfo(articleId)

  override fun handleSearchMode(isSearch: Boolean) {
    updateState { it.copy(isSearch = isSearch) }
  }

  override fun handleSearchText(searchQuery: String?) {
    updateState { it.copy(searchQuery = searchQuery) }
  }

  override fun handleUpResult() {
  }

  override fun handleDownResult() {
  }

  override fun handleUpText() {
    repository.updateSettings(currentState.toAppSettings().copy(isBigText = true))
  }

  override fun handleDownText() {
    repository.updateSettings(currentState.toAppSettings().copy(isBigText = false))
  }

  override fun handleNightMode() {
    val settings = currentState.toAppSettings()
    repository.updateSettings(currentState.toAppSettings().copy(isDarkMode = !settings.isDarkMode))
  }

  override fun handleLike() {
    val isLiked = currentState.isLike
    val toggleLike = {
      val info = currentState.toArticlePersonalInfo()
      repository.updateArticlePersonalInfo(info.copy(isLike = !info.isLike))
    }

    toggleLike()

    val msg =
      if (!isLiked) Notify.TextMessage("Mark is liked")
      else Notify.ActionMessage("Don`t like it anymore", "No, still like it", toggleLike)

    notify(msg)
  }

  override fun handleBookmark() {
    val info = currentState.toArticlePersonalInfo()
    repository.updateArticlePersonalInfo(info.copy(isBookmark = !info.isBookmark))

    val msg = if (currentState.isBookmark) "Add to bookmarks" else "Remove from bookmarks"
    notify(Notify.TextMessage(msg))
  }

  override fun handleShare() {
    val msg = "Share is not implemented"
    notify(Notify.ErrorMessage(msg, "OK", null))
  }

  override fun handleToggleMenu() {
    updateState { it.copy(isShowMenu = !it.isShowMenu) }
  }
}

data class ArticleState(
  val isAuth: Boolean = false,
  val isLoadingContent: Boolean = true,
  val isLoadingReviews: Boolean = true,
  val isLike: Boolean = false,
  val isBookmark: Boolean = false,
  val isShowMenu: Boolean = false,
  val isBigText: Boolean = false,
  val isDarkMode: Boolean = false,
  val isSearch: Boolean = false,
  val searchQuery: String? = null,
  val searchResult: List<Pair<Int, Int>> = emptyList(),
  val searchPosition: Int = 0,
  val shareLink: String? = null,
  val title: String? = null,
  val category: String? = null,
  val categoryIcon: Any? = null,
  val date: String? = null,
  val author: Any? = null,
  val poster: String? = null,
  val content: List<Any> = emptyList(),
  val reviews: List<Any> = emptyList()
) : IViewModelState {
  override fun save(outState: Bundle) {
    TODO("not implemented")
  }

  override fun restore(saveStore: Bundle): IViewModelState {
    TODO("not implemented")
  }
}
