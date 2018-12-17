/*
 * Copyright (c) 2018 .
 * This file is released under the Apache-2.0 license and part of the TimeTable repository located at:
 * https://github.com/greasemonk/android-timetable-core
 * See the "LICENSE" file at the root of the repository or go to the address below for the full license details.
 * https://github.com/GreaseMonk/android-timetable-core/blob/develop/LICENSE
 */

package nl.greasemonk.timetable.planning

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import nl.greasemonk.timetable.*
import nl.greasemonk.timetable.enums.PlanViewMode
import nl.greasemonk.timetable.interfaces.IPlanItem
import nl.greasemonk.timetable.models.TimeRange
import java.text.DateFormatSymbols
import java.util.*
import java.util.Calendar.*
import java.util.concurrent.TimeUnit

internal class PageFragment : androidx.fragment.app.Fragment() {

    companion object {
        internal fun newInstance(time: Long, delegate: PlanningDelegate, listener: (IPlanItem) -> Unit): PageFragment {
            val fragment = PageFragment()
            val date = Date(time)
            when (PlanningModule.instance.mode) {
                PlanViewMode.WEEK -> {
                    fragment.timeRange = TimeRange(date.startOfWeek().time, date.endOfWeek().time)
                }
                PlanViewMode.MONTH -> {
                    fragment.timeRange = TimeRange(date.startOfMonth().time, date.endOfMonth().time)
                }
            }
            fragment.delegate = delegate
            fragment.listener = listener
            return fragment
        }
    }

    private lateinit var delegate: PlanningDelegate
    private lateinit var listener: (IPlanItem) -> Unit
    private lateinit var timeRange: TimeRange
    private lateinit var titleTextView: TextView
    private lateinit var weekNumberTextView: TextView
    private lateinit var textViews: Array<TextView>
    private lateinit var eventViewGroup: LinearLayout
    private lateinit var eventsView: EventPlanView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChapterAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = layoutInflater.inflate(R.layout.planning_page, container, false)
        titleTextView = v.findViewById(R.id.title)
        weekNumberTextView = v.findViewById(R.id.week_number)
        textViews = arrayOf(v.findViewById(R.id.text1),
                v.findViewById(R.id.text2),
                v.findViewById(R.id.text3),
                v.findViewById(R.id.text4),
                v.findViewById(R.id.text5),
                v.findViewById(R.id.text6),
                v.findViewById(R.id.text7),
                v.findViewById(R.id.text8),
                v.findViewById(R.id.text9),
                v.findViewById(R.id.text10),
                v.findViewById(R.id.text11),
                v.findViewById(R.id.text12),
                v.findViewById(R.id.text13),
                v.findViewById(R.id.text14))
        eventViewGroup = v.findViewById(R.id.group3)
        eventsView = v.findViewById(R.id.event_view)
        recyclerView = v.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        eventsView.displayedRange = timeRange
        eventsView.setItems(PlanningModule.instance.mode, timeRange)
        adapter = ChapterAdapter(timeRange, delegate) {
            listener.invoke(it)
            recyclerView.invalidate()
        }
        recyclerView.adapter = adapter
        updateTextFields()

        if (PlanningModule.instance.events.filter {
                    timeRange.overlaps(it.timeRange)
                }.count() == 0)
            eventViewGroup.visibility = View.GONE

        return v
    }

    private fun updateTextFields() {

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeRange.start

        val endWeekCalendar = Calendar.getInstance()
        endWeekCalendar.timeInMillis = timeRange.endInclusive

        weekNumberTextView.text = calendar.get(WEEK_OF_YEAR).toString()

        // Values calculated for the WEEK_OF_YEAR field range from 1 to 53.
        var titleText = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        if (endWeekCalendar.get(MONTH) != calendar.get(MONTH) && endWeekCalendar.get(YEAR) != calendar.get(YEAR)) {
            titleText += " "
            titleText += calendar.get(YEAR).toString()
        }

        if (endWeekCalendar.get(MONTH) != calendar.get(MONTH)) {
            titleText += " \u2014 "
            titleText += endWeekCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        }

        titleText += " "
        titleText += endWeekCalendar.get(YEAR).toString()

        for (i in 0..6) {
            textViews[i].text = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH))
            calendar.add(Calendar.DATE, 1)
        }

        val namesOfDays = DateFormatSymbols.getInstance().getShortWeekdays()
        val current = getTodayColumn()
        if (current >= 0) {
            textViews[current].setTextColor(delegate.getCurrentDayColor())
            textViews[current + 7].setTextColor(delegate.getCurrentDayColor())
        } else {
            for (textView in textViews)
                textView.setTextColor(Color.DKGRAY)
        }
        textViews[7].text = namesOfDays[2]
        textViews[8].text = namesOfDays[3]
        textViews[9].text = namesOfDays[4]
        textViews[10].text = namesOfDays[5]
        textViews[11].text = namesOfDays[6]
        textViews[12].text = namesOfDays[7]
        textViews[13].text = namesOfDays[1]



        titleTextView.text = titleText
    }

    /**
     * Get the column number if the current display contains the current time.
     *
     * @return the column number for the current time, or -1 if non existent in current display
     */
    private fun getTodayColumn(): Int {
        val calendar = Calendar.getInstance()
        // Check if it is the current week we are drawing for the gray highlight for the current day.
        if (calendar.time.time > calendar.time.time && calendar.time.time < calendar.time.time) {
            val difference: Long = calendar.time.time - calendar.time.time
            return TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS).toInt()
        }
        return -1
    }
}