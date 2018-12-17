/*
 * Copyright (c) 2018 .
 * This file is released under the Apache-2.0 license and part of the TimeTable repository located at:
 * https://github.com/greasemonk/android-timetable-core
 * See the "LICENSE" file at the root of the repository or go to the address below for the full license details.
 * https://github.com/GreaseMonk/android-timetable-core/blob/develop/LICENSE
 */

package nl.greasemonk.timetable.planning

import androidx.annotation.ColorInt
import nl.greasemonk.timetable.enums.PlanType
import nl.greasemonk.timetable.interfaces.IPlanChapter
import nl.greasemonk.timetable.interfaces.IPlanItem
import java.util.*

internal interface PlanningDelegate {
    @ColorInt
    fun getTitlesColor(): Int
    @ColorInt
    fun getNowTitlesColor(): Int
    fun getColumnCount(): Int
    @ColorInt
    fun getCurrentDayColor(): Int
    fun getDateForCenter(): Date

    /**
     * Get the chapters to show in the planning view.
     *
     * @return the chapters to show
     */
    fun getChapters(): List<IPlanChapter>

    /**
     * Get the type of planning. This is important for the adapter
     * to distinguish using plain items or chapters.
     *
     * @return the type of planning
     */
    fun getType(): PlanType
    fun getLastClickedPlanItem(): IPlanItem?

    var showHeaders: Boolean
    var showItemsAtExactTime: Boolean
}