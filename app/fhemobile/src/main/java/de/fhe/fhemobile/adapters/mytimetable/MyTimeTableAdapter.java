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
import de.fhe.fhemobile.vos.mytimetable.MyTimeTableCourseComponent;
import de.fhe.fhemobile.vos.timetable.TimeTableEventVo;

/**
 * Created by Nadja - 02/2022
 */
public abstract class MyTimeTableAdapter extends BaseAdapter {

    private static final String TAG = "MyTimeTableAdapter";

    protected final Context mContext;
    protected List<MyTimeTableCourseComponent> mItems;
    private boolean roomVisible = false;


    public MyTimeTableAdapter(final Context context) {
        this.mContext = context;
    }

    public void setItems(final List<MyTimeTableCourseComponent> mItems) {
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

        final MyTimeTableCourseComponent currentItem = mItems.get(position);


        //click on row of the course (at header, study groups, ...) expands the row of next date and room
        // to a list of all upcoming dates with its rooms
        convertView.setOnClickListener(new CourseOnClickListener(convertView, currentItem, roomVisible));


        final RelativeLayout headerLayout = convertView.findViewById(R.id.layout_mytimetable_course_header);
        final TextView courseTitle = (TextView) convertView.findViewById(R.id.tv_mytimetable_courseTitle);

        //Add a header displaying the course title
        // if such a header has already been added because of processing another component of the course
        // then do not add header again (set its visibility to GONE)
        if(position == 0 || !mItems.get(position).isSameCourse(mItems.get(position - 1))){
            courseTitle.setText(currentItem.getFirstEvent().getGuiTitle());

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
        setAndAddEventDataTextViews(currentItem.getFirstEvent(), layoutAllEvents);

        //otherwise it will sometimes be invisible
        convertView.setVisibility(View.VISIBLE);

        convertView.setLayoutParams(new AbsListView.LayoutParams(-1,0));
        return convertView;
    }

    /**
     * Create OnClickListener for the button to add a course by overriding onClick()
     * @param currentItem {@link MyTimeTableCourseComponent} the button belongs to
     * @param btnAddCourse the button to add a course that is clicked
     * @return an onClickListener for the button to add a course
     */
    protected abstract View.OnClickListener getAddCourseBtnOnClickListener(
            MyTimeTableCourseComponent currentItem, Button btnAddCourse);



    /**
     * Listener for click on course.
     * On click, the view changes between displaying only the first course date
     * and displaying all course dates
     */
    private class CourseOnClickListener implements View.OnClickListener{

        final private View convertView;
        final private MyTimeTableCourseComponent currentCourse;
        final boolean roomVisible;

        CourseOnClickListener(final View view,final MyTimeTableCourseComponent currentItem, boolean showRoom){
            convertView = view;
            currentCourse = currentItem;
            roomVisible = showRoom;
        }

        @Override
        public void onClick(View view) {
            //remove all previously added textviews of course events to avoid duplicates
            final LinearLayout layoutAllEvents = convertView.findViewById(R.id.layout_my_time_table_course_events);
            final int layoutAllEventsSize = layoutAllEvents.getChildCount();
            layoutAllEvents.removeAllViews();

            List<TimeTableEventVo> eventList = new ArrayList<>();
            //if list is expanded then reduce
            if(layoutAllEventsSize > 1) eventList.add(currentCourse.getFirstEvent());
            else eventList = currentCourse.getEvents();

            for (final TimeTableEventVo event : eventList){
                setAndAddEventDataTextViews(event, layoutAllEvents);
            }
            convertView.invalidate();
            convertView.setVisibility(View.VISIBLE);
        }
    }

    private void setAndAddEventDataTextViews(TimeTableEventVo event, LinearLayout layoutAllEvents){
        final TextView dateTextview = new TextView(mContext);
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,5,5,10);
        dateTextview.setLayoutParams(params);

        final Date dateStartDate = new java.util.Date(event.getFullDateWithStartTime());
        //final String date = new SimpleDateFormat("dd.MM.yyyy").format(df);
        final String date = sdf.format(dateStartDate);
        final String dayOfWeek = new SimpleDateFormat("E", Locale.getDefault()).format(dateStartDate);
        String courseDateText = dayOfWeek + ", " + date + "  "
                + event.getStartTime() + " – " + event.getEndTime();
        if(roomVisible && event.getRoom().length() > 0) courseDateText += "\n"+ event.getRoom();
        dateTextview.setText(courseDateText); // $NON-NLS
        layoutAllEvents.addView(dateTextview);
    }


    private static final DateFormat sdf = SimpleDateFormat.getDateInstance();
}

