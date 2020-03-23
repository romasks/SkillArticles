package ru.skillbranch.skillarticles.viewmodels

interface IArticleViewModel {
  fun saveSearchViewState(isSearch: Boolean, searchQuery: String?)
  fun handleNightMode()
  fun handleUpText()
  fun handleDownText()
  fun handleBookmark()
  fun handleLike()
  fun handleShare()
  fun handleToggleMenu()
}
