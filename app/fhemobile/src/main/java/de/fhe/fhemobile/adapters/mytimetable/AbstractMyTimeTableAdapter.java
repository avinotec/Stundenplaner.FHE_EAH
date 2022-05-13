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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.vos.mytimetable.MyTimeTableEventSeriesVo;
import de.fhe.fhemobile.vos.mytimetable.MyTimeTableEventTimeVo;
import de.fhe.fhemobile.vos.timetable.TimeTableLocationVo;

/**
 * Abstract class for {@link MyTimeTableDialogAdapter} and {@link MyTimeTableOverviewAdapter}
 * Created by Nadja - 02/2022
 */
public abstract class AbstractMyTimeTableAdapter extends BaseAdapter {

    private static final String TAG = AbstractMyTimeTableAdapter.class.getSimpleName();

    protected final Context mContext;
    protected List<MyTimeTableEventSeriesVo> mItems;
    private boolean roomVisible = false;


    public AbstractMyTimeTableAdapter(final Context context) {
        this.mContext = context;
    }

    public void setItems(final List<MyTimeTableEventSeriesVo> mItems) {
        this.mItems = mItems;
        this.notifyDataSetChanged();
    }

    public void setRoomVisible(boolean visible){
        roomVisible = visible;
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
                    inflate(R.layout.item_my_time_table, parent, false);
        }

        final MyTimeTableEventSeriesVo currentItem = mItems.get(position);


        //click on row of the course (at header, study groups, ...) expands the row of next date and room
        // to a list of all upcoming dates with its rooms
        convertView.setOnClickListener(new CourseOnClickListener(convertView, currentItem, roomVisible));


        final RelativeLayout headerLayout = convertView.findViewById(R.id.layout_mytimetable_course_header);
        final TextView courseTitle = (TextView) convertView.findViewById(R.id.tv_mytimetable_coursetitle);

        //Add a header displaying the event series title
        // if such a header has already been added because of processing another event series with the same base title
        // then do not add header again (set its visibility to GONE)
        if(position == 0 || !mItems.get(position).canBeGroupedForDisplay(mItems.get(position - 1))){
            courseTitle.setText(currentItem.getTitle());

            courseTitle.setVisibility(View.VISIBLE);
            headerLayout.setVisibility(View.VISIBLE);
        }
        else{
            headerLayout.setVisibility(View.GONE);
        }


        final Button btnAddCourse = (Button) convertView.findViewById(R.id.btn_mytimetable_add_or_remove_course);
        btnAddCourse.setActivated(currentItem.isSubscribed());
        btnAddCourse.setOnClickListener(getAddCourseBtnOnClickListener(currentItem, btnAddCourse));

        //set study group list text view
        final TextView textStudyGroupList = (TextView) convertView.findViewById(R.id.tv_mytimetable_studygroups);
        textStudyGroupList.setText(currentItem.getStudyGroupListString());

        //if view is not populated for the first time (convertView != null at the beginning of the method)
        // then remove previously added event data views
        final LinearLayout layoutAllEvents = (LinearLayout) convertView.findViewById(R.id.layout_my_time_table_course_events);
        if(layoutAllEvents.getChildCount() >= 0){
           layoutAllEvents.removeAllViews();
        }
        setAndAddEventDataTextViews(currentItem.getFirstEvent(), currentItem.getLocationList(), layoutAllEvents);

        //otherwise it will sometimes be invisible
        convertView.setVisibility(View.VISIBLE);

        convertView.setLayoutParams(new AbsListView.LayoutParams(-1,0));
        return convertView;
    }

    /**
     * Create OnClickListener for the button to add a course by overriding onClick()
     * @param currentItem {@link MyTimeTableEventSeriesVo} the button belongs to
     * @param btnAddCourse the button to add a course that is clicked
     * @return an onClickListener for the button to add a course
     */
    protected abstract View.OnClickListener getAddCourseBtnOnClickListener(
            MyTimeTableEventSeriesVo currentItem, Button btnAddCourse);



    /**
     * Listener for click on event series view.
     * On click, the view changes between displaying only the first event of the series
     * and displaying all dates
     */
    private class CourseOnClickListener implements View.OnClickListener{

        final private View convertView;
        final boolean roomVisible;

        final private MyTimeTableEventSeriesVo currentEventSeries;

        CourseOnClickListener(final View view, final MyTimeTableEventSeriesVo currentItem, boolean showRoom){
            convertView = view;
            currentEventSeries = currentItem;
            roomVisible = showRoom;
        }

        @Override
        public void onClick(View view) {
            //remove all previously added textviews of course events to avoid duplicates
            final LinearLayout layoutAllEvents = convertView.findViewById(R.id.layout_my_time_table_course_events);
            final int layoutAllEventsSize = layoutAllEvents.getChildCount();
            layoutAllEvents.removeAllViews();

            List<MyTimeTableEventTimeVo> eventList = new ArrayList<>();
            //if list is expanded then reduce
            if(layoutAllEventsSize > 1) eventList.add(currentEventSeries.getFirstEvent());
            else eventList = currentEventSeries.getEvents();

            for (final MyTimeTableEventTimeVo event : eventList){
                setAndAddEventDataTextViews(event, currentEventSeries.getLocationList(), layoutAllEvents);
            }
            convertView.invalidate();
            convertView.setVisibility(View.VISIBLE);
        }
    }

    private void setAndAddEventDataTextViews(MyTimeTableEventTimeVo _EventTime,
                                             List<TimeTableLocationVo> _Rooms,
                                             LinearLayout layoutAllEvents){
        final TextView dateTextview = new TextView(mContext);
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,5,5,10);
        dateTextview.setLayoutParams(params);

        final Date startDateTime = _EventTime.getStartDateTime();
        final Date endDateTime =  _EventTime.getEndDateTime();
        final String date = sdf.format(startDateTime);
        final String dayOfWeek = new SimpleDateFormat("E", Locale.getDefault()).format(startDateTime);
        final String startTime = new SimpleDateFormat("h:mm", Locale.getDefault()).format(startDateTime);
        final String endTime = new SimpleDateFormat("h:mm", Locale.getDefault()).format(endDateTime);
        String courseDateText = dayOfWeek + ", " + date + "  " + startTime + " – " + endTime;


        StringBuilder stringBuilder = new StringBuilder();
        for(TimeTableLocationVo room : _Rooms){

            stringBuilder.append(room.getName());
            // jetzt bleibt (immer noch) ein Komma am Ende übrig
            stringBuilder.append(", ");
        }
        if(roomVisible && stringBuilder.length()>0) {
            courseDateText += "\n"+ stringBuilder;
        }
        dateTextview.setText(courseDateText);
        layoutAllEvents.addView(dateTextview);
    }


    private static final DateFormat sdf = SimpleDateFormat.getDateInstance();
}

