package ru.skillbranch.skillarticles.extensions

fun String?.indexesOf(substr: String, ignoreCase: Boolean = true) : List<Int>? {
  if (this == null) return null
  if (substr.isEmpty()) return emptyList()
  val content = if (ignoreCase) toLowerCase() else this
  return Regex(substr).findAll(content).map { it.range.first }.toList().filter { it != -1 }
}
