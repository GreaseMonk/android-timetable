/*
 * Copyright (c) 2018 .
 * This file is released under the Apache-2.0 license and part of the TimeTable repository located at:
 * https://github.com/greasemonk/android-timetable-core
 * See the "LICENSE" file at the root of the repository or go to the address below for the full license details.
 * https://github.com/GreaseMonk/android-timetable-core/blob/develop/LICENSE
 */

package nl.greasemonk.timetable.app

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import nl.greasemonk.timetable.EmployeePlanning
import nl.greasemonk.timetable.app.models.MyEmployee
import nl.greasemonk.timetable.app.models.MyPlanItem
import nl.greasemonk.timetable.enums.Recurrence
import nl.greasemonk.timetable.enums.Style
import nl.greasemonk.timetable.models.TimeRange
import java.util.*
import kotlin.math.max
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    val subjects: List<MyEmployee> = listOf(
            MyEmployee("Han Solo"),
            MyEmployee("Count Dooku"),
            MyEmployee("Leia"),
            MyEmployee("Chewbacca"),
            MyEmployee("Yoda"),
            MyEmployee("Obi-Wan Kenobi"),
            MyEmployee("Darth Vader")
    )
    val names = listOf<String>(
            "Invade planets",
            "Launch into space",
            "Tidy space ship",
            "Lightsaber fight",
            "Eliminate droids",
            "Man the turrets"
    )
    val colors = listOf(
            Color.RED,
            Color.BLUE,
            Color.CYAN,
            Color.DKGRAY,
            Color.GREEN,
            Color.MAGENTA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val planView: EmployeePlanning = findViewById(R.id.plan_view)
        planView.setItems(generateData())

        showDialog()
    }

    fun showDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Introduction")
        builder.setMessage("Swipe left/right to change weeks.\n\n" +
                "Clicking an item will call the click listener and highlight the item. " +
                "A planitem can contain multiple 'subjects' (ie. Employee names, study class names, department names, etc.).")
        builder.setPositiveButton("GOT IT!", null)
        builder.create().show()
    }

    fun generateData(): List<MyPlanItem> {
        val list: MutableList<MyPlanItem> = mutableListOf()
        val calendar: Calendar = Calendar.getInstance()

        var i: Int = 0
        while (i < 40) {
            var subjectCount = Random.nextInt(1,5) // max amount of subjects on 1 item
            val subjects = mutableListOf<MyEmployee>()
            while (subjectCount > 0) {
                subjects.add(this.subjects[Random.nextInt(0, this.subjects.count()-1)])
                subjectCount -= 1
            }
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.HOUR, Random.nextInt(0, 72))
            calendar.add(Calendar.HOUR, -Random.nextInt(0, 48))
            val start = calendar.timeInMillis
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.HOUR, Random.nextInt(0, 48))
            val end = calendar.timeInMillis

            list.add(MyPlanItem(
                    names.random(),
                    colors.random(),
                    Style.DEFAULT,
                    subjects,
                    TimeRange(start, end, Recurrence.ONCE)
            ))
            i += 1
        }


        return list.toList()
    }
}
