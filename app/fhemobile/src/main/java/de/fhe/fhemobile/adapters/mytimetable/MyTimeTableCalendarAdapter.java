/*
 *  Copyright (c) 2014-2019 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package de.fhe.fhemobile.adapters.mytimetable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.views.mytimetable.MyTimeTableOverviewView;
import de.fhe.fhemobile.vos.mytimetable.MyTimeTableCourse;

public class MyTimeTableCalendarAdapter extends BaseAdapter {

	private static final String TAG = "MyTimeTableCalendarAdapter";

	private final Context context;
	public MyTimeTableCalendarAdapter(Context context) {
		this.context=context;
	}

	@Override
	public int getCount() {
		return MyTimeTableOverviewView.getSortedCourses().size();
	}

	@Override
	public Object getItem(int position) {
		return MyTimeTableOverviewView.getSortedCourses().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	//private final static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
	private static final DateFormat sdf = SimpleDateFormat.getDateInstance();

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).
					inflate(R.layout.item_my_time_table_calendar, parent, false);
		}
		final MyTimeTableCourse currentItem = MyTimeTableOverviewView.getSortedCourses().get(position);

		RelativeLayout headerLayout = convertView.findViewById(R.id.headerBackground);

		final TextView courseDay = (TextView) convertView.findViewById(R.id.textviewWeekDay);
		courseDay.setText(currentItem.getEvent().getDayOfWeek());

		final TextView courseDate = (TextView) convertView.findViewById(R.id.tvCourseDate);
		courseDate.setText(currentItem.getEvent().getDate());


		final TextView courseTitle = (TextView) convertView.findViewById(R.id.textviewTitle);
		courseTitle.setText(currentItem.getEvent().getShortTitle());


		final TextView courseTime = (TextView) convertView.findViewById(R.id.textCourseTime);
		final Date df = new Date(currentItem.getEvent().getStartDate());
		//String date = sdf.format(df);
		courseTime.setText(currentItem.getEvent().getStartTime() + " – " + currentItem.getEvent().getEndTime()); // $NON-NLS

		TextView courseRoom = (TextView)convertView.findViewById(R.id.textviewRoom);
		courseRoom.setText(currentItem.getEvent().getRoom());

		TextView courseLecturer = (TextView)convertView.findViewById(R.id.textviewLecturer);
		courseLecturer.setText(currentItem.getEvent().getLecturer());

		if(position == 0){
			headerLayout.setVisibility(View.VISIBLE);
			courseDay.setVisibility(View.VISIBLE);
			courseDate.setVisibility(View.VISIBLE);
		}
		else if(!currentItem.getEvent().getDate()
				.equals(MyTimeTableOverviewView.getSortedCourses().get(position - 1).getEvent().getDate())){
			headerLayout.setVisibility(View.VISIBLE);
			courseDay.setVisibility(View.VISIBLE);
			courseDate.setVisibility(View.VISIBLE);
		}
		else{
			headerLayout.setVisibility(View.GONE);
			courseDay.setVisibility(View.GONE);
			courseDate.setVisibility(View.GONE);
		}

		if(sdf.format(df).compareTo(sdf.format(new Date())) == 0){
			courseDay.setText(context.getString(R.string.today) + " ("+currentItem.getEvent().getDayOfWeek() + ")");
		}

		return convertView;
	}
}
