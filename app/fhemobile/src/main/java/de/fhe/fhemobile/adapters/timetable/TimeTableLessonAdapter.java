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
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.views.timetable.MyTimeTableView;
import de.fhe.fhemobile.vos.timetable.FlatDataStructure;

public class TimeTableLessonAdapter extends BaseAdapter {
	private static final String TAG = "TimeTableLessonAdapter";

	//private String lessonTitle="";
	//private String studygroupTitle="";

	private final Context context;
	public TimeTableLessonAdapter(Context context) {

		this.context=context;
	}

	@Override
	public int getCount() {
		return MyTimeTableView.getCompleteLessons().size();
	}

	@Override
	public Object getItem(final int position) {
		return MyTimeTableView.getCompleteLessons().get(position);
	}

	@Override
	public long getItemId(final int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).
					inflate(R.layout.row_layout_events, parent, false);
		}
		final FlatDataStructure currentItem = MyTimeTableView.getCompleteLessons().get(position);
		final RelativeLayout layout = (RelativeLayout)convertView.findViewById(R.id.singleRowLayout);

		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				final List<FlatDataStructure> lessonTitleFilteredList = FlatDataStructure.queryGetEventsByEventTitle(MyTimeTableView.getCompleteLessons(),FlatDataStructure.cutEventTitle(currentItem.getEvent().getTitle()));
				final List <FlatDataStructure> filteredList = FlatDataStructure.queryGetEventsByStudyGroupTitle(lessonTitleFilteredList,currentItem.getStudyGroup().getTitle());
				for (FlatDataStructure event:filteredList){
					event.setVisible(!event.isVisible());
				}
				((ListView)parent).invalidateViews();
//				Log.d(TAG, "onClick: VisibleClick");

			}
		});

		if(currentItem.isVisible()){
			convertView.setLayoutParams(new AbsListView.LayoutParams(-1,0));
			convertView.setVisibility(View.VISIBLE);
		}
		else{
			convertView.setLayoutParams(new AbsListView.LayoutParams(-1,1));
			convertView.setVisibility(View.GONE);
		}

		final TextView lessonTitle = (TextView)convertView.findViewById(R.id.tvLessonTitle);
		final RelativeLayout headerLayout = convertView.findViewById(R.id.headerBackground);

		if(position==0){
			lessonTitle.setText(FlatDataStructure.cutEventTitle(currentItem.getEvent().getTitle()));
			lessonTitle.setVisibility(View.VISIBLE);
			convertView.setLayoutParams(new AbsListView.LayoutParams(-1,0));
			convertView.setVisibility(View.VISIBLE);
			headerLayout.setVisibility(View.VISIBLE);

		}

		else if(!FlatDataStructure.cutEventTitle(MyTimeTableView.getCompleteLessons().get(position).getEvent().getTitle()).equals(FlatDataStructure.cutEventTitle(MyTimeTableView.getCompleteLessons().get(position-1).getEvent().getTitle()))){
			lessonTitle.setText(FlatDataStructure.cutEventTitle(currentItem.getEvent().getTitle()));
			lessonTitle.setVisibility(View.VISIBLE);
			convertView.setLayoutParams(new AbsListView.LayoutParams(-1,0));
			convertView.setVisibility(View.VISIBLE);
			headerLayout.setVisibility(View.VISIBLE);
		}
		else{
			lessonTitle.setVisibility(View.GONE);
			headerLayout.setVisibility(View.GONE);
		}



		final TextView studyGroupTitle = (TextView)convertView.findViewById(R.id.tvStudyGroupTitle);
		final ImageButton btnAddLesson = (ImageButton)convertView.findViewById(R.id.ibAddLesson);
		btnAddLesson.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {

				final List<FlatDataStructure> eventFilteredList = FlatDataStructure.queryGetEventsByEventTitle(MyTimeTableView.getCompleteLessons(),FlatDataStructure.cutEventTitle(currentItem.getEvent().getTitle()));
				final List<FlatDataStructure> studyGroupFilteredList = FlatDataStructure.queryGetEventsByStudyGroupTitle(eventFilteredList,currentItem.getStudyGroup().getTitle());

				for(FlatDataStructure event : studyGroupFilteredList){
					MyTimeTableView.addLesson(event);
				}

			}

		});



		if(position==0){
			studyGroupTitle.setText(currentItem.getStudyGroup().getTitle());
			studyGroupTitle.setVisibility(View.VISIBLE);
			btnAddLesson.setVisibility(View.VISIBLE);
			convertView.setLayoutParams(new AbsListView.LayoutParams(-1,0));
			convertView.setVisibility(View.VISIBLE);


		}

		else if(!FlatDataStructure.cutEventTitle(currentItem.getEvent().getTitle()).equals(
					FlatDataStructure.cutEventTitle(MyTimeTableView.getCompleteLessons().get(position-1).getEvent().getTitle())))
		{
			studyGroupTitle.setText(currentItem.getStudyGroup().getTitle());
			studyGroupTitle.setVisibility(View.VISIBLE);
			btnAddLesson.setVisibility(View.VISIBLE);
			convertView.setLayoutParams(new AbsListView.LayoutParams(-1,0));
			convertView.setVisibility(View.VISIBLE);

		}
		else if(!currentItem.getStudyGroup().getTitle().equals(
				MyTimeTableView.getCompleteLessons().get(position-1).getStudyGroup().getTitle()))
		{
			studyGroupTitle.setText(currentItem.getStudyGroup().getTitle());
			studyGroupTitle.setVisibility(View.VISIBLE);
			btnAddLesson.setVisibility(View.VISIBLE);
			convertView.setLayoutParams(new AbsListView.LayoutParams(-1,0));
			convertView.setVisibility(View.VISIBLE);
		}
		else{
			studyGroupTitle.setVisibility(View.GONE);
			btnAddLesson.setVisibility(View.GONE);
		}

		final TextView tvTime = (TextView) convertView.findViewById(R.id.tvLessonTime);
		final Date df = new java.util.Date(currentItem.getEvent().getStartDate());
		final String date = new SimpleDateFormat("dd.MM.yyyy").format(df);
		tvTime.setText(date + " " + currentItem.getEvent().getStartTime() + "-" + currentItem.getEvent().getEndTime());

		final TextView tvRoom = (TextView)convertView.findViewById(R.id.tvRoom);
		tvRoom.setText(currentItem.getEvent().getRoom());

		return convertView;
	}
}
