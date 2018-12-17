/*
 * Copyright (c) 2018 .
 * This file is released under the Apache-2.0 license and part of the TimeTable repository located at:
 * https://github.com/greasemonk/android-timetable-core
 * See the "LICENSE" file at the root of the repository or go to the address below for the full license details.
 * https://github.com/GreaseMonk/android-timetable-core/blob/develop/LICENSE
 */

package nl.greasemonk.timetable.app.models

import nl.greasemonk.timetable.interfaces.IPlanSubject

data class MyEmployee(override val planSubjectName: String): IPlanSubject<String> {

    /**
     * For demonstration purposes, i have made the planSubjectName of every employee i have, unique.
     * In your own implementation, use an id or other unique comparable.
     */
    override val planSubjectIdentifier: String
        get() = planSubjectName
}

