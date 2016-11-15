package com.greasemonk.timetable.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.greasemonk.timetable.AbstractRowItem;
import com.greasemonk.timetable.TimeTable;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
	private static final int GENERATED_AMOUNT = 20;
	private TimeTable timeTable;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		timeTable = (TimeTable) findViewById(R.id.time_table);
		timeTable.setColumnCount(7);
		timeTable.update(generateSamplePlanData());
	}
	
	private static List<EmployeePlanItem> generateSamplePlanData()
	{
		List<EmployeePlanItem> planItems = new ArrayList<>();
		for(int i = 0; i < GENERATED_AMOUNT; i++)
			planItems.add(EmployeePlanItem.generateSample());
		
		return planItems;
	}
}
