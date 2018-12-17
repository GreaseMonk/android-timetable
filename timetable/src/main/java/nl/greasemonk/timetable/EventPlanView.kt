/*
 * Copyright (c) 2018 .
 * This file is released under the Apache-2.0 license and part of the TimeTable repository located at:
 * https://github.com/greasemonk/android-timetable-core
 * See the "LICENSE" file at the root of the repository or go to the address below for the full license details.
 * https://github.com/GreaseMonk/android-timetable-core/blob/develop/LICENSE
 */

package nl.greasemonk.timetable

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import nl.greasemonk.timetable.enums.PlanViewMode
import nl.greasemonk.timetable.enums.Style
import nl.greasemonk.timetable.extensions.drawDiagonalLinesStyle
import nl.greasemonk.timetable.extensions.drawItemArrows
import nl.greasemonk.timetable.extensions.drawItemText
import nl.greasemonk.timetable.interfaces.IEventItem
import nl.greasemonk.timetable.models.TimeRange
import java.util.*
import java.util.Calendar.*

@Suppress("NAME_SHADOWING")
internal class EventPlanView : BasePlanView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var rowItems: List<List<IEventItem>> = mutableListOf()
    private var coordCache: MutableList<Pair<IEventItem, Rect>> = mutableListOf()
    private var clickListener: ((IEventItem) -> Unit)? = null

    override fun init(context: Context, attrs: AttributeSet?) {
        defaultRowHeight = 24f
        desiredRowHeight = 24f
        desiredVerticalBarPadding = 4f

        super.init(context, attrs)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.apply {
            coordCache = mutableListOf()
            drawVerticalGridLines(this)
            drawAllItems(this)
        }
    }

    /**
     * Set the items
     */
    fun setItems(planViewMode: PlanViewMode, pageTimeRange: TimeRange) {
        this.planViewMode = planViewMode
        this.displayedRange = pageTimeRange
        this.rowItems = this.getRowItems(PlanningModule.instance.events.filter { pageTimeRange.overlaps(it.timeRange, true) })
        requestLayout()
        invalidate()
    }

    /**
     * Draws all items onto the given Canvas
     *
     * @param canvas the Canvas to draw all items on
     */
    private fun drawAllItems(canvas: Canvas) {
        for ((rowNum, row) in rowItems.withIndex()) {
            drawRow(canvas, row, rowNum)
        }
    }

    /**
     * Draw the rows for the given list of IEventItems
     *
     * Only give a list of non-overlapping IEventItems because this function
     * will draw items on to the row, even if the items overlap
     *
     * @param canvas the canvas to draw on
     * @param items the list of IEventItems to draw onto the row
     * @param row the row to draw
     */
    private fun drawRow(canvas: Canvas, items: List<IEventItem>, row: Int) {
        val rowHeight = getRowHeight()
        val lineYpos = row * rowHeight + (verticalBarPadding * 0.5f) - 0.5f

        // Draw the weekend background
        val columnWidth = getColumnWidth()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = displayedRange.start
        for (i: Int in 0..columnCount) {
            if (calendar.get(DAY_OF_WEEK) == SATURDAY || calendar.get(DAY_OF_WEEK) == SUNDAY) {
                val weekendRect = Rect((i * columnWidth).toInt(), (row * rowHeight).toInt(), (i * columnWidth + columnWidth).toInt(), (row * rowHeight + rowHeight).toInt())
                canvas.drawRect(weekendRect, gridLinePaint)
            }
            calendar.add(DAY_OF_YEAR, 1)
        }



        // Draw a horizontal line at the bottom of each row
        canvas.drawLine(0f, lineYpos, width.toFloat(), lineYpos, gridLinePaint)
        for (item in items) {
            drawItem(canvas, item, row)
        }
    }

    /**
     * Draw the rectangle and text for the given item
     *
     */
    private fun drawItem(canvas: Canvas, item: IEventItem, row: Int) {
        val endOfWeekMillis = displayedRange.endInclusive
        val columnIndexes = getColumnIndexes(item)
        val rowHeight = getRowHeight()
        val columnWidth = getColumnWidth()

        val startY = row * rowHeight + verticalBarPadding
        val endY = startY + rowHeight - verticalBarPadding
        val startX = columnWidth * columnIndexes.first
        val endX = columnWidth + (columnWidth * columnIndexes.second)
        val rect = RectF(startX, startY, endX, endY)
        val roundedRect = Rect()
        rect.round(roundedRect)
        val itemColor = item.eventColor ?: Color.RED
        val shader = LinearGradient(0f, 0f, 0f, 50f, intArrayOf(itemColor, itemColor.lighter()), null, Shader.TileMode.CLAMP)

        coordCache.add(Pair(item, Rect(roundedRect.left, roundedRect.top, roundedRect.right, roundedRect.bottom)))

        val itemIsSelected = delegate?.getLastClickedPlanItem() == item

        barPaint.color = if (itemIsSelected) itemColor.lighter() else itemColor
        barPaint.shader = shader
        drawable.paint.set(barPaint)
        drawable.bounds = roundedRect
        drawable.draw(canvas)

        // Draw a 'selected' effect on the plan item
        if (delegate?.getLastClickedPlanItem() == item) {
            drawable.paint.set(barStrokePaint)
            val offset: Int = (barStrokeSize / 4).toInt()
            drawable.bounds = Rect(roundedRect.left + offset, roundedRect.top + offset, roundedRect.right - offset, roundedRect.bottom - offset)
            drawable.draw(canvas)
        }

        // Draw the item arrows if needed
        val showLeftArrow = columnIndexes.first == 0 && item.timeRange.start < displayedRange.start
        val showRightArrow = columnIndexes.second == (columnCount - 1) && item.timeRange.endInclusive > endOfWeekMillis
        canvas.drawItemArrows(arrowLeftDrawable, arrowRightDrawable, arrowHeight, arrowWidth, showLeftArrow, showRightArrow, rect)

        if (item.eventStyle == Style.DIAGONAL_LINES) {
            canvas.drawDiagonalLinesStyle(diagonalLinesPaint, rect)
        }

        // Define the rect for the text to draw in, and finally draw the text.
        val textRectStart: Int = roundedRect.left + (if (showLeftArrow) arrowWidth.toInt() else 0)
        val textRectEnd: Int = roundedRect.right - (if (showRightArrow) arrowWidth.toInt() else 0)
        val textRect = Rect(textRectStart, roundedRect.top, textRectEnd, roundedRect.bottom)
        canvas.drawItemText(textPaint, item.eventName, textRect)
    }

    /**
     * Handle touch screen motion events.
     *
     * @return true if the action was intercepted
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_UP -> {
                if (clickListener != null) {
                    val clickedItem = itemAtPosition(event.x.toInt(), event.y.toInt())
                    if (clickedItem != null) {
                        clickListener!!.invoke(clickedItem)
                        invalidate()
                    }
                }
            }
        }
        return true
    }

    /**
     * Get the IEventItem at a specific position, of there is any
     *
     * @param x the x position to look for
     * @param y the y position to look for
     *
     * @return the first IEventItem that was found at the position, if any
     */
    private fun itemAtPosition(x: Int, y: Int): IEventItem? {
        for (entry in coordCache) {
            if (entry.second.contains(x, y)) {
                return entry.first
            }
        }
        return null
    }
}
