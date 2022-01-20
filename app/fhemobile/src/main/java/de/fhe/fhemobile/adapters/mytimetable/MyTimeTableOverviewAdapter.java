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

import static de.fhe.fhemobile.Main.getSubscribedCourses;
import static de.fhe.fhemobile.Main.removeFromSubscribedCourses;
import static de.fhe.fhemobile.utils.Utils.correctUmlauts;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.vos.mytimetable.FlatDataStructure;

public class MyTimeTableOverviewAdapter extends BaseAdapter {
	private static final String TAG = "MyTimeTableOverviewAdapter";
	private final Context context;

	public MyTimeTableOverviewAdapter(Context context) {
		this.context=context;
	}

	@Override
	public int getCount() {
		return getSubscribedCourses().size();
	}

	@Override
	public Object getItem(final int position) {
		return getSubscribedCourses().get(position);
	}

	@Override
	public long getItemId(final int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).
					inflate(R.layout.item_my_time_table_overview, parent, false);
		}
		final FlatDataStructure currentItem = getSubscribedCourses().get(position);
		//final RelativeLayout layout = (RelativeLayout)convertView.findViewById(R.id.singleRowLayout);

		convertView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				List<FlatDataStructure> courseTitleFilteredList
						= FlatDataStructure.getCoursesByEventTitle(
								getSubscribedCourses(),
								FlatDataStructure.cutEventTitle(currentItem.getEvent().getTitle()));

				List <FlatDataStructure> filteredList
						= FlatDataStructure.getCoursesByStudyGroupTitle(
						courseTitleFilteredList, currentItem.getSetString());

				for (FlatDataStructure event:filteredList){
					event.setVisible(!event.isVisible());
				}

				((ListView) parent).invalidateViews();
//				Log.d(TAG, "onClick: VisibleClick");

			}
		});

		if(currentItem.isVisible()){
			convertView.setLayoutParams(new AbsListView.LayoutParams(-1, 0));
			convertView.setVisibility(View.VISIBLE);
		}
		else{
			convertView.setLayoutParams(new AbsListView.LayoutParams(-1, 1));
			convertView.setVisibility(View.GONE);
		}

		final TextView courseTitle = (TextView) convertView.findViewById(R.id.textCourseTitle);
		final RelativeLayout headerBackground = convertView.findViewById(R.id.headerBackground);
		courseTitle.setText(correctUmlauts(FlatDataStructure.cutEventTitle(currentItem.getEvent().getTitle())));

		if(position == 0){
			courseTitle.setVisibility(View.VISIBLE);
			headerBackground.setVisibility(View.VISIBLE);
			convertView.setLayoutParams(new AbsListView.LayoutParams(-1, 0));
			convertView.setVisibility(View.VISIBLE);

		}

		else if(!getSubscribedCourses().get(position)
				.isEqual(getSubscribedCourses().get(position - 1))){

			courseTitle.setVisibility(View.VISIBLE);
			headerBackground.setVisibility(View.VISIBLE);
			convertView.setLayoutParams(new AbsListView.LayoutParams(-1, 0));
			convertView.setVisibility(View.VISIBLE);
		}

		else{
			courseTitle.setVisibility(View.GONE);
			headerBackground.setVisibility(View.GONE);
		}

		final TextView studyGroupLabel = (TextView)convertView.findViewById(R.id.textStudyGroupLabel);
		final TextView studyGroupTitle = (TextView)convertView.findViewById(R.id.textStudyGroupTitle);


		final ImageButton btnRemoveCourse = convertView.findViewById(R.id.btnRemoveCourse);
		btnRemoveCourse.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final List<FlatDataStructure> eventFilteredList
						= FlatDataStructure.getCoursesByEventTitle(
								getSubscribedCourses(),
								FlatDataStructure.cutEventTitle(currentItem.getEvent().getTitle()));

				final List<FlatDataStructure> studyGroupFilteredList
						= FlatDataStructure.getCoursesByStudyGroupTitle(
								eventFilteredList, currentItem.getSetString());

				for(FlatDataStructure event : studyGroupFilteredList){
					removeFromSubscribedCourses(event);
				}

				((ListView) parent).invalidateViews();
			}
		});


		if(position == 0){
			studyGroupTitle.setText(currentItem.getSetString());
			studyGroupTitle.setVisibility(View.VISIBLE);
			studyGroupLabel.setVisibility(View.VISIBLE);
			btnRemoveCourse.setVisibility(View.VISIBLE);
			convertView.setLayoutParams(new AbsListView.LayoutParams(-1,0));
			convertView.setVisibility(View.VISIBLE);
		}
		else if(!currentItem.isEqual(getSubscribedCourses().get(position - 1))){
			studyGroupTitle.setText(currentItem.getSetString());
			studyGroupTitle.setVisibility(View.VISIBLE);
			studyGroupLabel.setVisibility(View.VISIBLE);
			btnRemoveCourse.setVisibility(View.VISIBLE);
			convertView.setLayoutParams(new AbsListView.LayoutParams(-1,0));
			convertView.setVisibility(View.VISIBLE);

		}
		else if(!currentItem.getSetString()
				.equals( getSubscribedCourses().get(position - 1).getSetString() )){
			studyGroupTitle.setText(currentItem.getSetString());
			studyGroupTitle.setVisibility(View.VISIBLE);
			studyGroupLabel.setVisibility(View.VISIBLE);
			btnRemoveCourse.setVisibility(View.VISIBLE);
			convertView.setLayoutParams(new AbsListView.LayoutParams(-1,0));
			convertView.setVisibility(View.VISIBLE);
		}
		else{
			studyGroupTitle.setVisibility(View.GONE);
			studyGroupLabel.setVisibility(View.GONE);
			btnRemoveCourse.setVisibility(View.GONE);
		}

		final TextView tvTime = (TextView) convertView.findViewById(R.id.textCourseTime);
		final Date dateStartDate = new java.util.Date(currentItem.getEvent().getStartDate());
		//final String date = new SimpleDateFormat("dd.MM.yyyy").format(df);
		final String date = sdf.format(dateStartDate);
		final String dayOfWeek = new SimpleDateFormat("E", Locale.getDefault()).format(dateStartDate);
		tvTime.setText(dayOfWeek + ", " + date + "  "
				+ currentItem.getEvent().getStartTime() + " – " + currentItem.getEvent().getEndTime()); // $NON-NLS

		final TextView tvRoom = (TextView) convertView.findViewById(R.id.textviewRoom);
		tvRoom.setText(currentItem.getEvent().getRoom());

		return convertView;
	}


	private static final DateFormat sdf = SimpleDateFormat.getDateInstance();
}
