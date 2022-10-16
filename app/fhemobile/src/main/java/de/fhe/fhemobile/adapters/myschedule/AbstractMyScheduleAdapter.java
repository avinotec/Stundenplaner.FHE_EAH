/*
 *  Copyright (c) 2014-2022 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
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

import static de.fhe.fhemobile.utils.myschedule.MyScheduleUtils.getEventSeriesBaseTitle;
import static de.fhe.fhemobile.utils.timetable.TimeTableUtils.cutStudyProgramPrefix;

import android.content.Context;
import android.graphics.Paint;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.utils.myschedule.TimetableChangeType;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventSeriesVo;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventSetVo;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventVo;

/**
 * Abstract class for {@link MyScheduleDialogAdapter} and {@link MyScheduleSettingsAdapter}
 * Created by Nadja - 02/2022
 */
public abstract class AbstractMyScheduleAdapter extends BaseAdapter {

    private static final String TAG = AbstractMyScheduleAdapter.class.getSimpleName();

    protected final Context mContext;
    protected List<MyScheduleEventSeriesVo> mItems;
    boolean roomVisible = false;


    public AbstractMyScheduleAdapter(final Context context) {
        this.mContext = context;
    }

    public void setItems(final List<MyScheduleEventSeriesVo> mItems) {
        this.mItems = mItems;
        this.notifyDataSetChanged();
    }

    public void setRoomVisible(final boolean visible){
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
                    inflate(R.layout.item_myschedule_settings, parent, false);
        }

        final MyScheduleEventSeriesVo currentItem = mItems.get(position);


        //click on row of the course (at header, study groups, ...) expands the row of next date and room
        // to a list of all upcoming dates with its rooms
        convertView.setOnClickListener(new EventSeriesOnClickListener(convertView, currentItem, roomVisible));


        final RelativeLayout headerLayout = convertView.findViewById(R.id.layout_myschedule_eventseries_header);
        final TextView eventSeriesBaseTitle = (TextView) convertView.findViewById(R.id.tv_myschedule_eventseries_title);

        //Add a header displaying the event series title
        // if such a header has already been added because of processing another event series with the same base title
        // then do not add header again (set its visibility to GONE)
        if(position == 0 || !mItems.get(position).canBeGroupedForDisplay(mItems.get(position - 1))){
            eventSeriesBaseTitle.setText(cutStudyProgramPrefix(getEventSeriesBaseTitle(currentItem.getTitle())));

            eventSeriesBaseTitle.setVisibility(View.VISIBLE);
            headerLayout.setVisibility(View.VISIBLE);

        } else {
            headerLayout.setVisibility(View.GONE);
        }


        final Button btnAddCourse = convertView.findViewById(R.id.btn_myschedule_add_or_remove_eventseries);
        btnAddCourse.setActivated(currentItem.isSubscribed());
        btnAddCourse.setOnClickListener(getAddEventSeriesBtnOnClickListener(currentItem, btnAddCourse));

        //set study group list text view
        final TextView textStudyGroupList = convertView.findViewById(R.id.tv_myschedule_studygroups);
        textStudyGroupList.setText(currentItem.getStudyGroupListString());

        //if view is not populated for the first time (convertView != null at the beginning of the method)
        // then remove previously added event data views
        final LinearLayout layoutAllEvents = convertView.findViewById(R.id.layout_myschedule_eventseries_events);
        if(layoutAllEvents.getChildCount() >= 0){
           layoutAllEvents.removeAllViews();
        }

        final ToggleButton toggleBtn = convertView.findViewById(R.id.btn_myschedule_toggle_eventlist_expanded);
        toggleBtn.setOnClickListener(new EventSeriesOnClickListener(convertView, currentItem, roomVisible));
        if(currentItem.getEvents().size() <= 1) {
            toggleBtn.setVisibility(View.INVISIBLE);
        } else {
            toggleBtn.setVisibility(View.VISIBLE);
            toggleBtn.setActivated(false);
        }
        setAndAddEventDataTextViews(currentItem.getFirstEvent(), layoutAllEvents);

        //otherwise it will sometimes be invisible
        convertView.setVisibility(View.VISIBLE);

        convertView.setLayoutParams(new AbsListView.LayoutParams(-1,0));
        return convertView;
    }

    /**
     * Create OnClickListener for the button to add a course by overriding onClick()
     * @param currentItem {@link MyScheduleEventSetVo} the button belongs to
     * @param btnAddCourse the button to add a course that is clicked
     * @return an onClickListener for the button to add a course
     */
    protected abstract View.OnClickListener getAddEventSeriesBtnOnClickListener(
            MyScheduleEventSeriesVo currentItem, Button btnAddCourse);



    /**
     * Listener for click on event series view.
     * On click, the view changes between displaying only the first event of the series
     * and displaying all dates
     */
    private class EventSeriesOnClickListener implements View.OnClickListener{

        private final View convertView;
        // --Commented out by Inspection (28.05.2022 16:41):final boolean roomVisible;

        private final MyScheduleEventSeriesVo currentEventSeries;

        EventSeriesOnClickListener(final View view, final MyScheduleEventSeriesVo currentItem, final boolean showRoom){
            convertView = view;
            currentEventSeries = currentItem;
            roomVisible = showRoom;
        }

        @Override
        public void onClick(final View view) {
            //remove all previously added textviews of event series to avoid duplicates
            final LinearLayout layoutAllEvents = convertView.findViewById(R.id.layout_myschedule_eventseries_events);
            final int layoutAllEventsSize = layoutAllEvents.getChildCount();
            layoutAllEvents.removeAllViews();

            List<MyScheduleEventVo> eventList = new ArrayList<>();
            final ToggleButton toggleBtn = convertView.findViewById(R.id.btn_myschedule_toggle_eventlist_expanded);
            //if list is expanded then collapse
            if(layoutAllEventsSize > 1 ) {
                eventList.add(currentEventSeries.getFirstEvent());
                toggleBtn.setActivated(false);
            } else {
                eventList = currentEventSeries.getEvents();
                toggleBtn.setActivated(true);
            }

            for (final MyScheduleEventVo event : eventList){
                setAndAddEventDataTextViews(event, layoutAllEvents);
            }
            convertView.invalidate();
            convertView.setVisibility(View.VISIBLE);
        }
    }

    void setAndAddEventDataTextViews(final MyScheduleEventVo _Event,
                                     final LinearLayout _LayoutAllEvents){
        final TextView dateAndRoomTextView = new TextView(mContext);
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,5,5,10);
        dateAndRoomTextView.setLayoutParams(params);

        //TODO UTC --- setTimeZone(Locale.ROOT)
        final DateFormat sdf = SimpleDateFormat.getDateInstance();
        String date = sdf.format(_Event.getStartDateTime());

        String dayOfWeek = _Event.getWeekDayShort();
        String startTime = _Event.getStartTimeString();
        String endTime = _Event.getEndTimeString();
        String room = _Event.getLocationListAsString();

        //highlight changes
        for(final TimetableChangeType change : _Event.getTypesOfChanges()){
            switch (change){
                case ADDITION:
                    //set text bold
                    dayOfWeek = "<font><b>" + dayOfWeek + "</b></font>";
                    date = "<font><b>" + date + "</b></font>";
                    startTime = "<font><b>" + startTime + "</b></font>";
                    endTime = "<font><b>" + endTime + "</b></font>";
                    room = "<font><b>" + room + "</b></font>";
                    break;

                case DELETION:
                    //set text strikethrough
                    dateAndRoomTextView.setPaintFlags(dateAndRoomTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    break;

                case EDIT_TIME:
                    dayOfWeek = "<font><b>" + dayOfWeek + "</b></font>";
                    date = "<font><b>" + date + "</b></font>";
                    startTime = "<font><b>" + startTime + "</b></font>";
                    endTime = "<font><b>" + endTime + "</b></font>";
                    break;

                case EDIT_LOCATION:
                    room = "<font><b>" + room + "</b></font>";
                    break;
            }
        }

        String eventDateAndRoomText = dayOfWeek + ", " + date + "  " + startTime + " – " + endTime;
        if(roomVisible) {
            eventDateAndRoomText += "<br>"+ room;
        }

        dateAndRoomTextView.setText(Html.fromHtml(eventDateAndRoomText));
        _LayoutAllEvents.addView(dateAndRoomTextView);
    }


}

