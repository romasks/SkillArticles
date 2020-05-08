package ru.skillbranch.skillarticles

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.text.Layout
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import ru.skillbranch.skillarticles.markdown.spans.BlockquotesSpan
import ru.skillbranch.skillarticles.markdown.spans.HeaderSpan
import ru.skillbranch.skillarticles.markdown.spans.HorizontalRuleSpan
import ru.skillbranch.skillarticles.markdown.spans.IconLinkSpan
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
    val lbottom = 80 // line bottom

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
    val levels = 1..6
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
    val lbottom = 80 // line bottom
    val defaultAscent = -30
    val defaultDescent = 10

    for (level in levels) {
      // mock
      val canvas = mock(Canvas::class.java)
      `when`(canvas.width).thenReturn(canvasWidth)
      val paint = mock(Paint::class.java)
      `when`(paint.color).thenReturn(defaultColor)
      val measurePaint = mock(TextPaint::class.java)
      val drawPaint = mock(TextPaint::class.java)
      val layout = mock(Layout::class.java)
      val fm = mock(Paint.FontMetricsInt::class.java)
      fm.ascent = defaultAscent
      fm.descent = defaultDescent

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

      //check change line height
      span.chooseHeight(text, 0, text.length.inc(), 0, 0, fm)
      assertEquals((defaultAscent - marginTop).toInt(), fm.ascent)
      assertEquals(((defaultDescent - defaultAscent) * span.linePadding + marginBottom).toInt(), fm.descent)
      assertEquals(fm.top, fm.ascent)
      assertEquals(fm.bottom, fm.descent)

      // check line draw
      span.drawLeadingMargin(
        canvas, paint, cml, 1, ltop, lbase, lbottom,
        text, 0, text.length, true, layout
      )

      // check order call
      val inOrder = inOrder(paint, canvas)

      if (level == 1 || level == 2) {
        // check first set color to paint
        inOrder.verify(paint).color = lineColor
        val lh = (paint.descent() - paint.ascent()) * span.sizes[level]!!
        val lineOffset = lbase + lh * span.linePadding
        inOrder.verify(canvas).drawLine(0f, lineOffset, canvasWidth.toFloat(), lineOffset, paint)
        inOrder.verify(paint).color = defaultColor
      }
    }
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
    val lbottom = 80 // line bottom

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
    val lbottom = 80 // line bottom

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

  @Test
  fun draw_link() {
    // settings
    val iconColor: Int = Color.RED
    val padding: Float = 8f
    val textColor: Int = Color.BLUE

    // defaults
    val canvasWidth = 700
    val defaultColor = Color.GRAY
    val measureText = 100f
    val defaultAscent = -30
    val defaultDescent = 10
    val cml = 0 // current margin location
    val ltop = 0 // line top
    val lbase = 60 // line baseline
    val lbottom = 80 // line bottom

    val TAG = "draw_link"

    Log.d(TAG, "MOCK")
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
    val fm = mock(Paint.FontMetricsInt::class.java)
    fm.ascent = defaultAscent
    fm.descent = defaultDescent

    Log.d(TAG, "SPY")
    // spy
    val linkDrawable: Drawable = spy(VectorDrawable())
    val path: Path = spy(Path())

    val text = SpannableString("text")

    val span = IconLinkSpan(linkDrawable, iconColor, padding, textColor)
    text.setSpan(span, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    span.path = path

    Log.d(TAG, "check measure size")
    // check measure size
    val size = span.getSize(paint, text, 0, text.length, fm)
    assertEquals((defaultDescent - defaultAscent + padding + measureText).toInt(), size)

    Log.d(TAG, "check drawable set bounds and set tint")
    // check drawable set bounds and set tint
    verify(linkDrawable).setBounds(0, 0, fm.descent - fm.ascent, fm.descent - fm.ascent)
    verify(linkDrawable).setTint(iconColor)

    Log.d(TAG, "check draw icon and text")
    // check draw icon and text
    span.draw(canvas, text, 0, text.length, cml.toFloat(), ltop, lbase, lbottom, paint)
    Log.d(TAG, "1111111")

    val inOrder = inOrder(paint, canvas, path, linkDrawable)

    Log.d(TAG, "check path effect")
    // check path effect
    verify(paint, atLeastOnce()).pathEffect = any()
    verify(paint, atLeastOnce()).strokeWidth = 0f
    inOrder.verify(paint).color = textColor

    Log.d(TAG, "check reset path")
    // check reset path
    inOrder.verify(path).reset() // check reset before draw
    verify(path).moveTo(cml + span.iconSize + padding, lbottom.toFloat())
    verify(path).lineTo(cml + span.iconSize + padding + span.textWidth, lbottom.toFloat())

    Log.d(TAG, "check draw path")
    // check draw path
    inOrder.verify(canvas).drawPath(path, paint)

    Log.d(TAG, "check draw icon")
    // check draw icon
    inOrder.verify(canvas).save()
    inOrder.verify(canvas).translate(
      cml.toFloat(),
      (lbottom - linkDrawable.bounds.bottom).toFloat()
    )
    inOrder.verify(linkDrawable).draw(canvas)
    inOrder.verify(canvas).restore()

    Log.d(TAG, "check draw text")
    // check draw text
    inOrder.verify(paint).color = textColor
    inOrder.verify(canvas).drawText(
      text,
      0,
      text.length,
      cml + span.iconSize + padding,
      lbase.toFloat(),
      paint
    )
    inOrder.verify(paint).color = defaultColor
  }
}

