/*
 * Copyright (c) 2018 .
 * This file is released under the Apache-2.0 license and part of the TimeTable repository located at:
 * https://github.com/greasemonk/android-timetable-core
 * See the "LICENSE" file at the root of the repository or go to the address below for the full license details.
 * https://github.com/GreaseMonk/android-timetable-core/blob/develop/LICENSE
 */

package nl.greasemonk.timetable

import nl.greasemonk.timetable.enums.PlanViewMode
import nl.greasemonk.timetable.interfaces.IEventItem

internal class PlanningModule {
    companion object {
        val instance: PlanningModule = PlanningModule()
    }

    var preferredBarHeightMultiplier = 1f
    var mode: PlanViewMode = PlanViewMode.WEEK

    var events: List<IEventItem> = mutableListOf()
}