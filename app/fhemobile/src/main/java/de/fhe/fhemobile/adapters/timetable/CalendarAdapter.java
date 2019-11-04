/*
 * Copyright (c) 2014-2019 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fhe.fhemobile.adapters.timetable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.views.timetable.MyTimeTableView;
import de.fhe.fhemobile.vos.timetable.FlatDataStructure;

public class CalendarAdapter extends BaseAdapter {

	private Context context;
	public CalendarAdapter(Context context) {
		this.context=context;
	}

	@Override
	public int getCount() {
		return MyTimeTableView.getSortedLessons().size();
	}

	@Override
	public Object getItem(int position) {
		return MyTimeTableView.getSortedLessons().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private final static SimpleDateFormat sdf= new SimpleDateFormat("dd.MM.yyyy");

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).
					inflate(R.layout.row_layout_calendar, parent, false);
		}
		final FlatDataStructure currentItem = MyTimeTableView.getSortedLessons().get(position);
		RelativeLayout layout = (RelativeLayout)convertView.findViewById(R.id.singleRowLayout);

		RelativeLayout headerLayout = convertView.findViewById(R.id.headerBackground);

		final TextView lessonDay = (TextView)convertView.findViewById(R.id.tvLessonDay);
		lessonDay.setText(currentItem.getEvent().getDayOfWeek());

		final TextView lessonDate = (TextView)convertView.findViewById(R.id.tvLessonDate);
		lessonDate.setText(currentItem.getEvent().getDate());


		final TextView lessonTitle = (TextView)convertView.findViewById(R.id.tvTitle);
		lessonTitle.setText(currentItem.getEvent().getShortTitle());


		final TextView lessonTime = (TextView)convertView.findViewById(R.id.tvLessonTime);
		final Date df = new Date(currentItem.getEvent().getStartDate());
		//String date = sdf.format(df);
		lessonTime.setText(currentItem.getEvent().getStartTime()+"-"+currentItem.getEvent().getEndTime());

		TextView lessonRoom = (TextView)convertView.findViewById(R.id.tvRoom);
		lessonRoom.setText(currentItem.getEvent().getRoom());

		TextView lessonLecturer = (TextView)convertView.findViewById(R.id.tvLecturer);
		lessonLecturer.setText(currentItem.getEvent().getLecturer());

		if(position==0){
			headerLayout.setVisibility(View.VISIBLE);
			lessonDay.setVisibility(View.VISIBLE);
			lessonDate.setVisibility(View.VISIBLE);
		}
		else if(!currentItem.getEvent().getDate().equals(MyTimeTableView.getSortedLessons().get(position-1).getEvent().getDate())){
			headerLayout.setVisibility(View.VISIBLE);
			lessonDay.setVisibility(View.VISIBLE);
			lessonDate.setVisibility(View.VISIBLE);
		}
		else{
			headerLayout.setVisibility(View.GONE);
			lessonDay.setVisibility(View.GONE);
			lessonDate.setVisibility(View.GONE);
		}
		if(sdf.format(df).compareTo(sdf.format(new Date()))==0){
			lessonDay.setText("Heute "+"("+currentItem.getEvent().getDayOfWeek()+")");
		}

		return convertView;
	}
}
