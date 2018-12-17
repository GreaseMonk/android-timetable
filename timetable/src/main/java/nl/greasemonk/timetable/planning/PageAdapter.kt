/*
 * Copyright (c) 2018 .
 * This file is released under the Apache-2.0 license and part of the TimeTable repository located at:
 * https://github.com/greasemonk/android-timetable-core
 * See the "LICENSE" file at the root of the repository or go to the address below for the full license details.
 * https://github.com/GreaseMonk/android-timetable-core/blob/develop/LICENSE
 */

package nl.greasemonk.timetable.planning

import nl.greasemonk.timetable.PlanningModule
import nl.greasemonk.timetable.enums.PlanViewMode
import nl.greasemonk.timetable.interfaces.IPlanItem
import java.util.*
import java.util.Calendar.MONTH
import java.util.Calendar.WEEK_OF_YEAR

internal class PageAdapter internal constructor(
    fragmentManager: androidx.fragment.app.FragmentManager,
    val pages: Int,
    val delegate: PlanningDelegate,
    val listener: (IPlanItem) -> Unit) : androidx.fragment.app.FragmentPagerAdapter(fragmentManager) {

    val calendar = GregorianCalendar()

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        //val frag = mFrags.get(position % mFrags.size)
        //val pos = position % 3
        val centerDate = delegate.getDateForCenter()
        val offset = -(pages / 2) + position

        calendar.time = centerDate
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        when (PlanningModule.instance.mode) {
            PlanViewMode.WEEK -> {
                calendar.add(WEEK_OF_YEAR, offset)
            }
            PlanViewMode.MONTH -> {
                calendar.add(MONTH, offset)
            }
        }


        return PageFragment.newInstance(calendar.timeInMillis, delegate, listener)
    }

    override fun getCount(): Int {
        return Int.MAX_VALUE
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }
}