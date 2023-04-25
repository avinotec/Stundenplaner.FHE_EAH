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
package de.fhe.fhemobile.views.myschedule;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.events.Event;
import de.fhe.fhemobile.events.EventListener;
import de.fhe.fhemobile.events.MyScheduleChangeEvent;
import de.fhe.fhemobile.models.myschedule.MyScheduleModel;
import de.fhe.fhemobile.services.FetchMyScheduleBackgroundTask;
import de.fhe.fhemobile.utils.Define;


public class MyScheduleCalendarView extends LinearLayout {

    //-- STATIC --------------------------------------------------------------------------------
    private static final String TAG = MyScheduleCalendarView.class.getSimpleName();
    private static TextView mLastUpdatedTextView;


    /**
     * Set (or update) the text view displaying the date the schedule has been last updated
     */
    public void setLastUpdatedTextView(){

        //don't do any timezone magic here (like in MySchedule Events)
        // because Main.getLastUpdateSubscribedEventSeries() is an accurately generated date object
        final DateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());

        if(mLastUpdatedTextView != null){
            if(MyScheduleModel.getInstance().getLastUpdateSubscribedEventSeries() != null){
                mLastUpdatedTextView.setText(String.format("%s %s",
                        Main.getAppContext().getString(R.string.myschedule_last_updated),
                        sdf.format(MyScheduleModel.getInstance().getLastUpdateSubscribedEventSeries())));
            } else {
                mLastUpdatedTextView.setText(String.format("%s --", Main.getAppContext().getString(R.string.myschedule_last_updated)));
            }
        }
    }

    //-- END STATIC --------------------------------------------------------------------------------


    public MyScheduleCalendarView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScheduleCalendarView(final Context context) {
        super(context);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mCalendarListView = findViewById(R.id.lv_myschedule_calendar_courses);
        mCalendarListView.setAdapter(MyScheduleModel.getMyScheduleCalendarAdapter());

        mLastUpdatedTextView = findViewById(R.id.tv_myschedule_calendar_last_updated);
        if(!Define.ENABLE_MYSCHEDULE_UPDATING){
            //disable showing last updating time
            mLastUpdatedTextView.setVisibility(GONE);
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_myschedule_calendar);
    }

    public void initializeView(){
        //Init view with empty calendar
        setEmptyCalendarView();
        setLastUpdatedTextView();


        //refresh gesture
        if(Define.ENABLE_MYSCHEDULE_UPDATING){
            mSwipeRefreshLayout.setOnRefreshListener(() -> {
                if(MyScheduleModel.getInstance().getSubscribedEventSeries().size() > 0){
                    mSwipeRefreshLayout.setRefreshing(true);
                    FetchMyScheduleBackgroundTask.fetch();
                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    public void registerModelListener() {
        MyScheduleModel.getInstance().addListener(MyScheduleChangeEvent.MYSCHEDULE_UPDATED,
                mUpdatedMyScheduleEventListener);
    }

    public void deregisterModelListener() {
        MyScheduleModel.getInstance().removeListener(MyScheduleChangeEvent.MYSCHEDULE_UPDATED,
                mUpdatedMyScheduleEventListener);
    }


// --Commented out by Inspection START (29.03.2023 02:22):
//    /**
//     * View scrolls to today
//     * //outdated: My Schedule Calendar list was changed to not displaying past days
//     */
//    public void jumpToToday(){
//        final int currentDayIndex = MyScheduleModel.getInstance().getMyScheduleCalendarAdapter().getPositionOfFirstEventToday();
//        if(currentDayIndex >= 0){
//            mCalendarListView.setSelection(currentDayIndex);
//        }
//    }
// --Commented out by Inspection STOP (29.03.2023 02:22)

    /**
     * Sets view to show if the event list is empty
     */
    public void setEmptyCalendarView(){
        final TextView emptyView = findViewById(R.id.tv_myschedule_calendar_empty);
        mCalendarListView.setEmptyView(emptyView);
    }

    /**
     * Stop refreshing animation started by swipe down gesture
     */
    public void stopRefreshingAnimation() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private final EventListener mUpdatedMyScheduleEventListener = new EventListener() {
        @Override
        public void onEvent(Event event) {
            setLastUpdatedTextView();
            stopRefreshingAnimation();
        }
    };


    private ListView mCalendarListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

}
