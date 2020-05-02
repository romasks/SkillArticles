package ru.skillbranch.skillarticles

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.Layout
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.`when`
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import ru.skillbranch.skillarticles.markdown.spans.BlockquotesSpan
import ru.skillbranch.skillarticles.markdown.spans.HeaderSpan
import ru.skillbranch.skillarticles.markdown.spans.HorizontalRuleSpan
import ru.skillbranch.skillarticles.markdown.spans.InlineCodeSpan
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

  @Test
  fun draw_header() {
    // settings
    val level = 1
    val textColor = Color.RED
    val lineColor = Color.GREEN
    val marginTop = 24f
    val marginBottom = 16f

    // defaults
    val canvasWidth = 700
    val defaultColor = Color.GRAY
    val cml = 0 // current margin location
    val ltop = 0 // line top
    val lbase = 60 // line baseline
    val lbottom = 60 // line bottom

    // mock
    val canvas = mock(Canvas::class.java)
    `when`(canvas.width).thenReturn(canvasWidth)
    val paint = mock(Paint::class.java)
    `when`(paint.color).thenReturn(defaultColor)
    val measurePaint = mock(TextPaint::class.java)
    val drawPaint = mock(TextPaint::class.java)
    val layout = mock(Layout::class.java)

    val text = SpannableString("text")

    val span = HeaderSpan(level, textColor, lineColor, marginTop, marginBottom)
    text.setSpan(span, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    // check leading margin
    assertEquals(0, span.getLeadingMargin(true))

    // check measure state
    span.updateMeasureState(measurePaint)
    verify(measurePaint).textSize *= span.sizes[level]!!
    verify(measurePaint).isFakeBoldText = true

    // check draw state
    span.updateDrawState(drawPaint)
    verify(drawPaint).textSize *= span.sizes[level]!!
    verify(drawPaint).isFakeBoldText = true
    verify(drawPaint).color = textColor

    // check line draw
    span.drawLeadingMargin(
      canvas, paint, cml, 1, ltop, lbase, lbottom,
      text, 0, text.length, true, layout
    )

    // check order call
    val inOrder = inOrder(paint, canvas)
    // check first set color to paint
    inOrder.verify(paint).color = lineColor
    val lh = (paint.descent() - paint.ascent()) * span.sizes[level]!!
    val lineOffset = lbase + lh * span.linePadding
    inOrder.verify(canvas).drawLine(0f, lineOffset, canvasWidth.toFloat(), lineOffset, paint)
    inOrder.verify(paint).color = defaultColor
  }

  @Test
  fun draw_rule() {
    // settings
    val color = Color.RED
    val width = 24f

    // defaults
    val canvasWidth = 700
    val defaultColor = Color.GRAY
    val cml = 0 // current margin location
    val ltop = 0 // line top
    val lbase = 60 // line baseline
    val lbottom = 60 // line bottom

    // mock
    val canvas = mock(Canvas::class.java)
    `when`(canvas.width).thenReturn(canvasWidth)
    val paint = mock(Paint::class.java)
    `when`(paint.color).thenReturn(defaultColor)
    val layout = mock(Layout::class.java)

    val text = SpannableString("text")

    val span = HorizontalRuleSpan(width, color)
    text.setSpan(span, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    // check draw line rule
    span.draw(canvas, text, 0, text.length, cml.toFloat(), ltop, lbase, lbottom, paint)

    // check order call
    val inOrder = inOrder(paint, canvas)
    // check first set color to paint
    inOrder.verify(paint).color = color
    inOrder.verify(canvas).drawLine(
      0f,
      (ltop + lbottom) / 2f,
      canvasWidth.toFloat(),
      (ltop + lbottom) / 2f,
      paint
    )
    inOrder.verify(paint).color = defaultColor
  }

  @Test
  fun draw_inline_code() {
    // settings
    val textColor = Color.RED
    val bgColor = Color.GREEN
    val cornerRadius = 8f
    val padding = 8f

    // defaults
    val canvasWidth = 700
    val defaultColor = Color.GRAY
    val measureText = 100f
    val cml = 0 // current margin location
    val ltop = 0 // line top
    val lbase = 60 // line baseline
    val lbottom = 60 // line bottom

    // mock
    val canvas = mock(Canvas::class.java)
    `when`(canvas.width).thenReturn(canvasWidth)
    val paint = mock(Paint::class.java)
    `when`(paint.color).thenReturn(defaultColor)
    `when`(
      paint.measureText(
        ArgumentMatchers.anyString(),
        ArgumentMatchers.anyInt(),
        ArgumentMatchers.anyInt()
      )
    ).thenReturn(measureText)
    val layout = mock(Layout::class.java)
    val fm = mock(Paint.FontMetricsInt::class.java)

    val text = SpannableString("text")

    val span = InlineCodeSpan(textColor, bgColor, cornerRadius, padding)
    text.setSpan(span, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    // check measure size
    val size = span.getSize(paint, text, 0, text.length, fm)
    assertEquals((2 * padding + measureText).toInt(), size)

    // check draw inline code
    span.draw(canvas, text, 0, text.length, cml.toFloat(), ltop, lbase, lbottom, paint)

    val inOrder = inOrder(paint, canvas)
    // check draw background
    inOrder.verify(paint).color = bgColor
    inOrder.verify(canvas).drawRoundRect(
      RectF(0f, ltop.toFloat(), measureText + 2 * padding, lbottom.toFloat()),
      cornerRadius,
      cornerRadius,
      paint
    )

    // check draw background
    inOrder.verify(paint).color = textColor
    inOrder.verify(canvas).drawText(text, 0, text.length, cml + padding, lbase.toFloat(), paint)
    inOrder.verify(paint).color = defaultColor
  }
}

