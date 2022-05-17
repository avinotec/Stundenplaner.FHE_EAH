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
package de.fhe.fhemobile.views.mytimetable;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;


public class MyTimeTableCalendarView extends LinearLayout {

    private static final String TAG = MyTimeTableCalendarView.class.getSimpleName();


    public MyTimeTableCalendarView(final Context context, final AttributeSet attrs) { super(context, attrs);  }

    public MyTimeTableCalendarView(final Context context) {
        super(context);
    }



    public void initializeView(final OnClickListener onClickListener) {
        // button "modify schedule"
        final Button mBtnModifySchedule = (Button) findViewById(R.id.btn_mytimetable_calendar_modify_Schedule);
        mBtnModifySchedule.setOnClickListener(onClickListener);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mCalendarListView = (ListView) findViewById(R.id.lv_mytimetable_calendar_courses);

        //TODO Behelf, soll automatisch auf den aktuellen Eintrag vorgesprungen werden
        final Button mJumpCurrentLesson = (Button) findViewById(R.id.btn_mytimetable_calendar_jump_to_today);
        mJumpCurrentLesson.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                jumpToToday();
            }
        });

        mCalendarListView.setAdapter(MainActivity.myTimeTableCalendarAdapter);
    }

    /**
     * View scrolls to today
     */
    public void jumpToToday(){
        final int currentDayIndex = MainActivity.myTimeTableCalendarAdapter.getPositionOfFirstEventToday();
        if(currentDayIndex >= 0){
            mCalendarListView.setSelection(currentDayIndex);
        }
    }


    /**
     * Sets view to show if the event list is empty
     */
    public void setEmptyCalenderView(){
        final TextView emptyView = new TextView( getContext() );
        emptyView.setText(getResources().getString(R.string.my_time_table_empty_text_select));
        mCalendarListView.setEmptyView(emptyView);
    }


    private ListView mCalendarListView;

}
