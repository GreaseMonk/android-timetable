/*
 * Copyright (c) 2018 .
 * This file is released under the Apache-2.0 license and part of the TimeTable repository located at:
 * https://github.com/greasemonk/android-timetable-core
 * See the "LICENSE" file at the root of the repository or go to the address below for the full license details.
 * https://github.com/GreaseMonk/android-timetable-core/blob/develop/LICENSE
 */

package nl.greasemonk.timetable

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import nl.greasemonk.timetable.models.TimeRange
import java.util.*

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun Date.startOfMonth(): Date {
    val calendar = GregorianCalendar()
    calendar.timeInMillis = this.time
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    calendar.add(Calendar.DAY_OF_YEAR, -calendar.get(Calendar.DAY_OF_MONTH))
    return calendar.time
}

fun Date.endOfMonth(): Date {
    val calendar = GregorianCalendar()
    calendar.timeInMillis = this.time
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    calendar.add(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH))
    return calendar.time
}

fun Date.startOfWeek(): Date {
    val calendar = GregorianCalendar()
    calendar.timeInMillis = this.time
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    return calendar.time
}

fun Date.endOfWeek(): Date {
    val calendar = GregorianCalendar()
    calendar.timeInMillis = this.time
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 0)
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    calendar.add(Calendar.DAY_OF_YEAR, 6)
    return calendar.time
}

fun TimeRange.contains(date: Date): Boolean {
    return contains(date.time)
}

fun TimeRange.overlaps(with: TimeRange, countSameDayAsOverlap: Boolean = false): Boolean {
    var overlaps = false
    // DeMorgan's laws
    if (start <= with.endInclusive && endInclusive >= with.start)
        overlaps = true
    if (!overlaps && countSameDayAsOverlap) {
        val calendar = GregorianCalendar.getInstance()
        calendar.timeInMillis = start

        val lhsStartDay = calendar.get(Calendar.DAY_OF_YEAR)

        calendar.timeInMillis = with.start
        val rhsStartDay = calendar.get(Calendar.DAY_OF_YEAR)

        calendar.timeInMillis = endInclusive
        val lhsEndDay = calendar.get(Calendar.DAY_OF_YEAR)

        calendar.timeInMillis = with.endInclusive
        val rhsEndDay = calendar.get(Calendar.DAY_OF_YEAR)

        if (lhsStartDay == rhsStartDay || lhsStartDay == rhsEndDay)
            overlaps = true
        else if (lhsEndDay == rhsStartDay || lhsEndDay == rhsEndDay)
            overlaps = true
    }
    return overlaps
}

@ColorInt
fun Int.lighter(): Int {
    val hsv = FloatArray(3)
    Color.colorToHSV(this, hsv)
    hsv[2] *= 0.8f // value component
    return Color.HSVToColor(hsv)
}

@ColorInt
fun Int.invert(): Int {
    val hsv = FloatArray(3)
    Color.RGBToHSV(Color.red(this), Color.green(this),
            Color.blue(this), hsv)
    hsv[0] = (hsv[0] + 180) % 360
    return Color.HSVToColor(hsv)
}

fun <T : View> ViewGroup.getViewsByType(tClass: Class<T>): ArrayList<T> {
    val result = ArrayList<T>()
    val childCount = this.childCount
    for (i in 0 until childCount) {
        val child = this.getChildAt(i)
        if (child is ViewGroup)
            result.addAll(child.getViewsByType(tClass))

        if (tClass.isInstance(child))
            result.add(tClass.cast(child)!!)
    }
    return result
}