package com.greasemonk.timetable.app;

import com.greasemonk.timetable.AbstractRowItem;

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
public class EmployeePlanItem implements AbstractRowItem
{
	
	private String employeeName, projectName;
	private Date planStart, planEnd;
	
	public EmployeePlanItem() {}
	
	public EmployeePlanItem(String employeeName, String projectName, Date planStart, Date planEnd)
	{
		this.employeeName = employeeName;
		this.projectName = projectName;
		this.planStart = planStart;
		this.planEnd = planEnd;
	}
	
	public static EmployeePlanItem generateSample()
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
		
		return new EmployeePlanItem(firstNameSamples[rand.nextInt(firstNameSamples.length)] + " " +
				lastNameSamples[rand.nextInt(lastNameSamples.length)],
				projectNames[rand.nextInt(projectNames.length)],
				start.getTime(),
				end.getTime());
	}
	
	@Override
	public Date getPlanningStart()
	{
		return planStart;
	}
	
	@Override
	public Date getPlanningEnd()
	{
		return planEnd;
	}
	
	@Override
	public String getEmployeeName()
	{
		return employeeName;
	}
	
	@Override
	public String getProjectName()
	{
		return projectName;
	}
}
