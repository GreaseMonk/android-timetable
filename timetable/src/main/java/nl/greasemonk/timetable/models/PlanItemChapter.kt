/*
 * Copyright (c) 2018 .
 * This file is released under the Apache-2.0 license and part of the TimeTable repository located at:
 * https://github.com/greasemonk/android-timetable-core
 * See the "LICENSE" file at the root of the repository or go to the address below for the full license details.
 * https://github.com/GreaseMonk/android-timetable-core/blob/develop/LICENSE
 */

package nl.greasemonk.timetable.models

import nl.greasemonk.timetable.interfaces.IPlanChapter
import nl.greasemonk.timetable.interfaces.IPlanItem

open class PlanItemChapter<T: IPlanItem>(
        override val chapterName: String,
        override val chapterPlanItems: List<T>
) : IPlanChapter {

}