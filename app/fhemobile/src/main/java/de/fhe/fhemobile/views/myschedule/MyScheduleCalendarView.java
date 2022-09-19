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

import org.junit.Assert;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.fhe.fhemobile.BuildConfig;
import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.utils.Define;


public class MyScheduleCalendarView extends LinearLayout {

    private static final String TAG = MyScheduleCalendarView.class.getSimpleName();


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
     * Set or updated the textview displaying the last updated date
     */
    public void setLastUpdatedTextView(){
        if(BuildConfig.DEBUG) Assert.assertNotNull(mLastUpdatedTextView);

        if(Main.getLastUpdateSubscribedEventSeries() != null){
            mLastUpdatedTextView.setText(String.format("%s %s",
                    getContext().getString(R.string.myschedule_last_updated),
                    sdf.format(Main.getLastUpdateSubscribedEventSeries())));
        } else {
            mLastUpdatedTextView.setText(String.format("%s --", getContext().getString(R.string.myschedule_last_updated)));
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
    private TextView mLastUpdatedTextView;

    static final DateFormat sdf =  new SimpleDateFormat("dd.MM.yy HH:mm", Locale.GERMANY);

}
