/*
 * Copyright (c) 2018 .
 * This file is released under the Apache-2.0 license and part of the TimeTable repository located at:
 * https://github.com/greasemonk/android-timetable-core
 * See the "LICENSE" file at the root of the repository or go to the address below for the full license details.
 * https://github.com/GreaseMonk/android-timetable-core/blob/develop/LICENSE
 */

package nl.greasemonk.timetable.interfaces

import nl.greasemonk.timetable.enums.Style

/**
 * Interface to be implemented on objects that have no
 * subject(s) and should be shown on the top of the planning
 */
interface IEventItem: IPlannable {
    val eventName: String
    val eventColor: Int?
    val eventStyle: Style
}