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
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.views.mytimetable.MyTimeTableOverviewView;
import de.fhe.fhemobile.vos.timetable.FlatDataStructure;

public class MyTimeTableDialogAdapter extends BaseAdapter {
	private static final String TAG = "MyTimeTableDialogAdapter";

	//private String lessonTitle="";
	//private String studygroupTitle="";

	private final Context mContext;
	private List<FlatDataStructure> chosenCourseList;

	public MyTimeTableDialogAdapter(Context context) {
		this.mContext = context;
	}

	public void setChosenCourseList(List<FlatDataStructure> chosenCourseList) {
		this.chosenCourseList = chosenCourseList;
	}

	/**
	 * How many items are in the data set represented by this Adapter.
	 *
	 * @return Count of items.
	 */
	@Override
	public int getCount() {
		return chosenCourseList != null ? chosenCourseList.size() : 0;
	}

	/**
	 * Get the data item associated with the specified position in the data set.
	 *
	 * @param position Position of the item whose data we want within the adapter's
	 *                 data set.
	 * @return The data at the specified position.
	 */
	@Override
	public Object getItem(final int position) {
		if(chosenCourseList != null) return chosenCourseList.get(position);
		else return null;
	}

	/**
	 * Get the row id associated with the specified position in the list.
	 *
	 * @param position The position of the item within the adapter's data set whose row id we want.
	 * @return The id of the item at the specified position.
	 */
	@Override
	public long getItemId(final int position) {
		return position;
	}

	/**
	 * Get a View that displays the data at the specified position in the data set. You can either
	 * create a View manually or inflate it from an XML layout file. When the View is inflated, the
	 * parent View (GridView, ListView...) will apply default layout parameters unless you use
	 * {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)}
	 * to specify a root view and to prevent attachment to the root.
	 *
	 * @param position    The position of the item within the adapter's data set of the item whose view
	 *                    we want.
	 * @param convertView The old view to reuse, if possible. Note: You should check that this view
	 *                    is non-null and of an appropriate type before using. If it is not possible to convert
	 *                    this view to display the correct data, this method can create a new view.
	 *                    Heterogeneous lists can specify their number of view types, so that this View is
	 *                    always of the right type (see {@link #getViewTypeCount()} and
	 *                    {@link #getItemViewType(int)}).
	 * @param parent      The parent that this view will eventually be attached to
	 * @return A View corresponding to the data at the specified position.
	 */
	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {

		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).
					inflate(R.layout.item_my_time_table_dialog, parent, false);
		}

		final FlatDataStructure currentItem = chosenCourseList.get(position);

		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				//get all courses with certain title
				final List<FlatDataStructure> courseListFilteredByTitle =
						FlatDataStructure.getCoursesByEventTitle(
								chosenCourseList,
								FlatDataStructure.cutEventTitle(currentItem.getEvent().getTitle()));
				//get all courses with certain study group
				final List <FlatDataStructure> courseListFilteredByStudyGroup =
						FlatDataStructure.getCoursesByStudyGroupTitle(
								courseListFilteredByTitle, currentItem.getSetString());

				for (final FlatDataStructure event : courseListFilteredByStudyGroup){
					event.setVisible(!event.isVisible());
				}
				((ListView) parent).invalidateViews();

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

		final TextView courseTitle = (TextView) convertView.findViewById(R.id.textCourseTitle);
		final RelativeLayout headerLayout = convertView.findViewById(R.id.headerBackground);

		if(position == 0){
			courseTitle.setText(FlatDataStructure.cutEventTitle(currentItem.getEvent().getTitle()));
			courseTitle.setVisibility(View.VISIBLE);
			convertView.setLayoutParams(new AbsListView.LayoutParams(-1,0));
			convertView.setVisibility(View.VISIBLE);
			headerLayout.setVisibility(View.VISIBLE);

		}

		else if(!chosenCourseList.get(position).isEqual(chosenCourseList.get(position-1))){

			courseTitle.setText(FlatDataStructure.cutEventTitle(currentItem.getEvent().getTitle()));
			courseTitle.setVisibility(View.VISIBLE);
			convertView.setLayoutParams(new AbsListView.LayoutParams(-1,0));
			convertView.setVisibility(View.VISIBLE);
			headerLayout.setVisibility(View.VISIBLE);
		}
		else{
			courseTitle.setVisibility(View.GONE);
			headerLayout.setVisibility(View.GONE);
		}


		final TextView studyGroupLabel = (TextView) convertView.findViewById(R.id.textStudyGroupLabel);
		final TextView studyGroupTitle = (TextView) convertView.findViewById(R.id.textStudyGroupTitle);
		final ToggleButton btnAddCourse = (ToggleButton) convertView.findViewById(R.id.btnAddCourse);

		//set current state of button
		boolean btnEnabled = currentItem.isAdded() ? true : false;
		btnAddCourse.setActivated(btnEnabled);

		btnAddCourse.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {

				btnAddCourse.setActivated(!btnAddCourse.isActivated());

				if(btnAddCourse.isActivated()){
					currentItem.setSubscribed(true);
					final List<FlatDataStructure> eventFilteredList =
							FlatDataStructure.getCoursesByEventTitle(
									chosenCourseList,
									FlatDataStructure.cutEventTitle(currentItem.getEvent().getTitle()));
					final List<FlatDataStructure> studyGroupFilteredList =
							FlatDataStructure.getCoursesByStudyGroupTitle(
									eventFilteredList, currentItem.getSetString());

					for(final FlatDataStructure event : studyGroupFilteredList){
						MyTimeTableOverviewView.addCourse(event);
					}
				}else{
					MyTimeTableOverviewView.removeCourse(currentItem);
				}

				notifyDataSetChanged();

			}

		});


		String combinedStudyGroups = currentItem.getSetString();

		studyGroupTitle.setText(combinedStudyGroups);

		if(position == 0){
			studyGroupTitle.setVisibility(View.VISIBLE);
			studyGroupLabel.setVisibility(View.VISIBLE);
			btnAddCourse.setVisibility(View.VISIBLE);
			convertView.setLayoutParams(new AbsListView.LayoutParams(-1,0));
			convertView.setVisibility(View.VISIBLE);
		}

		else if(!currentItem.isEqual(chosenCourseList.get(position-1)))
		{
			studyGroupTitle.setVisibility(View.VISIBLE);
			studyGroupLabel.setVisibility(View.VISIBLE);
			btnAddCourse.setVisibility(View.VISIBLE);
			convertView.setLayoutParams(new AbsListView.LayoutParams(-1,0));
			convertView.setVisibility(View.VISIBLE);

		}

		else if(!currentItem.getSetString().equals(
				chosenCourseList.get(position-1).getSetString()))
		{
			studyGroupTitle.setVisibility(View.VISIBLE);
			studyGroupLabel.setVisibility(View.VISIBLE);
			btnAddCourse.setVisibility(View.VISIBLE);
			convertView.setLayoutParams(new AbsListView.LayoutParams(-1,0));
			convertView.setVisibility(View.VISIBLE);
		}

		else{
			studyGroupTitle.setVisibility(View.GONE);
			studyGroupLabel.setVisibility(View.GONE);
			btnAddCourse.setVisibility(View.GONE);
		}

		final TextView timeTextView = (TextView) convertView.findViewById(R.id.textCourseTime);
		final Date dateStartDate = new java.util.Date(currentItem.getEvent().getStartDate());
		//final String date = new SimpleDateFormat("dd.MM.yyyy").format(df);
		final String date = sdf.format(dateStartDate);
		final String dayOfWeek = new SimpleDateFormat("E", Locale.getDefault()).format(dateStartDate);
		timeTextView.setText(dayOfWeek + ", " + date + "  "
				+ currentItem.getEvent().getStartTime() + " – " + currentItem.getEvent().getEndTime()); // $NON-NLS

		final TextView roomTextView = (TextView)convertView.findViewById(R.id.textviewRoom);
		roomTextView.setText(currentItem.getEvent().getRoom());

		return convertView;
	}


	private static final DateFormat sdf = SimpleDateFormat.getDateInstance();
}
