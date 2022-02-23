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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.vos.mytimetable.MyTimeTableCourseComponent;
import de.fhe.fhemobile.vos.timetable.TimeTableEventVo;

public class MyTimeTableCalendarAdapter extends BaseAdapter {

	private static final String TAG = "MyTTCalenderAdapter";

	private final Context context;
	private List<MyTimeTableCourseComponent> mItems;


	public MyTimeTableCalendarAdapter(final Context context, final List<MyTimeTableCourseComponent> mItems) {
		this.context = context;
		this.mItems = mItems;
	}


	/**
	 * How many items are in the data set represented by this Adapter.
	 *
	 * @return Count of items.
	 */
	@Override
	public int getCount() {
		return mItems != null ? mItems.size() : 0;
		//return 0;
	}

	/**
	 * Get the data item associated with the specified position in the data set.
	 *
	 * @param position Position of the item whose data we want within the adapter's data set.
	 * @return The data at the specified position.
	 */
	@Override
	public MyTimeTableCourseComponent getItem(int position) {
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
		//note: position is correct
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).
					inflate(R.layout.item_my_time_table_calendar, parent, false);
		}


		//set TextViews Week, Day, Title,... for the currentItem
		final MyTimeTableCourseComponent currentItem = mItems.get(position);
		final Date df = new Date(currentItem.getFirstEvent().getFullDateWithStartTime());

		final TextView courseWeekDay = (TextView) convertView.findViewById(R.id.textviewWeekDay);
		courseWeekDay.setText(currentItem.getFirstEvent().getDayOfWeek());
		if(sdf.format(df).compareTo(sdf.format(new Date())) == 0){
			courseWeekDay.setText(context.getString(R.string.today) + " ("+currentItem.getFirstEvent().getDayOfWeek() + ")");
		}

		final TextView courseDate = (TextView) convertView.findViewById(R.id.tvCourseDate);
		courseDate.setText(currentItem.getFirstEvent().getDate());

		final TextView courseTitle = (TextView) convertView.findViewById(R.id.textviewTitle);
		courseTitle.setText(currentItem.getFirstEvent().getGuiTitle());

		final TextView courseTime = (TextView) convertView.findViewById(R.id.textCourseTime);
		//String date = sdf.format(df);
		courseTime.setText(currentItem.getFirstEvent().getStartTime() + " – " + currentItem.getFirstEvent().getEndTime()); // $NON-NLS

		final TextView courseRoom = (TextView)convertView.findViewById(R.id.textviewRoom);
		courseRoom.setText(currentItem.getFirstEvent().getRoom());

		final TextView courseLecturer = (TextView)convertView.findViewById(R.id.textviewLecturer);
		courseLecturer.setText(currentItem.getFirstEvent().getLecturer());


		//Add a header displaying WeekDay and Date
		// if such a header has already been added with other courses at the same date,
		// then do not add header again (set it invisible for this item)
		final RelativeLayout headerLayout = convertView.findViewById(R.id.layout_mytimetable_calendar_header);
		if(position == 0 || !currentItem.getFirstEvent().getDate().equals(
				mItems.get(position - 1).getFirstEvent().getDate())){
			headerLayout.setVisibility(View.VISIBLE);
			courseWeekDay.setVisibility(View.VISIBLE);
			courseDate.setVisibility(View.VISIBLE);
		}
		else{
			headerLayout.setVisibility(View.GONE);
			courseWeekDay.setVisibility(View.GONE);
			courseDate.setVisibility(View.GONE);
		}


		return convertView;
	}


	/**
	 *    gehe durch die Liste, bis die Startzeit eines Events größer ist als die angegebene Zeit und nehme den vorherigen Event
	 *    und gebe den Index zurück.
	 * @return
	 */
	public int getPositionOfFirstCourseToday(){
		final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy H:mm");
		//don't use SimpleDateFormat.getDateTimeInstance() because it includes seconds

		int posToday = -1;
		Date posEndTime = null;


		for(int i = 0; i < mItems.size(); i++){

			final TimeTableEventVo event = mItems.get(i).getFirstEvent();
			final Date now = new Date();

			try {
				Date eventStartTime = sdf.parse(event.getDate() + " " + event.getStartTime());

				//course starts now or in the future
				if(eventStartTime.compareTo(now) >= 0) {

					//if the event today has not finished yet (endTime after timeNow)
					if(sdf.parse(event.getDate() + " " + event.getEndTime())
							.compareTo(now) >= 0) {

						//if the event is earlier on the same day than the one found before
						if(posEndTime == null
								|| (eventStartTime.compareTo(posEndTime) == 0
								&& eventStartTime.compareTo(posEndTime) < 0)){
							posEndTime = eventStartTime;
							posToday = i;
						}
					}

				}
			} catch (ParseException e) {
				Log.e(TAG, "error getting position of first event today",e );
			}
			catch (NullPointerException e) {
				Log.e(TAG, "wrong Date format", e);
			}
		}
		return posToday;
	}

	//private final static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
	private static final DateFormat sdf = SimpleDateFormat.getDateInstance();
}
