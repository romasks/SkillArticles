package ru.skillbranch.skillarticles

import org.junit.Assert
import org.junit.Test
import ru.skillbranch.skillarticles.markdown.Element
import ru.skillbranch.skillarticles.markdown.MarkdownParser

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun parse_list_items() {
    val result = MarkdownParser.parse(unorderedListString)
    val actual = prepare<Element.UnorderedListItem>(result.elements)
    Assert.assertEquals(expectedUnorderedList, actual)

    printResults(actual)
    println("")
    printElements(result.elements)
  }

  @Test
  fun parse_headers() {
    val result = MarkdownParser.parse(headerString)
    val actual = prepare<Element.Header>(result.elements)
    val actualLevels = result.elements.spread()
      .filterIsInstance<Element.Header>()
      .map { it.level }

    Assert.assertEquals(expectedHeader, actual)
    Assert.assertEquals(listOf(1, 2, 3, 4, 5, 6), actualLevels)

    printResults(actual)
    println("")
    printElements(result.elements)
  }

  @Test
  fun parse_quote() {
    val result = MarkdownParser.parse(quoteString)
    val actual = prepare<Element.Quote>(result.elements)
    Assert.assertEquals(expectedQuote, actual)

    printResults(actual)
    println("")
    printElements(result.elements)
  }

  private fun printResults(list: List<String>) {
    val iterator = list.iterator()
    while (iterator.hasNext()) {
      println("find >> ${iterator.next()}")
    }
  }

  private fun printElements(list: List<Element>) {
    val iterator = list.iterator()
    while (iterator.hasNext()) {
      println("element >> ${iterator.next()}")
    }
  }

  private fun Element.spread(): List<Element> {
    val elements = mutableListOf<Element>()
    elements.add(this)
    elements.addAll(this.elements.spread())
    return elements
  }

  private fun List<Element>.spread(): List<Element> {
    val elements = mutableListOf<Element>()
    if (this.isNotEmpty()) elements.addAll(
      this.fold(mutableListOf()) { acc, el -> acc.also { it.addAll(el.spread()) } }
    )
    return elements
  }

  private inline fun <reified T : Element> prepare(list: List<Element>): List<String> {
    return list
      .fold(mutableListOf<Element>()) { acc, el ->
        acc.also { it.addAll(el.spread()) } // spread inner elements
      }
      .filterIsInstance<T>() // filter only expected instance
      .map { it.text.toString() } // transform to element text
  }
}
