package com.greasemonk.timetable.app;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;
import com.greasemonk.timetable.AbstractGridItem;
import com.greasemonk.timetable.TimeRange;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by Wiebe Geertsma on 15-11-2016.
 * E-mail: e.w.geertsma@gmail.com
 * <p>
 * EmployeePlanItem
 * sample class which is a substitute for your class.
 */
public class EmployeePlanItem extends AbstractGridItem
{
	private String employeeName, projectName;
	private String planName = "-";
	private TimeRange timeRange;
	private Context context;
	
	public EmployeePlanItem() {}
	
	public EmployeePlanItem(Context context, String employeeName, String projectName, Date planStart, Date planEnd)
	{
		this.employeeName = employeeName;
		this.projectName = projectName;
		this.timeRange = new TimeRange(planStart, planEnd);
	}
	
	public static EmployeePlanItem generateSample(Context context)
	{
		final String[] firstNameSamples = {"Kristeen", "Carran", "Lillie", "Marje", "Edith", "Steve", "Henry", "Kyle", "Terrence"};
		final String[] lastNameSamples = {"Woodham", "Boatwright", "Lovel", "Dennel", "Wilkerson", "Irvin", "Aston", "Presley"};
		final String[] projectNames = {"Roof Renovation", "Mall Construction", "Demolition old Hallway"};
		
		// Generate a date range between now and 30 days
		Random rand = new Random();
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		int r1 = -rand.nextInt(12);
		int r2 = rand.nextInt(12);
		start.add(Calendar.DATE, r1);
		end.add(Calendar.DATE, r2);
		
		return new EmployeePlanItem(context, firstNameSamples[rand.nextInt(firstNameSamples.length)] + " " +
				lastNameSamples[rand.nextInt(lastNameSamples.length)],
				projectNames[rand.nextInt(projectNames.length)],
				start.getTime(),
				end.getTime());
	}
	
	@Nullable
	@Override
	public TimeRange getTimeRange()
	{
		return timeRange;
	}
	
	@Override
	public String getName()
	{
		return planName;
	}
	
	@Override
	public String getPersonName()
	{
		return employeeName;
	}
	
	
	/**
	 * OPTIONAL 
	 * 
	 * @return a random color for demo purposes.
	 */
	@Override
	public int getItemColor()
	{
		return Color.argb(48, 0,0,255);
	}
	
	@Override
	public void onClick(View view)
	{
		// Do something here!
		Toast.makeText(context, "Plan name: " + planName + "\n" + "Project name: " + projectName, Toast.LENGTH_LONG).show();
	}
}
