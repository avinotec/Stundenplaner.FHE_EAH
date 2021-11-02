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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.mytimetable.MyTimeTableCalendarAdapter;
import de.fhe.fhemobile.fragments.mytimetable.MyTimeTableFragment;
import de.fhe.fhemobile.vos.timetable.TimeTableEventVo;

/**
 * Created by paul on 12.03.15.
 */
public class MyTimeTableCalendarView extends LinearLayout {

    private static final String TAG = "MyTimeTableCalendarView";

    public MyTimeTableCalendarView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTimeTableCalendarView(final Context context) {
        super(context);
    }



    public void initializeView(final FragmentManager _Manager) {
        mFragmentManager = _Manager;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Button mBtnModifySchedule = (Button) findViewById(R.id.btnMyTimetableModifySchedule);
        mCalendarList =     (ListView)      findViewById(R.id.lvCalendar);

        // "modify schedule"
        mBtnModifySchedule.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                FragmentTransaction transaction = mFragmentManager.beginTransaction();
                transaction.replace(R.id.container, new MyTimeTableFragment(), MyTimeTableFragment.TAG)
                        .addToBackStack(MyTimeTableFragment.TAG)
                        .commit();
                //Wechsle zum EditorFragment (MyTimeTableFragment) mittels backstack
            }
        });

        //TODO Behelf, soll automatisch auf den aktuellen Eintrag vorgesprungen werden
        Button mJumpCurrentLesson = (Button) findViewById(R.id.jumpCurrentLesson);
        mJumpCurrentLesson.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                jumpToToday();
            }
        });

        //deprecated: calendarAdapter = new CalendarAdapter(mContext);
        myTimeTableCalendarAdapter = new MyTimeTableCalendarAdapter( getContext() );
        mCalendarList.setAdapter(myTimeTableCalendarAdapter);
    }

    // Springen auf den aktuellen Tag
    public void jumpToToday(){
        final int currentDayIndex = getCurrentEventIndex();
        if(currentDayIndex >= 0){
            mCalendarList.setSelection(currentDayIndex);
        }
    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy H:mm");
    //don't use SimpleDateFormat.getDateTimeInstance() because it includes seconds

    /**
     *    gehe durch die Liste, bis die Startzeit eines Events größer ist als die angegebene Zeit und nehme den vorherigen Event
     *    und gebe den Index zurück.
     * @return
     */
    public static int getCurrentEventIndex(){

        for(int i = 0; i < MyTimeTableView.sortedLessons.size(); i++){
            final TimeTableEventVo event = MyTimeTableView.sortedLessons.get(i).getEvent();
            final Date now = new Date();
            try {
                //lesson starts now or in the future
                if(sdf.parse(event.getDate() + " " + event.getStartTime())
                        .compareTo(now) >= 0) {
                    //lesson already ended //todo:????
                    if(sdf.parse(event.getDate() + " " + event.getEndTime())
                            .compareTo(now) < 0) {
                        return (i);
                    }

                    if(i == 0) return 0;
                    return (i - 1);
                }
            } catch (ParseException e) {
                Log.e(TAG, "getCurrentEventIndex: ",e );
            }
        }
        return -1;
    }


// --Commented out by Inspection START (02.11.2021 17:22):
//    private void createAddDialog(){
//        final FragmentManager fm = mFragmentManager;
//        MyTimeTableDialogFragment myTimeTableDialogFragment = MyTimeTableDialogFragment.newInstance();
//        myTimeTableDialogFragment.show(fm, "fragment_edit_name");
//
//    }
// --Commented out by Inspection STOP (02.11.2021 17:22)

    public void setEmptyText(final String text){
        TextView emptyView = findViewById(R.id.emptyView);
        emptyView.setText(text);
        mCalendarList.setEmptyView(emptyView);
    }

    //deprecated: private final Context           mContext;
    private FragmentManager   mFragmentManager;

    private ListView mCalendarList;

    private MyTimeTableCalendarAdapter myTimeTableCalendarAdapter;

}
