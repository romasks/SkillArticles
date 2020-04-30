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

  @Test
  fun parse_italic() {
    val result = MarkdownParser.parse(italicString)
    val actual = prepare<Element.Italic>(result.elements)
    Assert.assertEquals(expectedItalic, actual)

    printResults(actual)
    println("")
    printElements(result.elements)
  }

  @Test
  fun parse_bold() {
    val result = MarkdownParser.parse(boldString)
    val actual = prepare<Element.Bold>(result.elements)
    Assert.assertEquals(expectedBold, actual)

    printResults(actual)
    println("")
    printElements(result.elements)
  }

  @Test
  fun parse_strike() {
    val result = MarkdownParser.parse(strikeString)
    val actual = prepare<Element.Strike>(result.elements)
    Assert.assertEquals(expectedStrike, actual)

    printResults(actual)
    println("")
    printElements(result.elements)
  }

  @Test
  fun parse_combine() {
    val result = MarkdownParser.parse(combineEmphasisString)
    val actualItalic = prepare<Element.Italic>(result.elements)
    val actualBold = prepare<Element.Bold>(result.elements)
    val actualStrike = prepare<Element.Strike>(result.elements)

    Assert.assertEquals(expectedCombine["italic"], actualItalic)
    Assert.assertEquals(expectedCombine["bold"], actualBold)
    Assert.assertEquals(expectedCombine["strike"], actualStrike)

    printResults(actualItalic)
    printResults(actualBold)
    printResults(actualStrike)
    println("")
    printElements(result.elements)
  }

  @Test
  fun parse_rule() {
    val result = MarkdownParser.parse(ruleString)
    val actual = prepare<Element.Rule>(result.elements)
    Assert.assertEquals(3, actual.size)

    printResults(actual)
    println("")
    printElements(result.elements)
  }

  @Test
  fun parse_inline_code() {
    val result = MarkdownParser.parse(inlineString)
    val actual = prepare<Element.InlineCode>(result.elements)
    Assert.assertEquals(expectedInline, actual)

    printResults(actual)
    println("")
    printElements(result.elements)
  }

  @Test
  fun parse_link() {
    val result = MarkdownParser.parse(linkString)
    val actual = prepare<Element.Link>(result.elements)
    val actualLink = result.elements.spread()
      .filterIsInstance<Element.Link>()
      .map { it.link }

    Assert.assertEquals(expectedLink["titles"], actual)
    Assert.assertEquals(expectedLink["links"], actualLink)

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
