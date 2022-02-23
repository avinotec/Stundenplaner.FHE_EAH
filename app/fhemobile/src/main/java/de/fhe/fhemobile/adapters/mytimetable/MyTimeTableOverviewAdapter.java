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

import static de.fhe.fhemobile.Main.getSubscribedCourseComponents;

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
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.utils.mytimetable.MyTimeTableUtils;
import de.fhe.fhemobile.vos.mytimetable.MyTimeTableCourseComponent;
import de.fhe.fhemobile.vos.timetable.TimeTableEventVo;

public class MyTimeTableOverviewAdapter extends BaseAdapter {

	private static final String TAG = "MyTimeTableOverviewAdapter";

	private final Context context;
	private List<MyTimeTableCourseComponent> mItems;

	public MyTimeTableOverviewAdapter(final Context context, final List<MyTimeTableCourseComponent> _items) {
		this.context = context;
		this.mItems = _items;
	}

	/**
	 * How many items are in the data set represented by this Adapter.
	 *
	 * @return Count of items.
	 */
	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(final int position) {
		return mItems.get(position);
	}

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
			convertView = LayoutInflater.from(context).
					inflate(R.layout.item_my_time_table_overview, parent, false);
		}

		final MyTimeTableCourseComponent currentItem = mItems.get(position);


		//todo: find out in which use case this is needed
		convertView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				for (final TimeTableEventVo event : currentItem.getEvents()){
					//todo: make all dates of the course visible
					//event.setVisible(!event.isVisible());
				}

				((ListView) parent).invalidateViews();
			}
		});


		final ImageButton btnRemoveCourse = convertView.findViewById(R.id.btnRemoveCourse);
		btnRemoveCourse.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final List<MyTimeTableCourseComponent> eventFilteredList
						= MyTimeTableUtils.getCoursesByEventTitle(
						getSubscribedCourseComponents(),
						currentItem.getTitle());

				final List<MyTimeTableCourseComponent> studyGroupFilteredList
						= MyTimeTableUtils.getCoursesByStudyGroupTitle(
						eventFilteredList, currentItem.getStudyGroupListString());

				for(MyTimeTableCourseComponent event : studyGroupFilteredList){
					MainActivity.removeFromSubscribedCourseComponentsAndUpdateAdapters(event);
				}

				((ListView) parent).invalidateViews();
			}
		});

		//set text of courseTitle
		final TextView courseTitle = (TextView) convertView.findViewById(
				R.id.textview_mytimetable_overview_courseTitle);
		courseTitle.setText(currentItem.getFirstEvent().getGuiTitle());


		//todo: find out in which use case currentItem is not visible
		if(currentItem.isVisible()){
			convertView.setLayoutParams(new AbsListView.LayoutParams(-1, 0));
			convertView.setVisibility(View.VISIBLE);
		}
		else{
			convertView.setLayoutParams(new AbsListView.LayoutParams(-1, 1));
			convertView.setVisibility(View.GONE);
		}

		//Add a header displaying the course title
		// if such a header has already been added because of processing another set of the course (I guess),
		// then do not add header again (set it visibility to GONE)
		final RelativeLayout header = convertView.findViewById(
				R.id.layout_mytimetable_overview_header);
		if(position == 0
				|| !getSubscribedCourseComponents().get(position).isSameCourse(mItems.get(position - 1))){
			header.setVisibility(View.VISIBLE);
			convertView.setLayoutParams(new AbsListView.LayoutParams(-1, 0));
			convertView.setVisibility(View.VISIBLE);

		} else{
			header.setVisibility(View.GONE);
		}

		//Add each event (lecture, training, practical, ...) belonging to the course title in the header
		// Only add if cutEventTitle and setString do not equal previous item
		final TextView studyGroupLabel = (TextView)convertView.findViewById(
				R.id.textview_mytimetable_overview_sets_label);
		final TextView studyGroupTitle = (TextView)convertView.findViewById(R.id.textview_mytimetable_overview_studygroups);
		if(position == 0
				|| !currentItem.isSameCourse(mItems.get(position - 1))
				|| !currentItem.getStudyGroupListString().equals( mItems.get(position - 1).getStudyGroupListString() )){
			studyGroupTitle.setText(currentItem.getStudyGroupListString());
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


		//set text for course time, weekday and room

		final Date dateStartDate = new java.util.Date(currentItem.getFirstEvent().getStartDate());
		//final String date = new SimpleDateFormat("dd.MM.yyyy").format(df);
		final String date = sdf.format(dateStartDate);
		final String dayOfWeek = new SimpleDateFormat("E", Locale.getDefault()).format(dateStartDate);

		final TextView textEventTime = (TextView) convertView.findViewById(R.id.textCourseTime);
		textEventTime.setText(dayOfWeek + ", " + date + "  "
				+ currentItem.getFirstEvent().getStartTime() + " – " + currentItem.getFirstEvent().getEndTime()); // $NON-NLS

		final TextView textRoom = (TextView) convertView.findViewById(R.id.textviewRoom);
		textRoom.setText(currentItem.getFirstEvent().getRoom());



		return convertView;
	}




	private static final DateFormat sdf = SimpleDateFormat.getDateInstance();
}
