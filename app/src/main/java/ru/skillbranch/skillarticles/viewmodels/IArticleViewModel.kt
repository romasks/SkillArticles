package ru.skillbranch.skillarticles.viewmodels

interface IArticleViewModel {
  fun handleSearchMode(isSearch: Boolean)
  fun handleSearchText(searchQuery: String?)
  fun handleUpResult()
  fun handleDownResult()
  fun handleNightMode()
  fun handleUpText()
  fun handleDownText()
  fun handleBookmark()
  fun handleLike()
  fun handleShare()
  fun handleToggleMenu()
}
