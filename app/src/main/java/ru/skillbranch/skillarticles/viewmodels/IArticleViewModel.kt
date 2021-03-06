package ru.skillbranch.skillarticles.viewmodels

interface IArticleViewModel {
  fun handleSearchMode(isSearch: Boolean)
  fun handleSearch(query: String?)
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
