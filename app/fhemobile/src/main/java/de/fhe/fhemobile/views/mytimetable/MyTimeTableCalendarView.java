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

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.fragments.mytimetable.MyTimeTableOverviewFragment;


public class MyTimeTableCalendarView extends LinearLayout {

    private static final String TAG = "MyTimeTableCalendarView";


    public MyTimeTableCalendarView(final Context context, final AttributeSet attrs) { super(context, attrs);  }

    public MyTimeTableCalendarView(final Context context) {
        super(context);
    }



    public void initializeView(final FragmentManager _Manager) {
        mFragmentManager = _Manager;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mCalendarListView = (ListView) findViewById(R.id.listview_mytimetable_calendar_courses);

        // "modify schedule"
        final Button mBtnModifySchedule = (Button) findViewById(R.id.btn_mytimetable_calendar_modify_Schedule);
        mBtnModifySchedule.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {

                //todo: listener ins Fragment verlagern
                //((MainActivity) getActivity()).changeFragment() ...

                final FragmentTransaction transaction = mFragmentManager.beginTransaction();
                transaction.replace(R.id.container, new MyTimeTableOverviewFragment(), MyTimeTableOverviewFragment.TAG)
                        .addToBackStack(MyTimeTableOverviewFragment.TAG)
                        .commit();
                //Wechsle zum EditorFragment (MyTimeTableOverviewFragment) mittels backstack
            }
        });

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

    // Springen auf den aktuellen Tag
    public void jumpToToday(){
        final int currentDayIndex = MainActivity.myTimeTableCalendarAdapter.getPositionOfFirstCourseToday();
        if(currentDayIndex >= 0){
            mCalendarListView.setSelection(currentDayIndex);
        }
    }




// --Commented out by Inspection START (02.11.2021 17:22):
//    private void createAddDialog(){
//        final FragmentManager fm = mFragmentManager;
//        MyTimeTableDialogFragment myTimeTableDialogFragment = MyTimeTableDialogFragment.newInstance();
//        myTimeTableDialogFragment.show(fm, "fragment_edit_name");
//
//    }
// --Commented out by Inspection STOP (02.11.2021 17:22)

    /**
     * Sets view to show if the course list is empty
     */
    public void setEmptyCalenderView(){
        final TextView emptyView = new TextView( getContext() );
        emptyView.setText(getResources().getString(R.string.my_time_table_empty_text_select));
        mCalendarListView.setEmptyView(emptyView);
    }


    private FragmentManager   mFragmentManager;

    private ListView mCalendarListView;

}
