/*
 * Copyright (c) 2018 .
 * This file is released under the Apache-2.0 license and part of the TimeTable repository located at:
 * https://github.com/greasemonk/android-timetable-core
 * See the "LICENSE" file at the root of the repository or go to the address below for the full license details.
 * https://github.com/GreaseMonk/android-timetable-core/blob/develop/LICENSE
 */

package nl.greasemonk.timetable.interfaces

interface IPlanSubject<T: Comparable<T>> {
    /**
     * When you have the same planSubjectName, identify the difference
     * by setting the subject identifier.
     *
     * This can be any comparable such as an integer, string, or your own comparable implementation
     * As long as this identifier is unique to your list of objects that extend IPlanSubject,
     * you have nothing to worry about.
     */
    val planSubjectIdentifier: T
    /**
     * The string to display on the left of the screen.
     */
    val planSubjectName: String
}