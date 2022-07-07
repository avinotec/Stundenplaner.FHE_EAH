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
package de.fhe.fhemobile.adapters.myschedule;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.comparator.TimeIgnoringDateComparator;
import de.fhe.fhemobile.fragments.myschedule.MyScheduleCalendarFragment;
import de.fhe.fhemobile.utils.myschedule.TimetableChangeType;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventVo;

/**
 * Edited by Nadja - 02/2022
 */
public class MyScheduleCalendarAdapter extends BaseAdapter {

	private static final String TAG = MyScheduleCalendarFragment.class.getSimpleName();


	public MyScheduleCalendarAdapter() {
	}

	public void setItems(List<MyScheduleEventVo> items){
		mItems = items;
	}


	/**
	 * How many items are in the data set represented by this Adapter.
	 * @return Count of items.
	 */
	@Override
	public int getCount() {
		return mItems != null ? mItems.size() : 0;
	}

	/**
	 * Get the data item associated with the specified position in the data set.
	 *
	 * @param position Position of the item whose data we want within the adapter's data set.
	 * @return The data at the specified position.
	 */
	@Override
	public MyScheduleEventVo getItem(int position) {
		return mItems.get(position);
	}

	/**
	 * Get the row id associated with the specified position in the list.
	 *
	 * @param position The position of the item within the adapter's data set whose row id we want.
	 * @return
	 */
	@Override
	public long getItemId(int position) {
		//note: returning position is intended because MyScheduleEventVo has no id
		return position;
	}


	/**
	 * Get a View that displays the data at the specified position in the data set. You can either
	 * create a View manually or inflate it from an XML layout file. When the View is inflated, the
	 * parent View (GridView, ListView...) will apply default layout parameters unless you use
	 * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
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
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = LayoutInflater.from(Main.getAppContext()).
					inflate(R.layout.item_myschedule_calendar, parent, false);
		}

		final MyScheduleEventVo currentItem = mItems.get(position);


		//Add a header displaying WeekDay and Date
		// if such a header has already been added with another event at that date,
		// then do not add header again (set invisible)
		final TextView weekdayHeader = (TextView) convertView.findViewById(R.id.tv_item_header_default_day);
		if(position == 0 || new TimeIgnoringDateComparator()
				.compare(currentItem.getStartDateTime(), mItems.get(position - 1).getStartDateTime()) != 0){

			String weekDay = currentItem.getWeekDayName();
			final Date df = currentItem.getStartDateTime();
			//if necessary, add "today" in brackets to mark today's day
			if(new TimeIgnoringDateComparator().compare(df, new Date()) == 0){
				weekDay = "(" + Main.getAppContext().getString(R.string.today) + ") "+currentItem.getWeekDayName(); //$NON-NLS
			}
			weekDay += ", " + new SimpleDateFormat("dd.MM.yy",
					Locale.getDefault()).format(currentItem.getStartDateTime());
			weekdayHeader.setText(weekDay);
			weekdayHeader.setVisibility(View.VISIBLE);

		} else{
			weekdayHeader.setVisibility(View.GONE);
		}


		//set texts: title, time, room, lecturer
		ArrayList<TextView> eventViews = new ArrayList<>();
		final TextView eventTitle = (TextView) convertView.findViewById(R.id.tv_myschedule_calendar_eventtitle);
		eventTitle.setText(currentItem.getGuiTitle());
		eventViews.add(eventTitle);

		final TextView eventTime = (TextView) convertView.findViewById(R.id.tv_myschedule_calendar_eventtime);
		eventTime.setText(currentItem.getStartTimeString() + " – " + currentItem.getEndTimeString()); // $NON-NLS
		eventViews.add(eventTime);

		final TextView eventLocation = (TextView) convertView.findViewById(R.id.tv_myschedule_calendar_room);
		eventLocation.setText(currentItem.getLocationListAsString());
		eventViews.add(eventLocation);

		final TextView eventLecturer = (TextView) convertView.findViewById(R.id.tv_myschedule_calendar_lecturer);
		eventLecturer.setText(currentItem.getLecturerListAsString());
		eventViews.add(eventLecturer);

		//highlight changes
		for(TimetableChangeType change : currentItem.getTypesOfChanges()){
			switch (change){
				case ADDITION:
					//set text bold
					for (TextView v : eventViews) {
						if(v.getTypeface().equals(Typeface.ITALIC)){
							v.setTypeface(null, Typeface.BOLD_ITALIC);
						} else {
							v.setTypeface(null, Typeface.BOLD);
						}
					}
					break;

				case DELETION:
					//set text strikethrough
					for (TextView v : eventViews) {
						v.setPaintFlags(v.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
					}
					break;

				case EDIT_TIME:
					eventTime.setTypeface(null, Typeface.BOLD);
					break;
				case EDIT_LOCATION:
					eventLocation.setTypeface(null, Typeface.BOLD);
					break;
				case EDIT_LECTURER:
					eventLecturer.setTypeface(null, Typeface.BOLD_ITALIC);
					break;
			}
		}

		return convertView;
	}


	/**
	 * Iterate over item list of the adapter till start time of an event is after now,
	 * return the index of the previous event.
	 * @return The position of the first event with today's date
	 */
	public int getPositionOfFirstEventToday(){
		//iteration variables
		int posToday = -1;
		Long k = null;

		for(MyScheduleEventVo eventVo : mItems){

			final Date now = new Date();

			try {
				//course starts now or in the future
				if(eventVo.getStartDateTime().compareTo(now) >= 0) {

					//if event has not finished yet
					if(eventVo.getEndDateTime().compareTo(now) >= 0) {

						//update, if event startTime is earlier the event found before
						if(k == null || eventVo.getStartDateTimeInSec() <= k){
							k = eventVo.getStartDateTimeInSec();
							posToday = mItems.indexOf(eventVo);
						}
					}

				}
			} catch (NullPointerException e) {
				Log.e(TAG, "Invalid Date format", e);
			}
		}
		return posToday;
	}


	private List<MyScheduleEventVo> mItems;
}
