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

import static de.fhe.fhemobile.Main.addToSubscribedCourses;
import static de.fhe.fhemobile.Main.getAppContext;
import static de.fhe.fhemobile.Main.getSubscribedCourses;
import static de.fhe.fhemobile.Main.removeFromSubscribedCourses;
import static de.fhe.fhemobile.utils.Utils.correctUmlauts;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.mytimetable.MyTimeTableOverviewAdapter;
import de.fhe.fhemobile.comparator.Date_Comparator;
import de.fhe.fhemobile.fragments.mytimetable.MyTimeTableDialogFragment;
import de.fhe.fhemobile.fragments.mytimetable.MyTimeTableOverviewFragment;
import de.fhe.fhemobile.utils.Define;
import de.fhe.fhemobile.vos.mytimetable.FlatDataStructure;


public class MyTimeTableOverviewView extends LinearLayout {

    private FragmentManager mFragmentManager;
    private ListView mCourseListView;
    private static MyTimeTableOverviewAdapter myTimeTableOverviewAdapter;


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

        myTimeTableOverviewAdapter = new MyTimeTableOverviewAdapter(getContext());

        mCourseListView = (ListView) findViewById(R.id.listview_mytimetable_overview_courses);
        mCourseListView.setAdapter(myTimeTableOverviewAdapter);
    }


    private void createAddDialog(){
        final FragmentManager fm = mFragmentManager;
        MyTimeTableDialogFragment myTimeTableDialogFragment = MyTimeTableDialogFragment.newInstance();
        myTimeTableDialogFragment.show(fm, "fragment_edit_name");
    }

    /**
     * returns the "subscribedCourses" in sorted order
     * @param comparator
     * @return
     */
    private static List<FlatDataStructure> getSortedList(Comparator<FlatDataStructure> comparator){
        final List<FlatDataStructure> sortedList = new ArrayList<FlatDataStructure>(getSubscribedCourses());
        if(sortedList.isEmpty() == false){
            Collections.sort(sortedList,comparator);
        }
        return sortedList;
    }



    /**
     *
     * @param courses
     */
    public static void setSubscribedCourses(final List<FlatDataStructure> courses){
        if(courses != null){
            Main.setSubscribedCourses(courses);
            MyTimeTableOverviewFragment.sortedCourses = getSortedList(new Date_Comparator());
        }
    }

    /**
     * finde die Lessons heraus, die nicht mehr enthalten sind.
     * @return
     */
    public static List<String[]> generateNegativeLessons(){

    	final List<String[]> negativeList = new ArrayList<>();

    	//todo: überarbeiten, coursesOfChosenSemester should not be needed here
    	//Durchsuche alle Vorlesungen
//    	for(FlatDataStructure eventInCompleteList : MyTimeTableDialogFragment.coursesOfChosenSemester){
//            boolean exists = false;
//
//            //überspringe einzelne Vorlesungsevents wenn der Vorlesungstitel schon zur negativeList hinzugefügt wurde
//    		for(String[] eventdata : negativeList) {
//    		    if(eventdata[0].equals( FlatDataStructure.cutEventTitle(eventInCompleteList.getEvent().getTitle()))
//                && eventdata[1].equals( eventInCompleteList.getStudyGroup().getTimeTableId())){
//                    exists = true;
//                    break;
//                }
//            }
//
//    		//dieses eventInCompleteList gibt es noch nicht in der negativeList
//    		if(!exists) {
//    		    boolean isSelected = false;
//    		    //durchsuche subscribedCourses nach dem eventInCompletedList
//                for (FlatDataStructure eventInSelectedList : getSubscribedCourses()) {
//
//                    if (FlatDataStructure.cutEventTitle(eventInCompleteList.getEvent().getTitle())
//                            .equals(FlatDataStructure.cutEventTitle(eventInSelectedList.getEvent().getTitle()))
//                    && eventInCompleteList.getStudyGroup().getTimeTableId()
//                            .equals(eventInSelectedList.getStudyGroup().getTimeTableId())){
//                        isSelected = true;
//                    }
//
//                }
//                //add event to negative List if eventInCompletedList is in subscribedCourses
//                if(!isSelected){
//                    final String[] eventdata = new String[2];
//                    //add an event (title + id) to negativeList
//                    eventdata[0] = FlatDataStructure.cutEventTitle(eventInCompleteList.getEvent().getTitle());
//                    eventdata[1] = eventInCompleteList.getStudyGroup().getTimeTableId();
//                    negativeList.add(eventdata);
//                }
//            }
//	    }

       return negativeList;
    }

    /**
     *
     * @return sorted Lessons list
     */
    public static List<FlatDataStructure> getSortedCourses(){  return MyTimeTableOverviewFragment.sortedCourses;  }

    /**
     *
     * @param course
     */
    public static void removeCourse(final FlatDataStructure course){
        removeFromSubscribedCourses(course);
        MyTimeTableOverviewFragment.sortedCourses = getSortedList(new Date_Comparator());

        final Gson gson = new Gson();
        final String json = correctUmlauts(gson.toJson(getSubscribedCourses()));
        final SharedPreferences sharedPreferences = getAppContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Define.SHARED_PREFERENCES_SUBSCRIBED_COURSES, json);
        editor.apply();

        myTimeTableOverviewAdapter.notifyDataSetChanged();
    }

    /**
     *
     * @param course
     */
    public static void addCourse(final FlatDataStructure course){
        addToSubscribedCourses(course);
        MyTimeTableOverviewFragment.sortedCourses = getSortedList(new Date_Comparator());

        final Gson gson = new Gson();
        final String json = correctUmlauts(gson.toJson(getSubscribedCourses()));
        final SharedPreferences sharedPreferences = getAppContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Define.SHARED_PREFERENCES_SUBSCRIBED_COURSES, json);
        editor.apply();
        myTimeTableOverviewAdapter.notifyDataSetChanged();
    }

    /**
     * Sets view to show if the course list is empty
     */
    public void setCourseListEmptyView(){
        final TextView emptyView = new TextView( getContext() );
        emptyView.setText(getResources().getString(R.string.my_time_table_empty_text_select));
        mCourseListView.setEmptyView(emptyView);
    }

}
