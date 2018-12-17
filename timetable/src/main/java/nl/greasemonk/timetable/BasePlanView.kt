/*
 * Copyright (c) 2018 .
 * This file is released under the Apache-2.0 license and part of the TimeTable repository located at:
 * https://github.com/greasemonk/android-timetable-core
 * See the "LICENSE" file at the root of the repository or go to the address below for the full license details.
 * https://github.com/GreaseMonk/android-timetable-core/blob/develop/LICENSE
 */

package nl.greasemonk.timetable

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.AttributeSet
import android.view.View
import nl.greasemonk.timetable.enums.PlanViewMode
import nl.greasemonk.timetable.interfaces.IPlannable
import nl.greasemonk.timetable.models.TimeRange
import nl.greasemonk.timetable.planning.PlanningDelegate
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

internal open class BasePlanView: View {

    constructor(context: Context) : super(context) {
        this.init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.init(context, attrs)
    }


    var delegate: PlanningDelegate? = null


    protected lateinit var planViewMode: PlanViewMode
    lateinit var displayedRange: TimeRange

    // Drawable vars
    protected lateinit var drawable: ShapeDrawable
    protected lateinit var arrowLeftDrawable: Drawable
    protected lateinit var arrowRightDrawable: Drawable

    // Paint vars
    protected lateinit var gridLinePaint: Paint
    protected lateinit var diagonalLinesPaint: Paint
    protected lateinit var barPaint: Paint
    protected lateinit var barStrokePaint: Paint
    protected lateinit var textPaint: Paint
    protected lateinit var arrowPaint: Paint
    protected lateinit var todayPaint: Paint

    /**
     * An array of 8 radius values, for the outer roundrect.
     * The first two floats are for the top-left corner (remaining pairs correspond clockwise).
     * For no rounded corners on the outer rectangle, pass null.
     *
     * @see [RoundRectShape](https://developer.android.com/reference/android/graphics/drawable/shapes/RoundRectShape.html)
     */
    protected lateinit var radii: FloatArray

    protected var scaleWithMultiplierSetting = false

    protected var columnCount: Int = 0
    protected var rowCount: Int = 0 // Amount of rows since getRowItems() was last called
    protected var defaultRowHeight: Float = 48f
    protected var desiredRowHeight: Float = 48f
    protected var desiredVerticalBarPadding = 10f
    protected var verticalBarPadding = 0f
    protected var barStrokeSize = 4f
    protected var scaledDensity: Float = 0f
    protected var arrowHeight: Float = 0f
    protected var arrowWidth: Float = 0f

    open fun init(context: Context, attrs: AttributeSet?) {

        scaledDensity = context.resources.displayMetrics.scaledDensity
        barStrokeSize = 4f * scaledDensity * PlanningModule.instance.preferredBarHeightMultiplier
        desiredRowHeight = defaultRowHeight * scaledDensity
        verticalBarPadding = scaledDensity * desiredVerticalBarPadding * PlanningModule.instance.preferredBarHeightMultiplier

        gridLinePaint = Paint()
        gridLinePaint.color = Color.GRAY
        gridLinePaint.alpha = 125

        diagonalLinesPaint = Paint()
        diagonalLinesPaint.color = Color.WHITE
        diagonalLinesPaint.alpha = 125
        diagonalLinesPaint.isAntiAlias = true


        todayPaint = Paint()
        todayPaint.color = Color.RED
        todayPaint.alpha = 125
        todayPaint.style = Paint.Style.STROKE
        todayPaint.strokeWidth = 4f

        barPaint = Paint()
        barPaint.color = Color.BLUE
        barPaint.style = Paint.Style.FILL
        barPaint.isAntiAlias = true

        barStrokePaint = Paint()
        barStrokePaint.color = Color.WHITE
        barStrokePaint.style = Paint.Style.STROKE
        barStrokePaint.strokeWidth = barStrokeSize
        barStrokePaint.strokeCap = Paint.Cap.ROUND
        barStrokePaint.isAntiAlias = true

        arrowPaint = Paint()
        arrowPaint.color = Color.BLUE
        arrowPaint.style = Paint.Style.FILL
        arrowPaint.isAntiAlias = true

        val typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        textPaint = Paint()
        textPaint.color = Color.WHITE
        textPaint.textSize = scaledDensity * 12
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.typeface = typeface
        textPaint.isAntiAlias = true

        initBarDrawable(4f)
        arrowLeftDrawable = context.resources.getDrawable(R.drawable.arrow_left, context.theme)
        arrowRightDrawable = context.resources.getDrawable(R.drawable.arrow_right, context.theme)

        arrowHeight = scaledDensity * 12f
        arrowWidth = scaledDensity * 6f

        planViewMode = PlanningModule.instance.mode
        columnCount = when(planViewMode) {
            PlanViewMode.WEEK -> 7
            PlanViewMode.MONTH -> Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH) // View days
        }
    }

    /**
     * Set the bar's corner radius
     *
     * @param radius the radius to set
     */
    private fun initBarDrawable(radius: Float) {
        val r = scaledDensity * radius + 0.5f
        this.radii = floatArrayOf(r, r, r, r, r, r, r, r)

        val roundRectShape = RoundRectShape(radii, null, null)
        drawable = ShapeDrawable(roundRectShape)
    }

    /**
     * Called to determine the size requirements for this view
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightMultiplier = PlanningModule.instance.preferredBarHeightMultiplier

        barStrokeSize = 4f * scaledDensity * heightMultiplier
        desiredRowHeight = defaultRowHeight * scaledDensity
        verticalBarPadding = scaledDensity * desiredVerticalBarPadding * heightMultiplier

        val desiredWidth = 100
        val desiredHeight = desiredRowHeight * rowCount * heightMultiplier

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width: Int
        val height: Int

        // Measure the width
        width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> Math.min(desiredWidth, widthSize)
            MeasureSpec.UNSPECIFIED -> desiredWidth
            else -> desiredWidth
        }

        // Measure the height
        height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> Math.min(desiredHeight.toInt(), heightSize)
            MeasureSpec.UNSPECIFIED -> desiredHeight.toInt()
            else -> desiredHeight.toInt()
        }

        setMeasuredDimension(width, height)
    }

    /**
     * Get the row height to use when drawing the rows
     *
     * @returns the row height
     */
    protected fun getRowHeight(): Float {
        if (rowCount <= 0 || height <= 0) {
            return 0f
        }
        return height.toFloat() / rowCount
    }

    /**
     * Get the column width
     *
     * @return the column width
     */
    protected fun getColumnWidth(): Float {
        val columnCount = columnCount
        if (columnCount <= 0)
            return 0f
        return width.toFloat() / columnCount
    }

    /**
     * Calculates the start and end column indexes for a given IPlanItem object
     *
     * @param item the IPlanItem to calculate the start and end column index for
     * @return the start and end column index as Pair<start column, end column>
     */
    protected fun <T: IPlannable> getColumnIndexes(item: T): Pair<Int, Int> {
        val itemStartsBeforeLeftSide = item.timeRange.start < displayedRange.start
        val itemEndsAfterRightSide = item.timeRange.endInclusive > displayedRange.endInclusive
        val columnCount = columnCount -1
        var start: Int
        var end: Int

        if(itemStartsBeforeLeftSide && itemEndsAfterRightSide) {
            start = 0
            end = columnCount
        } else {
            val calendarStart = GregorianCalendar.getInstance()
            val calendarEnd = GregorianCalendar.getInstance()
            calendarStart.timeInMillis = if (item.timeRange.start < displayedRange.start) displayedRange.start else item.timeRange.start
            calendarEnd.timeInMillis = item.timeRange.endInclusive

            start = if (itemStartsBeforeLeftSide) 0
            else {
                val difference = item.timeRange.start - displayedRange.start
                val hours = TimeUnit.HOURS.convert(Math.abs(difference), TimeUnit.MILLISECONDS)
                val days: Double = hours.toDouble() / 24
                Math.floor(days).roundToInt()
            }

            end = if (itemEndsAfterRightSide) columnCount else {
                if (calendarStart.get(Calendar.YEAR) == calendarEnd.get(Calendar.YEAR)) {
                    start + Math.abs(calendarStart.get(Calendar.DAY_OF_YEAR) - calendarEnd.get(Calendar.DAY_OF_YEAR))
                } else {
                    var extraDays = 0
                    val dayOneOriginalYearDays = calendarStart.get(Calendar.DAY_OF_YEAR)

                    while (calendarStart.get(Calendar.YEAR) > calendarEnd.get(Calendar.YEAR)) {
                        calendarStart.add(Calendar.YEAR, -1)
                        extraDays += calendarStart.getActualMaximum(Calendar.DAY_OF_YEAR)
                    }
                    extraDays - calendarEnd.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYearDays

                }
            }
        }
        end = Math.min(end, columnCount)
        start = Math.max(0, start)
        return Pair(start, end)
    }

    /**
     * Get the item rows
     *
     * @return a List of IPlanItems for each row, wrapped in another List
     */
    protected fun <T: IPlannable> getRowItems(items: List<T>): List<List<T>> {
        val itemRows: MutableList<MutableList<T>> = mutableListOf()
        for (item in items) {
            var didInsertIntoRow = false

            // Find the row to insert this item into
            for (itemRow in itemRows) {
                var shouldInsertIntoRow = true

                // Make sure there is no conflicting planitem
                for (itemInRow in itemRow) {
                    if(itemInRow.timeRange.overlaps(item.timeRange, true)) {
                        shouldInsertIntoRow = false
                        break
                    }
                }

                if (shouldInsertIntoRow) {
                    didInsertIntoRow = true
                    itemRow.add(item)
                    break
                }
            }

            // We can't find a row where this item fits, so make a new row
            if(!didInsertIntoRow) {
                itemRows.add(mutableListOf(item))
            }
        }
        rowCount = itemRows.size
        return itemRows
    }

    /**
     * Draws lines to make a grid
     */
    protected fun drawVerticalGridLines(canvas: Canvas) {
        for (i in 0..columnCount) {
            val x: Float = i * getColumnWidth()
            canvas.drawLine(x, 0f, x, height.toFloat(), gridLinePaint)
        }
    }
}