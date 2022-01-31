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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.fragments.mytimetable.MyTimeTableDialogFragment;


public class MyTimeTableOverviewView extends LinearLayout {

    private FragmentManager mFragmentManager;
    private ListView mCourseListView;


    public MyTimeTableOverviewView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTimeTableOverviewView(final Context context) {
        super(context);
    }

    public void initializeView(final FragmentManager _Manager) {
        mFragmentManager = _Manager;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        FloatingActionButton mAddButton = (FloatingActionButton) findViewById(R.id.btn_mytimetable_overview_add_course);
        mAddButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                createAddDialog();
            }
        });


        mCourseListView = (ListView) findViewById(R.id.listview_mytimetable_overview_courses);
        mCourseListView.setAdapter(MainActivity.myTimeTableOverviewAdapter);
    }

    /**
     * finde die Kurse heraus, die nicht mehr enthalten sind.
     * @return
     */
    public static List<String[]> generateNegativeLessons(){

    	final List<String[]> negativeList = new ArrayList<>();

    	//todo: überarbeiten, coursesOfChosenSemester should not be needed here
    	//Durchsuche alle Vorlesungen
//    	for(MyTimeTableCourseComponent eventInCompleteList : MyTimeTableDialogFragment.coursesOfChosenSemester){
//            boolean exists = false;
//
//            //überspringe einzelne Vorlesungsevents wenn der Vorlesungstitel schon zur negativeList hinzugefügt wurde
//    		for(String[] eventdata : negativeList) {
//    		    if(eventdata[0].equals( MyTimeTableCourseComponent.cutEventTitle(eventInCompleteList.getEvent().getTitle()))
//                && eventdata[1].equals( eventInCompleteList.getStudyGroup().getTimeTableId())){
//                    exists = true;
//                    break;
//                }
//            }
//
//    		//dieses eventInCompleteList gibt es noch nicht in der negativeList
//    		if(!exists) {
//    		    boolean isSelected = false;
//    		    //durchsuche subscribedCourseComponents nach dem eventInCompletedList
//                for (MyTimeTableCourseComponent eventInSelectedList : getSubscribedCourses()) {
//
//                    if (MyTimeTableCourseComponent.cutEventTitle(eventInCompleteList.getEvent().getTitle())
//                            .equals(MyTimeTableCourseComponent.cutEventTitle(eventInSelectedList.getEvent().getTitle()))
//                    && eventInCompleteList.getStudyGroup().getTimeTableId()
//                            .equals(eventInSelectedList.getStudyGroup().getTimeTableId())){
//                        isSelected = true;
//                    }
//
//                }
//                //add event to negative List if eventInCompletedList is in subscribedCourseComponents
//                if(!isSelected){
//                    final String[] eventdata = new String[2];
//                    //add an event (title + id) to negativeList
//                    eventdata[0] = MyTimeTableCourseComponent.cutEventTitle(eventInCompleteList.getEvent().getTitle());
//                    eventdata[1] = eventInCompleteList.getStudyGroup().getTimeTableId();
//                    negativeList.add(eventdata);
//                }
//            }
//	    }

       return negativeList;
    }

    /**
     * Sets view to show if the course list is empty
     */
    public void setCourseListEmptyView(){
        final TextView emptyView = new TextView( getContext() );
        emptyView.setText(getResources().getString(R.string.my_time_table_empty_text_select));
        mCourseListView.setEmptyView(emptyView);
    }


    private void createAddDialog(){
        final FragmentManager fm = mFragmentManager;
        MyTimeTableDialogFragment myTimeTableDialogFragment = MyTimeTableDialogFragment.newInstance();
        myTimeTableDialogFragment.show(fm, "fragment_edit_name");
    }
}
