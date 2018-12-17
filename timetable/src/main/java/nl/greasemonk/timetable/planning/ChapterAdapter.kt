/*
 * Copyright (c) 2018 .
 * This file is released under the Apache-2.0 license and part of the TimeTable repository located at:
 * https://github.com/greasemonk/android-timetable-core
 * See the "LICENSE" file at the root of the repository or go to the address below for the full license details.
 * https://github.com/GreaseMonk/android-timetable-core/blob/develop/LICENSE
 */

package nl.greasemonk.timetable.planning

import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import nl.greasemonk.timetable.R
import nl.greasemonk.timetable.inflate
import nl.greasemonk.timetable.interfaces.IPlanChapter
import nl.greasemonk.timetable.interfaces.IPlanItem
import nl.greasemonk.timetable.models.TimeRange
import nl.greasemonk.timetable.overlaps

internal class ChapterAdapter constructor(val timeRange: TimeRange, val delegate: PlanningDelegate, val listener: (IPlanItem) -> Unit): RecyclerView.Adapter<ChapterAdapter.ViewHolder>() {

    // Filter the chapters for items. If we do not filter them here, all chapters will be visible but without any items.
    val items: List<IPlanChapter> = delegate.getChapters().filter {
        it.chapterPlanItems.filter { item ->
            item.timeRange.overlaps(timeRange, true)
        }.count() > 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.planning_chapter))
    override fun getItemCount() = items.count()
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(timeRange, delegate, items[position], listener)



    internal class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.text1)
        private val collapseImageView: ImageView = itemView.findViewById(R.id.collapse_image)
        private val recyclerView: RecyclerView = itemView.findViewById(R.id.recycler_view)
        private val group1: LinearLayout = itemView.findViewById(R.id.group1)

        fun bind(timeRange: TimeRange, delegate: PlanningDelegate, item: IPlanChapter, listener: (IPlanItem) -> Unit) = with(itemView) {
            textView.text = item.chapterName
            recyclerView.adapter = null
            recyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
            recyclerView.adapter = RowAdapter(item, timeRange, delegate, listener)

            if (delegate.showHeaders) {
                group1.setOnClickListener { _ ->
                    val shouldFadeOut = recyclerView.height > 0
                    if (!shouldFadeOut) {
                        recyclerView.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    }
                    val targetHeight = if (shouldFadeOut) 0 else recyclerView.measuredHeight
                    val startValue = if (shouldFadeOut) recyclerView.height else 0
                    val endValue = if (shouldFadeOut) 0 else targetHeight

                    val animator = ValueAnimator.ofInt(startValue, endValue)
                    animator.interpolator = DecelerateInterpolator()
                    animator.duration = 300
                    animator.addUpdateListener {
                        val newValue = it.animatedValue as Int
                        val layoutParams = recyclerView.layoutParams
                        layoutParams.height = newValue
                        recyclerView.layoutParams = layoutParams
                    }
                    animator.start()
                }
            } else {
                group1.visibility = View.GONE
            }

        }
    }
}