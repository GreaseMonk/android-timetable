/*
 * Copyright (c) 2018 .
 * This file is released under the Apache-2.0 license and part of the TimeTable repository located at:
 * https://github.com/greasemonk/android-timetable-core
 * See the "LICENSE" file at the root of the repository or go to the address below for the full license details.
 * https://github.com/GreaseMonk/android-timetable-core/blob/develop/LICENSE
 */

package nl.greasemonk.timetable.models

import nl.greasemonk.timetable.enums.Style
import nl.greasemonk.timetable.interfaces.IPlanItem
import nl.greasemonk.timetable.interfaces.IPlanSubject

open class PlanItem(
        override val planItemName: String,
        override val timeRange: TimeRange,
        override val planItemSubjects: List<IPlanSubject<*>>?,
        override val planItemColor: Int?,
        override val planStyle: Style) : IPlanItem