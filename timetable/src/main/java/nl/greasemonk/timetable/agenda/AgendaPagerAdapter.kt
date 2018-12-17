/*
 * Copyright (c) 2018 .
 * This file is released under the Apache-2.0 license and part of the TimeTable repository located at:
 * https://github.com/greasemonk/android-timetable-core
 * See the "LICENSE" file at the root of the repository or go to the address below for the full license details.
 * https://github.com/GreaseMonk/android-timetable-core/blob/develop/LICENSE
 */

package nl.greasemonk.timetable.agenda


class AgendaPagerAdapter(fragmentManager: androidx.fragment.app.FragmentManager): androidx.fragment.app.FragmentPagerAdapter(fragmentManager) {
    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}