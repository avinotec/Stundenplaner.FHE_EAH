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
package de.fhe.fhemobile.views.timetable;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.adapters.timetable.CalendarAdapter;
import de.fhe.fhemobile.fragments.mytimetable.MyTimeTableDialogFragment;
import de.fhe.fhemobile.fragments.mytimetable.MyTimeTableFragment;

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
        mBtnModifySchedule = (Button)       findViewById(R.id.btnMyTimetableModifySchedule);
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
                jumpCurrentLesson();
            }
        });

        //deprecated: calendarAdapter = new CalendarAdapter(mContext);
        calendarAdapter = new CalendarAdapter( getContext() );
        mCalendarList.setAdapter(calendarAdapter);
    }

    // Springen auf den aktuellen Tag
    public void jumpCurrentLesson(){
        final int currentDayIndex = MainActivity.getCurrentEventIndex();
        if(currentDayIndex >= 0){
            mCalendarList.setSelection(currentDayIndex);
        }
    }


    private void createAddDialog(){
        final FragmentManager fm = mFragmentManager;
        MyTimeTableDialogFragment myTimeTableDialogFragment = MyTimeTableDialogFragment.newInstance();
        myTimeTableDialogFragment.show(fm, "fragment_edit_name");

    }

    public void setEmptyText(final String text){
        TextView emptyView = findViewById(R.id.emptyView);
        emptyView.setText(text);
        mCalendarList.setEmptyView(emptyView);
    }

    //deprecated: private final Context           mContext;
    private FragmentManager   mFragmentManager;

    private Button mBtnModifySchedule;
    private ListView mCalendarList;

    private CalendarAdapter calendarAdapter;

}
