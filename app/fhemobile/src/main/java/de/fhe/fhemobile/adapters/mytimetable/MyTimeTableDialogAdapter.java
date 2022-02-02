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
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.utils.MyTimeTableUtils;
import de.fhe.fhemobile.vos.mytimetable.MyTimeTableCourse;

public class MyTimeTableDialogAdapter extends BaseAdapter {

	private static final String TAG = "MyTimeTableDialogAdapter";

	private final Context mContext;
	private List<MyTimeTableCourse> mItems;



	public MyTimeTableDialogAdapter(final Context context) {
		this.mContext = context;
	}

	public void setItems(final List<MyTimeTableCourse> mItems) {
		this.mItems = mItems;
		this.notifyDataSetChanged();
	}

	/**
	 * How many items are in the data set represented by this Adapter.
	 *
	 * @return Count of items.
	 */
	@Override
	public int getCount() {
		return mItems != null ? mItems.size() : 0;
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
		if(mItems != null) return mItems.get(position);
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

		final MyTimeTableCourse currentItem = mItems.get(position);


		//click on row of the course (at header, sets, ...) expands the row of next date and room
		// to a list of all upcoming dates with its rooms
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {

				//get all courses with certain title
				final List<MyTimeTableCourse> courseListFilteredByTitle =
						MyTimeTableUtils.getCoursesByEventTitle(
								mItems,
								MyTimeTableUtils.cutEventTitle(currentItem.getEvent().getTitle()));
				//get all courses with certain study group
				final List <MyTimeTableCourse> courseListFilteredByStudyGroup =
						MyTimeTableUtils.getCoursesByStudyGroupTitle(
								courseListFilteredByTitle, currentItem.getSetString());

				for (final MyTimeTableCourse event : courseListFilteredByStudyGroup){
					event.setVisible(!event.isVisible());
				}
				((ListView) parent).invalidateViews();

			}
		});
		//currentItem can be invisible because initially only the next date
		// (and not all upcoming dates) are shown for a course
		if(currentItem.isVisible()){
			convertView.setLayoutParams(new AbsListView.LayoutParams(-1,0));
			convertView.setVisibility(View.VISIBLE);
		}
		else{
			convertView.setLayoutParams(new AbsListView.LayoutParams(-1,1));
			convertView.setVisibility(View.GONE);
		}


		final TextView courseTitle = (TextView) convertView.findViewById(R.id.textview_mytimetable_dialog_courseTitle);
		final RelativeLayout headerLayout = convertView.findViewById(R.id.layout_mytimetable_dialog_header);

		//Add a header displaying the course title
		// if such a header has already been added because of processing another set of the course (I guess - Nadja),
		// then do not add header again (set it visibility to GONE)
		if(position == 0
				|| !mItems.get(position).isEqual(mItems.get(position-1))){
			courseTitle.setText(currentItem.getEvent().getShortTitle());
			courseTitle.setVisibility(View.VISIBLE);
			convertView.setLayoutParams(new AbsListView.LayoutParams(-1,0));
			convertView.setVisibility(View.VISIBLE);
			headerLayout.setVisibility(View.VISIBLE);
		}
		else{
			headerLayout.setVisibility(View.GONE);
		}


		final ToggleButton btnAddCourse = (ToggleButton) convertView.findViewById(R.id.btnAddCourse);
		//set current state of button
		final boolean btnEnabled = currentItem.isSubscribed();
		btnAddCourse.setActivated(btnEnabled);

		btnAddCourse.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {

				btnAddCourse.setActivated(!btnAddCourse.isActivated());

				if(btnAddCourse.isActivated()){
					currentItem.setSubscribed(true);
					final List<MyTimeTableCourse> eventFilteredList =
							MyTimeTableUtils.getCoursesByEventTitle(
									mItems,
									MyTimeTableUtils.cutEventTitle(currentItem.getEvent().getTitle()));
					final List<MyTimeTableCourse> studyGroupFilteredList =
							MyTimeTableUtils.getCoursesByStudyGroupTitle(
									eventFilteredList, currentItem.getSetString());

					for(final MyTimeTableCourse event : studyGroupFilteredList){
						MainActivity.addToSubscribedCourses(event);
					}
				}else{
					MainActivity.removeFromSubscribedCourses(currentItem);
				}

				notifyDataSetChanged();
			}

		});



		final TextView studyGroupTitle = (TextView) convertView.findViewById(R.id.textview_mytimetable_dialog_studygroups);
		final String allStudyGroupsOfCourse = currentItem.getSetString();
		studyGroupTitle.setText(allStudyGroupsOfCourse);

		final TextView labelSets = (TextView) convertView.findViewById(R.id.textview_mytimetable_dialog_label_sets);

		//Add each event (lecture, training, practical, ...) belonging to the course title in the header
		if(position == 0
				|| !currentItem.isEqual(mItems.get(position-1))
				|| !currentItem.getSetString().equals(mItems.get(position-1).getSetString())){
			studyGroupTitle.setVisibility(View.VISIBLE);
			labelSets.setVisibility(View.VISIBLE);
			btnAddCourse.setVisibility(View.VISIBLE);
			convertView.setLayoutParams(new AbsListView.LayoutParams(-1,0));
			convertView.setVisibility(View.VISIBLE);
		}
		else{
			studyGroupTitle.setVisibility(View.GONE);
			labelSets.setVisibility(View.GONE);
			btnAddCourse.setVisibility(View.GONE);
		}


		//set text for course time, weekday and room

		final Date dateStartDate = new java.util.Date(currentItem.getEvent().getStartDate());
		//final String date = new SimpleDateFormat("dd.MM.yyyy").format(df);
		final String date = sdf.format(dateStartDate);
		final String dayOfWeek = new SimpleDateFormat("E", Locale.getDefault()).format(dateStartDate);

		final TextView textEventTime = (TextView) convertView.findViewById(R.id.textCourseTime);
		textEventTime.setText(dayOfWeek + ", " + date + "  "
				+ currentItem.getEvent().getStartTime() + " – " + currentItem.getEvent().getEndTime()); // $NON-NLS

		final TextView textRoom = (TextView)convertView.findViewById(R.id.textviewRoom);
		textRoom.setText(currentItem.getEvent().getRoom());



		return convertView;
	}


	private static final DateFormat sdf = SimpleDateFormat.getDateInstance();
}
