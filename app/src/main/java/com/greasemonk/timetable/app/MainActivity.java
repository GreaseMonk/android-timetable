package com.greasemonk.timetable.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.greasemonk.timetable.TimeTable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

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
		
		//List<EmployeePlanItem> items = new ArrayList<>();
		//items.add(new EmployeePlanItem("test", "test", start.toDate(), end.toDate()));
		
		timeTable.update(generateSamplePlanData()); //generateSamplePlanData()
	}
	
	private static List<EmployeePlanItem> generateSamplePlanData()
	{
		List<EmployeePlanItem> planItems = new ArrayList<>();
		for(int i = 0; i < GENERATED_AMOUNT; i++)
			planItems.add(EmployeePlanItem.generateSample());
		
		return planItems;
	}
}
