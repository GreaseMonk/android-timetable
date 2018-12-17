/*
 * Copyright (c) 2018 .
 * This file is released under the Apache-2.0 license and part of the TimeTable repository located at:
 * https://github.com/greasemonk/android-timetable-core
 * See the "LICENSE" file at the root of the repository or go to the address below for the full license details.
 * https://github.com/GreaseMonk/android-timetable-core/blob/develop/LICENSE
 */

package nl.greasemonk.timetable.planning

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import nl.greasemonk.timetable.*
import nl.greasemonk.timetable.interfaces.IPlanChapter
import nl.greasemonk.timetable.interfaces.IPlanItem
import nl.greasemonk.timetable.interfaces.IPlanSubject
import nl.greasemonk.timetable.models.TimeRange

internal class RowAdapter constructor(val chapter:IPlanChapter, val timeRange: TimeRange, val delegate: PlanningDelegate, val listener: (IPlanItem) -> Unit): RecyclerView.Adapter<RowAdapter.ViewHolder>() {

    internal data class DataHolder(
            val subject: IPlanSubject<*>? = null,
            var items: MutableList<IPlanItem>
    )

    private var items: MutableList<DataHolder> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.planning_chapter_row))
    override fun getItemCount() = items.count()
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(timeRange, delegate, items[position], listener)

    init {
        for(item: IPlanItem in chapter.chapterPlanItems) {
            if(item.timeRange.overlaps(timeRange, true)) {
                if(item.planItemSubjects == null || item.planItemSubjects!!.isEmpty()) {
                    continue
                }

                for(i in 0 until item.planItemSubjects!!.sortedBy { it.planSubjectName }.size) {
                    val subject = item.planItemSubjects!![i]
                    val targetHolder: DataHolder? = items.firstOrNull { it.subject!!.planSubjectIdentifier == subject.planSubjectIdentifier }

                    if(targetHolder != null) {
                        items[items.indexOf(targetHolder)].items.add(item)
                    } else {
                        items.add(DataHolder(subject, mutableListOf(item)))
                    }

                }
            }
        }
        items = items.sortedBy { it.subject!!.planSubjectName }.toMutableList()
    }

    internal class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var textView: TextView = itemView.findViewById(R.id.text1)
        private var planView: PlanView = itemView.findViewById(R.id.plan_view)

        fun bind(timeRange: TimeRange, delegate: PlanningDelegate, item: DataHolder, listener: (IPlanItem) -> Unit) = with(itemView) {
            textView.text = item.subject!!.planSubjectName
            planView.delegate = delegate
            planView.setItems(PlanningModule.instance.mode, timeRange, item.items)
            planView.clickListener = listener
        }
    }
}