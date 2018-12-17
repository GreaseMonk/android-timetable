/*
 * Copyright (c) 2018 .
 * This file is released under the Apache-2.0 license and part of the TimeTable repository located at:
 * https://github.com/greasemonk/android-timetable-core
 * See the "LICENSE" file at the root of the repository or go to the address below for the full license details.
 * https://github.com/GreaseMonk/android-timetable-core/blob/develop/LICENSE
 */

package nl.greasemonk.timetable

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import nl.greasemonk.timetable.enums.PlanType
import nl.greasemonk.timetable.interfaces.IEventItem
import nl.greasemonk.timetable.interfaces.IPlanChapter
import nl.greasemonk.timetable.interfaces.IPlanItem
import nl.greasemonk.timetable.models.PlanItemChapter
import nl.greasemonk.timetable.planning.PageAdapter
import nl.greasemonk.timetable.planning.PlanningDelegate
import java.util.*

@Suppress("NAME_SHADOWING")
class EmployeePlanning : FrameLayout, PlanningDelegate {

    companion object {
        private const val DEFAULT_COLUMN_COUNT = 7
        private val DEFAULT_CURRENT_DAY_COLOR = Color.argb(255, 33, 150, 243)
        private const val PAGES = 100
    }

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) { initAttributes(attrs) }
    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int): super(context, attrs, defStyleAttr) { initAttributes(attrs) }
    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int, @StyleRes defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes) { initAttributes(attrs) }

    private var view: View
    private var progressBar: ProgressBar
    private var viewPager: androidx.viewpager.widget.ViewPager
    var delegate: ((IPlanItem) -> Unit)? = null

    @ColorInt
    private var titlesColor: Int = 0
    @ColorInt
    private var nowTitlesColor: Int = 0
    private var columnCount: Int = DEFAULT_COLUMN_COUNT
    @ColorInt
    private var currentDayColor: Int = DEFAULT_CURRENT_DAY_COLOR
    private var centerDate = Date()
    private var planItems: List<IPlanItem>? = null
    private var lastClickedItem: IPlanItem? = null
    override var showHeaders = false
    override var showItemsAtExactTime = true

    init {
        view = inflate(context, R.layout.planning, null);
        viewPager = view.findViewById(R.id.view_pager)
        viewPager.adapter = PageAdapter((context as AppCompatActivity).supportFragmentManager, PAGES, this) {
            delegate?.invoke(it)
            lastClickedItem = it

            /*if (context is AppCompatActivity) {
                (context as AppCompatActivity).runOnUiThread {
                    viewPager.adapter?.notifyDataSetChanged()
                }
            }*/
        }
        viewPager.offscreenPageLimit = 1
        viewPager.setCurrentItem(PAGES / 2, false)
        progressBar = view.findViewById(R.id.progress_bar)

        addView(view)
        requestLayout()
    }

    private fun initAttributes(attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray = context.theme.obtainStyledAttributes(
                    attrs,
                    R.styleable.TimeTable,
                    0, 0)
            try {
                titlesColor = typedArray.getColor(R.styleable.TimeTable_calTitlesColor, Color.BLACK)
                nowTitlesColor = typedArray.getColor(R.styleable.TimeTable_calNowTitlesColor, Color.BLUE)
            } finally {
                typedArray.recycle()
            }
        }
    }

    override fun getNowTitlesColor(): Int {
        return titlesColor
    }

    override fun getTitlesColor(): Int {
        return nowTitlesColor
    }

    override fun getColumnCount(): Int {
        return columnCount
    }

    override fun getCurrentDayColor(): Int {
        return currentDayColor
    }

    override fun getDateForCenter(): Date {
        return centerDate
    }

    override fun getLastClickedPlanItem(): IPlanItem? {
        return lastClickedItem
    }

    fun setEvents(events: List<IEventItem>) {
        PlanningModule.instance.events = events
    }

    fun setItems(items: List<IPlanItem>) {
        planItems = items
        progressBar.visibility = GONE

        if (context is AppCompatActivity) {
            (context as AppCompatActivity).runOnUiThread {
                viewPager.adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun getChapters(): List<IPlanChapter> {
        return listOf(PlanItemChapter("", planItems ?: emptyList()))
    }

    override fun getType(): PlanType {
        return PlanType.ITEMS
    }

    fun goToToday() {
        if (viewPager.currentItem > (PAGES / 2)) {
            while(viewPager.currentItem != (PAGES / 2) + 1) {
                viewPager.setCurrentItem(viewPager.currentItem - 1, false)
            }

        }
        viewPager.setCurrentItem(PAGES / 2, true)
    }

    fun openSettings(rowHeightLocalized: String?, closeLocalized: String?) {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater!!.inflate(R.layout.employee_planning_settings, null)

        val rowHeightText: TextView = view.findViewById(R.id.row_height_text)
        rowHeightText.text = rowHeightLocalized ?: "Row height"
        val rowHeightValue: TextView = view.findViewById(R.id.row_height_value)
        val valueText = String.format(Locale.US, "%.2f", PlanningModule.instance.preferredBarHeightMultiplier) + "x"
        rowHeightValue.text = valueText

        val rowHeightAddBtn: AppCompatButton = view.findViewById(R.id.row_height_add_btn)
        rowHeightAddBtn.setOnClickListener { _ ->
            PlanningModule.instance.preferredBarHeightMultiplier += 0.1f
            if (PlanningModule.instance.preferredBarHeightMultiplier > 2.0f)
                PlanningModule.instance.preferredBarHeightMultiplier = 2.0f
            val valueText = String.format(Locale.US, "%.2f", PlanningModule.instance.preferredBarHeightMultiplier) + "x"
            rowHeightValue.text = valueText

            viewPager.adapter?.notifyDataSetChanged()
        }
        val rowHeightSubstractBtn: AppCompatButton = view.findViewById(R.id.row_height_substract_btn)
        rowHeightSubstractBtn.setOnClickListener { _ ->
            PlanningModule.instance.preferredBarHeightMultiplier -= 0.1f
            if (PlanningModule.instance.preferredBarHeightMultiplier < 0.1f)
                PlanningModule.instance.preferredBarHeightMultiplier = 0.1f
            val valueText = String.format(Locale.US, "%.2f", PlanningModule.instance.preferredBarHeightMultiplier) + "x"
            rowHeightValue.text = valueText

            viewPager.adapter?.notifyDataSetChanged()
        }

        builder.setView(view)
        builder.setPositiveButton(closeLocalized ?: "close") { dialog, _ -> dialog?.dismiss() }
        builder.create().show()
    }


}