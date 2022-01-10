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

import static de.fhe.fhemobile.utils.Utils.correctUmlauts;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.mytimetable.MyTimeTableSelectedCoursesAdapter;
import de.fhe.fhemobile.comparator.Date_Comparator;
import de.fhe.fhemobile.fragments.mytimetable.MyTimeTableDialogFragment;
import de.fhe.fhemobile.fragments.mytimetable.MyTimeTableFragment;
import de.fhe.fhemobile.utils.Define;
import de.fhe.fhemobile.vos.timetable.FlatDataStructure;

/**
 * Created by paul on 12.03.15.
 */
public class MyTimeTableView extends LinearLayout {

    //TODO: im View sollten keine Daten liegen
    /** sortedCourses: Liste der selectedCourses sortiert für die Ausgabe im view, ausschließlich dafür benötigt. */
    public static List<FlatDataStructure> sortedCourses = new ArrayList<>();
    public static List<FlatDataStructure> selectedCourses = new ArrayList();

    private FragmentManager mFragmentManager;
    private ListView mCoursesListView;
    private static MyTimeTableSelectedCoursesAdapter myTimeTableSelectedCoursesAdapter;



    public MyTimeTableView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTimeTableView(final Context context) {
        super(context);
    }

    public void initializeView(final FragmentManager _Manager) {
        mFragmentManager = _Manager;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Button mAddButton = (Button) findViewById(R.id.timetableAddCourse);
        mAddButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                createAddDialog();
            }
        });

        myTimeTableSelectedCoursesAdapter = new MyTimeTableSelectedCoursesAdapter(getContext());

        mCoursesListView = (ListView) findViewById(R.id.ListViewCourses);
        mCoursesListView.setAdapter(myTimeTableSelectedCoursesAdapter);
    }


    private void createAddDialog(){
        final FragmentManager fm = mFragmentManager;
        MyTimeTableDialogFragment myTimeTableDialogFragment = MyTimeTableDialogFragment.newInstance();
        myTimeTableDialogFragment.show(fm, "fragment_edit_name");
    }

    /**
     * returns the "selectedCourses" in sorted order
     * @param comparator
     * @return
     */
    private static List<FlatDataStructure> getSortedList(Comparator<FlatDataStructure> comparator){
        final List<FlatDataStructure> sortedList = new ArrayList<FlatDataStructure>(selectedCourses);
        if(sortedList.isEmpty() == false){
            Collections.sort(sortedList,comparator);
        }
        return sortedList;
    }

    /**
     * Stores the list of allCoursesOfSelectedSemesters
     * @param allSelectedCourses
     */
    public static void setAllSelectedCourses(List<FlatDataStructure> allSelectedCourses){
        if(allSelectedCourses == null){
            MyTimeTableFragment.allCoursesOfSelectedSemesters = new ArrayList<>();
        } else {
            MyTimeTableFragment.allCoursesOfSelectedSemesters = allSelectedCourses;
        }
    }

    /**
     * returns the complete course list for the chosen study course and semester
     * @return allCoursesOfSelectedSemesters
     */
    public static List<FlatDataStructure> getAllCoursesOfChosenStudyCourseAndSemester(){
        return MyTimeTableFragment.allCoursesOfSelectedSemesters;
    }

    /**
     *
     * @param courses
     */
    public static void setSelectedCourses(final List<FlatDataStructure> courses){
        if(courses == null){
            selectedCourses = new ArrayList<>();
        } else {

            selectedCourses = courses;
            sortedCourses = getSortedList(new Date_Comparator());
        }

    }

    /**
     *
     * @return
     */
    public static List<FlatDataStructure> getSelectedCourses(){
        return selectedCourses;
    }

    /**
     * finde die Lessons heraus, die nicht mehr enthalten sind.
     * @return
     */
    public static List<String[]> generateNegativeLessons(){

    	final List<String[]> negativeList = new ArrayList<>();

    	//Durchsuche alle Vorlesungen
    	for(FlatDataStructure eventInCompleteList : MyTimeTableFragment.allCoursesOfSelectedSemesters){
            boolean exists = false;

            //überspringe einzelne Vorlesungsevents wenn der Vorlesungstitel schon zur negativeList hinzugefügt wurde
    		for(String[] eventdata : negativeList) {
    		    if(eventdata[0].equals( FlatDataStructure.cutEventTitle(eventInCompleteList.getEvent().getTitle()))
                && eventdata[1].equals( eventInCompleteList.getStudyGroup().getTimeTableId())){
                    exists = true;
                    break;
                }
            }

    		//dieses eventInCompleteList gibt es noch nicht in der negativeList
    		if(!exists) {
    		    boolean isSelected = false;
    		    //durchsuche selectedCourses nach dem eventInCompletedList
                for (FlatDataStructure eventInSelectedList : selectedCourses) {

                    if (FlatDataStructure.cutEventTitle(eventInCompleteList.getEvent().getTitle())
                            .equals(FlatDataStructure.cutEventTitle(eventInSelectedList.getEvent().getTitle()))
                    && eventInCompleteList.getStudyGroup().getTimeTableId()
                            .equals(eventInSelectedList.getStudyGroup().getTimeTableId())){
                        isSelected = true;
                    }

                }
                //add event to negative List if eventInCompletedList is in selectedCourses
                if(!isSelected){
                    final String[] eventdata = new String[2];
                    //add an event (title + id) to negativeList
                    eventdata[0] = FlatDataStructure.cutEventTitle(eventInCompleteList.getEvent().getTitle());
                    eventdata[1] = eventInCompleteList.getStudyGroup().getTimeTableId();
                    negativeList.add(eventdata);
                }
            }
	    }

       return negativeList;
    }

    /**
     *
     * @return sorted Lessons list
     */
    public static List<FlatDataStructure> getSortedCourses(){  return sortedCourses;  }

    /**
     *
     * @param lesson
     */
    public static void removeCourse(final FlatDataStructure lesson){
        selectedCourses.remove(lesson);
        sortedCourses = getSortedList(new Date_Comparator());

        final Gson gson = new Gson();
        final String json = correctUmlauts(gson.toJson(MyTimeTableView.getSelectedCourses()));
        final SharedPreferences sharedPreferences = Main.getAppContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Define.SHARED_PREFERENCES_COURSES_LIST, json);
        editor.apply();

        myTimeTableSelectedCoursesAdapter.notifyDataSetChanged();
    }

    /**
     *
     * @param lesson
     */
    public static void addCourse(final FlatDataStructure lesson){
        selectedCourses.add(lesson);
        sortedCourses = getSortedList(new Date_Comparator());

        final Gson gson = new Gson();
        final String json = correctUmlauts(gson.toJson(MyTimeTableView.getSelectedCourses()));
        final SharedPreferences sharedPreferences = Main.getAppContext().getSharedPreferences("prefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Define.SHARED_PREFERENCES_COURSES_LIST, json);
        editor.apply();
        myTimeTableSelectedCoursesAdapter.notifyDataSetChanged();
    }

    /**
     *
     * @param selectedCourses
     * @return
     */
// not used
//    public static List<FlatDataStructure> loadSelectedList(final List<FlatDataStructure> selectedCourses){
//
//        final List<FlatDataStructure> completeSelectedList = new ArrayList();
//        final List<String> studygroups = new ArrayList();
//
//        for(final FlatDataStructure event:selectedCourses){
//            if(!studygroups.contains(event.getStudyGroup().getTimeTableId())){
//                studygroups.add(event.getStudyGroup().getTimeTableId());
//                completeSelectedList.addAll(NetworkHandler.getInstance().reloadEvents(event));
//            }
//        }
//
//        return completeSelectedList;
//    }

    /**
     *
     * @param text
     */
    public void setEmptyText(final String text){
        final TextView emptyView = findViewById(R.id.emptyView);
        emptyView.setText(text);
        mCoursesListView.setEmptyView(emptyView);
    }

}
