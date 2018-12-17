[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.greasemonk/timetable/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.github.greasemonk/timetable) [![API](https://img.shields.io/badge/API-16%2B-yellow.svg?style=flat)](https://android-arsenal.com/api?level=16)
android-timetable
===================

A timetable designed for planning employees to projects.
  
[Screenshot](https://github.com/GreaseMonk/android-timetable/blob/develop/screenshots/device-2018-12-18-000422.png) 


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
<com.greasemonk.timetable.TimeTable android:id="@+id/time_table"
                                        android:layout_width="match_parent"
                                        android:layout_height="wrap_content"/>
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
