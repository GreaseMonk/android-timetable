/*
 * Copyright (c) 2018 .
 * This file is released under the Apache-2.0 license and part of the TimeTable repository located at:
 * https://github.com/greasemonk/android-timetable-core
 * See the "LICENSE" file at the root of the repository or go to the address below for the full license details.
 * https://github.com/GreaseMonk/android-timetable-core/blob/develop/LICENSE
 */

package nl.greasemonk.timetable

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.annotation.IntRange


/**
 * Created by Wiebe Geertsma on 10-11-2016.
 * E-mail: [e.w.geertsma@gmail.com](mailto:e.w.geertsma@gmail.com?SUBJECT=SpannableBar)
 *
 * @see [SpannableBar on Github](https://github.com/GreaseMonk/SpannableBar)
 *
 * @see [Issue tracker](https://github.com/GreaseMonk/SpannableBar/issues)
 * <br></br><br></br>
 * SpannableBar is a Grid-style spannable bar, that is useful when you need a way to span a bar over columns.
 * The view allows you to set the starting column, the span, the number of columns, and more.
 */
class SpannableBar : View {

    private var text: String? = null
    private var start = DEFAULT_START
    private var span = DEFAULT_SPAN
    private var columnCount = DEFAULT_COLUMN_COUNT
    private var textColor = DEFAULT_TEXT_COLOR
    private var color = DEFAULT_BAR_COLOR
    private var textSize = DEFAULT_TEXT_SIZE_SP
    private var scaledDensity: Float = 0.toFloat()
    private var radius: Float = 0.toFloat()
    private var textPaint: Paint? = null
    private var linePaint: Paint? = null
    private var drawable: ShapeDrawable? = null
    private var drawCells = false
    private var renderToSides = true
    private var columnColors: MutableMap<Int, Paint>? = null
    private var bounds = Rect()

    /**
     * An array of 8 radius values, for the outer roundrect.
     * The first two floats are for the top-left corner (remaining pairs correspond clockwise).
     * For no rounded corners on the outer rectangle, pass null.
     *
     * @see [RoundRectShape](https://developer.android.com/reference/android/graphics/drawable/shapes/RoundRectShape.html)
     */
    private var radii = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)


    //region CONSTRUCTORS

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    //endregion


    private fun init(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray = context.theme.obtainStyledAttributes(
                    attrs,
                    R.styleable.SpannableBar,
                    0, 0)
            try {
                text = typedArray.getString(R.styleable.SpannableBar_barText)
                color = typedArray.getColor(R.styleable.SpannableBar_barColor, DEFAULT_BAR_COLOR)
                textColor = typedArray.getColor(R.styleable.SpannableBar_barTextColor, Color.WHITE)
                textSize = typedArray.getDimensionPixelSize(R.styleable.SpannableBar_barTextSize, DEFAULT_TEXT_SIZE_SP)
                start = Math.abs(typedArray.getInteger(R.styleable.SpannableBar_barStart, DEFAULT_START))
                span = Math.abs(typedArray.getInteger(R.styleable.SpannableBar_barSpan, DEFAULT_SPAN))
                columnCount = Math.abs(typedArray.getInteger(R.styleable.SpannableBar_barColumns, DEFAULT_COLUMN_COUNT))

            } finally {
                typedArray.recycle()
            }
        }

        correctValues()
        columnColors = HashMap<Int, Paint>()
        scaledDensity = context.resources.displayMetrics.scaledDensity
        val typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)

        textPaint = Paint()
        textPaint!!.color = textColor
        textPaint!!.textSize = scaledDensity * textSize
        textPaint!!.textAlign = Paint.Align.CENTER
        textPaint!!.typeface = typeface
        textPaint!!.isAntiAlias = true

        linePaint = Paint()
        linePaint!!.color = Color.GRAY
        linePaint!!.alpha = 125

        setOnClickListener {
            if (text != null)
                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show()
        }

        setRadius(DEFAULT_RADIUS)
        requestLayout()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val colWidth = width / columnCount

        if (drawCells) {
            // Draw the grid for this row
            // Draw a line along the bottom border
            // canvas.drawLine(0, getHeight()-2, getWidth(), getHeight(), linePaint);
            // Draw the columns
            for (i in 0..columnCount) {
                val x: Float = i * colWidth.toFloat()
                canvas.drawLine(x, 0f, x, height.toFloat(), linePaint!!)
            }
        }

        // Draw the column background colors
        if (columnColors != null) {
            for (key in columnColors!!.keys) {
                // Get coordinates without padding
                val left: Float = key * colWidth.toFloat()
                val top: Float  = 0f
                val right: Float  = left + colWidth
                val bottom: Float  = height.toFloat()

                canvas.drawRect(left, top, right, bottom, columnColors!![key]!!)
            }
        }

        if (span > 0) {
            val fromStart = start == 0
            val toEnd = start + span == columnCount
            val coordLeft = (if (renderToSides && fromStart) 0 else paddingLeft) + start * colWidth
            val coordTop = paddingTop
            var coordRight = coordLeft + span * colWidth
            if (renderToSides) {
                if (!fromStart)
                    coordRight -= paddingLeft
                if (!toEnd)
                    coordRight -= paddingRight //(renderToSides ? (fromStart ? 0 : getPaddingLeft()) - (toEnd ? 0 : getPaddingRight()) : 0);
            }
            val coordBottom = height - paddingBottom

            // Remove the corner radii if the bar spans to the sides
            if (renderToSides) {
                val removeLeftRadii = start == 0
                val removeRightRadii = start + span == columnCount

                radii[0] = if (removeLeftRadii) 0f else radius // Top left
                radii[1] = if (removeLeftRadii) 0f else radius
                radii[2] = if (removeRightRadii) 0f else radius // Top right
                radii[3] = if (removeRightRadii) 0f else radius
                radii[4] = if (removeRightRadii) 0f else radius // Bottom right
                radii[5] = if (removeRightRadii) 0f else radius
                radii[6] = if (removeLeftRadii) 0f else radius // Bottom left
                radii[7] = if (removeLeftRadii) 0f else radius
            }
            drawable!!.paint.color = color
            drawable!!.setBounds(coordLeft, coordTop, coordRight, coordBottom)
            drawable!!.draw(canvas)

            // Only make a drawcall if there is actually something to draw.
            if (text != null && !text!!.isEmpty()) {

                var textCoordX: Float = (paddingLeft + coordLeft).toFloat()
                val textBaselineToCenter: Float = Math.abs(Math.round((textPaint!!.descent() + textPaint!!.ascent()) / 2)).toFloat()
                val textBaselineCoordY: Float = height / 2 + textBaselineToCenter
                var characters = text!!.length

                textPaint!!.getTextBounds(text, 0, characters, bounds)
                while (bounds.right > coordRight - coordLeft) {
                    textPaint!!.getTextBounds(text, 0, characters, bounds)
                    characters--
                }

                var displayedText: String = text ?: ""
                if (characters != text!!.length && characters > 3) {
                    displayedText = text!!.substring(0, characters - 3) + "..."
                }
                textCoordX += bounds.width() / 2

                if (characters == text!!.length && coordRight > 0)
                // prevent division by zero
                {
                    textCoordX -= bounds.width() / 2
                    textCoordX += (coordRight - coordLeft) / 2
                }

                canvas.drawText(displayedText, 0, displayedText.length, textCoordX, textBaselineCoordY, textPaint!!)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 100
        val desiredHeight = 100

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width: Int
        val height: Int

        // Measure width
        if (widthMode == MeasureSpec.EXACTLY)
            width = widthSize
        else if (widthMode == MeasureSpec.AT_MOST)
            width = Math.min(desiredWidth, widthSize)
        else
            width = desiredWidth

        // Measure height
        if (heightMode == MeasureSpec.EXACTLY)
            height = heightSize
        else if (heightMode == MeasureSpec.AT_MOST)
            height = Math.min(desiredHeight, heightSize)
        else
            height = desiredHeight

        setMeasuredDimension(width, height)
    }

    /**
     * Correct the start, span, and column count where needed.
     */
    private fun correctValues() {
        // Make sure to set values to zero if it was set below that.
        start = Math.max(0, start)
        span = Math.max(0, span)
        columnCount = Math.max(0, columnCount)
        if (columnColors != null) {
            val iterator = columnColors!!.keys.iterator()
            while (iterator.hasNext()) {
                val key = iterator.next()
                if (key > columnCount)
                    columnColors!!.remove(key)
            }
        }


        // Make sure the span vale is correct.
        if (start + span > columnCount) {
            if (start <= columnCount)
                span = columnCount - start
            if (start >= columnCount)
                start = columnCount - 1
        }
    }

    //region GETTERS & SETTERS

    /**
     * Set a column's cell background color.
     *
     * @param row   the row to apply the color to
     * @param color the color to apply
     */
    fun setColumnColor(row: Int, color: Int) {
        val paint = Paint()
        paint.color = color
        columnColors!![row] = paint
    }

    /**
     * Removes all coloring that was previously applied to any column.
     */
    fun removeColumnColors() {
        columnColors!!.clear()
    }

    /**
     * Removes the column color of a specific row.
     *
     * @param row the row to remove the column color of.
     */
    fun removeColumnColor(row: Int) {
        if (columnColors == null)
            return

        if (columnColors!!.containsKey(row))
            columnColors!!.remove(row)
    }

    /**
     * Sets all the required properties of the bar in one go.
     * Any values will be corrected for you, for example:
     * start = 3, span = 7, columnCount = 7, will have it's span corrected to 4.
     * Any values below zero will be automatically corrected to zero.
     *
     * @param start       the start column of the bar (0 to columnCount)
     * @param span        the span of the bar
     * @param columnCount the amount of columns to set
     */
    fun setProperties(@IntRange(from = 1) start: Int, @IntRange(from = 0) span: Int, @IntRange(from = 1) columnCount: Int) {
        this.start = start
        this.span = span
        this.columnCount = columnCount
        correctValues()
        invalidate()
    }

    /**
     * Set the amount of columnCount
     *
     * @param numColumns the amount of columnCount to set
     */
    fun setColumnCount(@IntRange(from = 0) numColumns: Int) {
        columnCount = numColumns
        correctValues()
        invalidate()
    }

    /**
     * Set the displayed text. The view will automatically be invalidated.
     *
     * @param text the text to be displayed
     */
    fun setText(text: String?) {
        this.text = text ?: ""
        invalidate()
    }

    /**
     * Set the desired starting column of the bar. Any amount that is higher than the span
     * will automatically limit itself to the value of columnCount.
     * If you have set the amount of columns to 7, use 0-6.
     *
     * @param start the start column of the bar (0 to columnCount)
     */
    fun setStart(@IntRange(from = 0) start: Int) {
        this.start = start
        correctValues()
        invalidate()
    }

    /**
     * Set the bar's span. This is the actual span,
     * so a value of 1 will show a bar with one column filled.
     *
     * @param span the span to set the bar to.
     */
    fun setSpan(@IntRange(from = 0) span: Int) {
        this.span = span
        correctValues()
        invalidate()
    }

    /**
     * Set the bar's corner radius
     *
     * @param radius the radius to set
     */
    fun setRadius(radius: Float) {
        this.radius = scaledDensity * radius + 0.5f
        this.radii = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)

        drawable = ShapeDrawable(RoundRectShape(radii, null, null))
        invalidate()
    }

    /**
     * Set the bar text size. Values that are zero or below will be brought back up to 1.
     *
     * @param sp
     */
    fun setTextSize(@IntRange(from = 1) sp: Int) {
        this.textSize = Math.max(1, sp)
        textPaint!!.textSize = scaledDensity * this.textSize
        invalidate()
    }

    /**
     * Set the bar color
     *
     * @param color the color to set the bar to, such as Color.WHITE.
     */
    fun setColor(color: Int) {
        this.color = color
        invalidate()
    }

    /**
     * Set the text alignment
     *
     * @param align the alignment of the text to set.
     */
    fun setTextAlignment(align: Paint.Align) {
        textPaint!!.textAlign = align
        invalidate()
    }

    /**
     * Shows additional lines along the outline of each cell.
     *
     * @param show set to TRUE to show cell lines
     */
    fun setShowCellLines(show: Boolean) {
        this.drawCells = show
        invalidate()
    }

    /**
     * Set the cell lines color
     *
     * @param color the color to set the cell lines to.
     */
    fun setCellLineColor(color: Int) {
        this.linePaint!!.color = color
        invalidate()
    }

    /**
     * Set the text typeface, to set the desired font.
     * Default: Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
     *
     * @param typeface the typeface to assign to the text
     */
    fun setTextTypeface(typeface: Typeface) {
        textPaint!!.typeface = typeface
        invalidate()
    }

    /**
     * The bar will automatically draw to the left and right edge of the
     * view (no rounded corners) if span == columnCount
     *
     * @param renderToSides TRUE to enable
     */
    fun setRenderToSides(renderToSides: Boolean) {
        this.renderToSides = renderToSides
    }

    companion object {
        val DEFAULT_START = 0
        val DEFAULT_SPAN = 7
        val DEFAULT_COLUMN_COUNT = 7 // week view, 7 days
        val DEFAULT_RADIUS = 4f
        val DEFAULT_BAR_COLOR = Color.argb(128, 63, 81, 181)
        val DEFAULT_TEXT_SIZE_SP = 12
        val DEFAULT_TEXT_COLOR = Color.WHITE
    }

    //endregion
}