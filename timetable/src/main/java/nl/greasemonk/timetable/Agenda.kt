/*
 * Copyright (c) 2018 .
 * This file is released under the Apache-2.0 license and part of the TimeTable repository located at:
 * https://github.com/greasemonk/android-timetable-core
 * See the "LICENSE" file at the root of the repository or go to the address below for the full license details.
 * https://github.com/GreaseMonk/android-timetable-core/blob/develop/LICENSE
 */

package nl.greasemonk.timetable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager


/*****************************
 *
 *  WORK IN PROGRESS
 *
 *****************************/
class Agenda : androidx.fragment.app.Fragment() {

    private lateinit var viewPager: androidx.viewpager.widget.ViewPager
    private var mFocusedPage = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.agenda_fragment, container, false)

        viewPager = view.findViewById(R.id.view_pager)
        viewPager.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // Do nothing
            }

            override fun onPageSelected(position: Int) {
                // Do nothing
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == androidx.viewpager.widget.ViewPager.SCROLL_STATE_IDLE) {
                    if (mFocusedPage == 0) {
                        // move data from the center to the right page

                    } else if (mFocusedPage == 2) {
                        // move data from the center to the left page

                    }

                    // go back to the center allowing to scroll indefinitely
                    viewPager.setCurrentItem(1, false);
                }
            }
        })

        return view
    }
}