package ru.skillbranch.skillarticles.viewmodels

import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.extensions.format

class ArticleViewModel(private val articleId: String) : BaseViewModel<ArticleState>(ArticleState()) {

  private val repository = ArticleRepository

  init {
    subscribeOnDataSource(getArticleData()) { article, state ->
      article ?: return@subscribeOnDataSource null
      state.copy(
        shareLink = article.shareLink,
        title = article.title,
        category = article.category,
        categoryIcon = article.categoryIcon,
        date = article.date.format()
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

  fun handleUpText() {
  }

  fun handleDownText() {
  }

  fun handleNightMode() {
  }

  fun handleLike() {
  }

  fun handleBookmarks() {
  }

  fun handleShare() {
  }

  fun handleToggleMenu() {
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
)
