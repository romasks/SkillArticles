package ru.skillbranch.skillarticles

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Layout
import android.text.SpannableString
import android.text.Spanned
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.mock
import ru.skillbranch.skillarticles.markdown.spans.BlockquotesSpan
import ru.skillbranch.skillarticles.markdown.spans.UnorderedListSpan

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class Hometask5InstrumentedTest {

  @Test
  fun draw_list_item() {
    // settings
    val color = Color.RED
    val gap = 24f
    val radius = 8f

    // defaults
    val canvasWidth = 700
    val defaultColor = Color.GRAY
    val cml = 0 // current margin location
    val ltop = 0 // line top
    val lbase = 60 // line baseline
    val lbottom = 60 // line bottom

    // mock
    val canvas = mock(Canvas::class.java)
    val paint = mock(Paint::class.java)
    `when`(paint.color).thenReturn(defaultColor)
    val layout = mock(Layout::class.java)

    val text = SpannableString("text")

    val span = UnorderedListSpan(gap, radius, color)
    text.setSpan(span, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    // check leading margin
    assertEquals((4 * radius + gap).toInt(), span.getLeadingMargin(true))

    // check bullet draw
    span.drawLeadingMargin(
      canvas, paint, cml, 1, ltop, lbase, lbottom,
      text, 0, text.length, true, layout
    )

    // check order call
    val inOrder = inOrder(paint, canvas)
    // check first set color to paint
    inOrder.verify(paint).color = color
    // check draw circle bullet
    inOrder.verify(canvas).drawCircle(
      gap + cml + radius,
      (lbottom - ltop) / 2f + ltop,
      radius,
      paint
    )
    // check paint color restore
    inOrder.verify(paint).color = defaultColor
  }

  @Test
  fun draw_quote() {
    // settings
    val color = Color.RED
    val gap = 24f
    val lineWidth = 8f

    // defaults
    val canvasWidth = 700
    val defaultColor = Color.GRAY
    val cml = 0 // current margin location
    val ltop = 0 // line top
    val lbase = 60 // line baseline
    val lbottom = 60 // line bottom

    // mock
    val canvas = mock(Canvas::class.java)
    val paint = mock(Paint::class.java)
    `when`(paint.color).thenReturn(defaultColor)
    val layout = mock(Layout::class.java)

    val text = SpannableString("text")

    val span = BlockquotesSpan(gap, lineWidth, color)
    text.setSpan(span, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    // check leading margin
    assertEquals((lineWidth + gap).toInt(), span.getLeadingMargin(true))

    // check line draw
    span.drawLeadingMargin(
      canvas, paint, cml, 1, ltop, lbase, lbottom,
      text, 0, text.length, true, layout
    )

    // check order call
    val inOrder = inOrder(paint, canvas)
    // check first set color to paint
    inOrder.verify(paint).color = color
    inOrder.verify(paint).strokeWidth = lineWidth
    // check draw circle bullet
    inOrder.verify(canvas).drawLine(
      lineWidth / 2f,
      ltop.toFloat(),
      lineWidth / 2f,
      lbottom.toFloat(),
      paint
    )
    // check paint color restore
    inOrder.verify(paint).color = defaultColor
  }
}

