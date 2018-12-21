[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.greasemonk/timetable/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.github.greasemonk/timetable) [![API](https://img.shields.io/badge/API-16%2B-yellow.svg?style=flat)](https://android-arsenal.com/api?level=16)
android-timetable
===================

A timetable designed for planning employees to projects.

<p align="center">
<img src="https://github.com/GreaseMonk/android-timetable/blob/develop/screenshots/device-2018-12-18-000422.png" height="500"></p>

# Features
- Thin bars on top ('Events')
- Weekly view (more on roadmap incoming)
- Two drawing styles, Default and Diagonal lines
- Colored bars
- You don't need to create duplicate PlanItem objects. Just return the subjects in the planitem for whom or what they are for.
- Red 'Today' line

# Concept
All items have a 'TimeRange' which are easily instantiated with TimeRange(from, to) and an optional 'recurrence' variable. If an item is recurring (daily, weekly, monthly, yearly, etc.) you will be able to specify that in this time range if it is supported. (Keep in mind this is a feature still in progress on the roadmap!)

Items with the same start and end timestamp count as 00:00 to 23:59.
Items with end date before start date are ignored and thus not displayed.

The PlanItem class or the IPlanItem interface are meant for regular planitems. Specify a name, optional color, style (default or diagonal lines), and subjects. 

About the 'List<IPlanSubject<*>>', it's nothing scary. At all. In fact, let me demo how you create a planitem which just the objects supplied from the library. Remember, you don't need to implement the interface on your objects, you can instantiate them the following way:

```kotlin
// We're going to make a plan item for thomas and federica going on vacation.
// The identifier and name are the same for demo purposes.
// The identifier must implement Comparable - Integer, String, etc.
val thomas = PlanItemSubject("Thomas Schultz", "Thomas Schultz")
val federica = PlanItemSubject("Federica Carraro", "Federica Carraro")

// Let's create a vacation from today, lasting 23 days
val cal = Calendar.getInstance()
val startMillis = cal.timeInMillis
cal.add(Calendar.DAY_OF_YEAR, 23)
val endMillis = cal.timeInMillis

val vacation = PlanItem("Vacation to Italy",
            		TimeRange(startMillis, endMillis), 
            		listOf(thomas, federica), 
            		Color.GREEN, 
            		Style.DEFAULT)
```

# Roadmap
Q1 2019
- More (working) recurring TimeRange options: Hourly, Daily, Weekly, Monthly, Yearly
- Better text rendering for subjects and planitems
- Dynamic loading delegate for the implementation of http paging
- Month view
- 'Agenda' view (day view)

Q2 2019
- Custom TimeRange option so you or your user can specify your own
- More styles for your planitems

# How to contribute
- Fork the repository
- Coffee
- Code
- Commit
- Send me your pull request
- Commits are reviewed before approval

# Installation
For now, you can only import the module.
Dependency deployment will become available soon.

# Usage

### 1. Include the layout in your XML

```xml
<nl.greasemonk.timetable.EmployeePlanning
        android:id="@+id/plan_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```


### 2. Implement your class with IPlanItem (or extend [PlanItem](https://github.com/GreaseMonk/android-timetable/blob/develop/timetable/src/main/java/nl/greasemonk/timetable/models/PlanItem.kt) )

As for the name on the left side (ie. employee name, class name, department name, etc.), you can implement [IPlanSubject](https://github.com/GreaseMonk/android-timetable/blob/develop/timetable/src/main/java/nl/greasemonk/timetable/interfaces/IPlanSubject.kt), or extend [PlanItemSubject](https://github.com/GreaseMonk/android-timetable/blob/develop/timetable/src/main/java/nl/greasemonk/timetable/models/PlanItemSubject.kt) )

For an example of your Android Activity, please see the [Demo Activity](https://github.com/GreaseMonk/android-timetable/blob/develop/app/src/main/java/nl/greasemonk/timetable/app/MainActivity.kt).


```kotlin
open class PlanItem(
        override val planItemName: String,
        override val timeRange: TimeRange,
        override val planItemSubjects: List<IPlanSubject<*>>?,
        override val planItemColor: Int?,
        override val planStyle: Style) : IPlanItem
```


### 3. Fill the table with data

```java
val planning: EmployeePlanning = findViewById(R.id.plan_view);
planning.setItems(getMyData());
```

## License

```
		Copyright 2018 Wiebe Geertsma

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
```
