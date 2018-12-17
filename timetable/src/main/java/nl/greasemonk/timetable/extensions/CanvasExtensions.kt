/*
 * Copyright (c) 2018 .
 * This file is released under the Apache-2.0 license and part of the TimeTable repository located at:
 * https://github.com/greasemonk/android-timetable-core
 * See the "LICENSE" file at the root of the repository or go to the address below for the full license details.
 * https://github.com/GreaseMonk/android-timetable-core/blob/develop/LICENSE
 */

@file:Suppress("NAME_SHADOWING")

package nl.greasemonk.timetable.extensions

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import nl.greasemonk.timetable.models.TimeRange
import kotlin.math.abs

/**
 * Draws the DIAGONAL_LINES style for items in the specified rect.
 *
 * @param canvas the Canvas to draw on
 * @param rect the RectF to draw diagonal lines in
 */
internal fun Canvas.drawDiagonalLinesStyle(paint: Paint, rect: RectF) {
    val dim = rect.height()
    val offset = dim / 3

    var x: Float = rect.left - dim + offset
    val y: Float = rect.bottom

    while (x < rect.right) {
        var endX = x + dim
        var endY = rect.top
        if (endX > rect.right) {
            val cutOffAmount = endX - rect.right
            endX = rect.right
            endY = rect.top + cutOffAmount
        }

        var startX = x
        var startY = y
        if (startX < rect.left) {
            val cutOffAmount = abs(endX - rect.left)
            startX = rect.left
            startY = rect.top + cutOffAmount
        }


        this.drawLine(startX, startY, endX, endY, paint)

        x += offset
    }
}

/**
 * Draws the arrows on an item, indicating the item goes on outside of the view
 *
 * @param canvas the Canvas to draw arrows on
 * @param drawStartArrow draws the start arrow if true
 * @param drawEndArrow draws the end arrow if true
 * @param rect the specified rect to draw in - this should be the item bounds, not the row bounds
 */
internal fun Canvas.drawItemArrows(leftDrawable: Drawable, rightDrawable: Drawable, arrowHeight: Float, arrowWidth: Float, drawStartArrow: Boolean, drawEndArrow: Boolean, rect: RectF) {
    val padding = rect.height() * 0.25f

    // Draw the left arrow if the planitem is before the left side of the view
    if (drawStartArrow) {
        val startX = rect.left + arrowHeight
        val startY = rect.top + padding
        val endX = startX + arrowWidth
        val endY = rect.bottom - padding
        val rect = RectF(startX, startY, endX, endY)
        val roundedRect = Rect()
        rect.round(roundedRect)
        leftDrawable.bounds = roundedRect
        leftDrawable.draw(this)
    }
    // Draw the right arrow if the planitem is after the right side of the view
    if (drawEndArrow) {
        val startX = rect.right - arrowHeight
        val startY = rect.top + padding
        val endX = rect.right - arrowWidth
        val endY = rect.bottom - padding
        val rect = RectF(startX, startY, endX, endY)
        val roundedRect = Rect()
        rect.round(roundedRect)
        rightDrawable.bounds = roundedRect
        rightDrawable.draw(this)
    }
}

/**
 * Draws the text on an item
 *
 * @param canvas the Canvas to draw on
 * @param text the text to draw
 * @param boundsRect the rect to draw the text in
 */
internal fun Canvas.drawItemText(paint: Paint, text:String, boundsRect: Rect) {
    if (text.isBlank())
        return

    val textBaselineToCenter: Float = Math.abs(Math.round((paint.descent() + paint.ascent()) / 2)).toFloat()
    val charactersToDraw = paint.breakText(text.toCharArray(), 0, text.length, boundsRect.width().toFloat(), null)
    val displayedText: String = text.substring(0, charactersToDraw)
    val centerX = boundsRect.left.toFloat() + boundsRect.width().toFloat() / 2
    val centerY = boundsRect.top.toFloat() + boundsRect.height() / 2 + textBaselineToCenter
    this.drawText(displayedText, 0, displayedText.length, centerX, centerY, paint)
}

/**
 * Draws the 'today' line for the given canvas
 *
 * @param canvas the Canvas to draw the today line on
 */
internal fun Canvas.drawTodayLine(paint: Paint, pageTimeRange: TimeRange) {
    // Draw a red 'today' line if the week contains today
    val now = System.currentTimeMillis()
    if (pageTimeRange.contains(now)) {
        val totalPageMillis = pageTimeRange.endInclusive - pageTimeRange.start
        val millisFromStart = now - pageTimeRange.start
        val percentage: Float = millisFromStart.toFloat() / totalPageMillis.toFloat()
        val xPos = percentage * width.toFloat()
        this.drawLine(xPos, 0f, xPos, height.toFloat(), paint)
    }
}