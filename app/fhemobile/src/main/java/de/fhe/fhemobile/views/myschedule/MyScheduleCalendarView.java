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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.utils.Define;


public class MyScheduleCalendarView extends LinearLayout {

    private static final String TAG = MyScheduleCalendarView.class.getSimpleName();
    private static final DateFormat sdf =  new SimpleDateFormat("dd.MM.yy HH:mm", Locale.GERMANY);
    private static TextView mLastUpdatedTextView;

    /**
     * Set (or update) the text view displaying the date the schedule has been last updated
     */
    public static void setLastUpdatedTextView(){
        if(mLastUpdatedTextView != null){
            if(Main.getLastUpdateSubscribedEventSeries() != null){
                mLastUpdatedTextView.setText(String.format("%s %s",
                        Main.getAppContext().getString(R.string.myschedule_last_updated),
                        sdf.format(Main.getLastUpdateSubscribedEventSeries())));
            } else {
                mLastUpdatedTextView.setText(String.format("%s --", Main.getAppContext().getString(R.string.myschedule_last_updated)));
            }
        }
    }


    public MyScheduleCalendarView(final Context context, final AttributeSet attrs) { super(context, attrs);  }

    public MyScheduleCalendarView(final Context context) {
        super(context);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mCalendarListView = findViewById(R.id.lv_myschedule_calendar_courses);
        mCalendarListView.setAdapter(MainActivity.myScheduleCalendarAdapter);

        mLastUpdatedTextView = findViewById(R.id.tv_myschedule_calendar_last_updated);
        if(!Define.ENABLE_MYSCHEDULE_UPDATING_AND_PUSHNOTIFICATIONS){
            mLastUpdatedTextView.setVisibility(GONE);
        }
    }

    /**
     * View scrolls to today
     */
    public void jumpToToday(){
        final int currentDayIndex = MainActivity.myScheduleCalendarAdapter.getPositionOfFirstEventToday();
        if(currentDayIndex >= 0){
            mCalendarListView.setSelection(currentDayIndex);
        }
    }

    /**
     * Sets view to show if the event list is empty
     */
    public void setEmptyCalendarView(){
        final TextView emptyView = findViewById(R.id.tv_myschedule_calendar_empty);
        mCalendarListView.setEmptyView(emptyView);
    }


    private ListView mCalendarListView;

}
