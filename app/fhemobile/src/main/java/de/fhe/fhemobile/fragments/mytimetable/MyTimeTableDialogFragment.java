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

package de.fhe.fhemobile.fragments.mytimetable;


import static android.content.ContentValues.TAG;
import static de.fhe.fhemobile.Main.getAppContext;
import static de.fhe.fhemobile.Main.getSubscribedCourses;
import static de.fhe.fhemobile.utils.Utils.correctUmlauts;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.mytimetable.MyTimeTableDialogAdapter;
import de.fhe.fhemobile.comparator.CourseTitleStudyGroupTitleComparator;
import de.fhe.fhemobile.network.MyTimeTableCallback;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.utils.MyTimeTableUtils;
import de.fhe.fhemobile.utils.Utils;
import de.fhe.fhemobile.views.mytimetable.MyTimeTableDialogView;
import de.fhe.fhemobile.vos.mytimetable.MyTimeTableCourse;
import de.fhe.fhemobile.vos.timetable.TimeTableDayVo;
import de.fhe.fhemobile.vos.timetable.TimeTableEventVo;
import de.fhe.fhemobile.vos.timetable.TimeTableResponse;
import de.fhe.fhemobile.vos.timetable.TimeTableSemesterVo;
import de.fhe.fhemobile.vos.timetable.TimeTableStudyCourseVo;
import de.fhe.fhemobile.vos.timetable.TimeTableStudyGroupVo;
import de.fhe.fhemobile.vos.timetable.TimeTableWeekVo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyTimeTableDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * Display a modal dialog in front of the fragment of MyTimeTable
 */
public class MyTimeTableDialogFragment extends DialogFragment {

    public static final String PREFS_CHOSEN_SEMESTER = "_ChosenSemester";
    public static final String PREFS_CHOSEN_STUDY_COURSE = "_ChosenSudyCourse";
    public static final String PREFS_ALL_COURSES_OF_CHOSEN_STUDYCOURSE_AND_SEMESTER = "_Result";
    public static final String PREFS_COURSE_LIST = "MyTimeTableDialog_course_list";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MyTimeTableOverviewFragment.
     */
    public static MyTimeTableDialogFragment newInstance() {
        MyTimeTableDialogFragment fragment = new MyTimeTableDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MyTimeTableDialogFragment() {
        // Required empty public constructor
    }

    // resize the dialog to fit to the full display
    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mChosenStudyCourse = null;
        mChosenSemester = null;

	    mCourseListAdapter = new MyTimeTableDialogAdapter(getAppContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (MyTimeTableDialogView) inflater.inflate(R.layout.fragment_my_time_table_dialog,
                container, false);

        mView.initializeView(getChildFragmentManager());
        mView.setViewListener(mViewListener);

        mView.setCourseListAdapter(mCourseListAdapter);

        //todo: loading course list from shared preferences risks to display an outdated version,
        // the user has to re-select his study course and semester to update it.
        // This is not intuitive and not how the user predicts MyTimeTableDialog to work.
        // Thus this function should be commented out till or replaced by a less risky method.
        //loadSelectionFromSharedPreferences();

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        NetworkHandler.getInstance().fetchTimeTable(mTimeTableResponseCallback);
    }


    /**
     * Adds the course represented by timeTableWeeks to allCoursesInChosenSemester
     *
     * Method for collecting all courses and events of all study groups that belong to study course
     * and semester chosen by the user in the My Time Table Dialog
     * @param timeTableWeeks list of TimeTableWeeks of the remaining semester for the study group
     *                       (weeks contain days which contain TimeTableEvents)
     * @param studyGroup study group the timeTableWeeks belong to
     */
    private void addToAllCoursesInChosenSemester(final List<TimeTableWeekVo> timeTableWeeks,
                                                 final TimeTableStudyGroupVo studyGroup) {

        if (timeTableWeeks != null) {
            try {

                for(TimeTableWeekVo timeTableWeek : timeTableWeeks){
                    for(TimeTableDayVo timeTableDay : timeTableWeek.getDays()){
                        for(TimeTableEventVo timeTableEvent : timeTableDay.getEvents()){


                            //check if a course corresponding to timeTableEvent already exists in allCoursesInChosenSemester
                            // because the course has already been added with a previous study group or other event of the course
                            MyTimeTableCourse alreadyExistingCourse =
                                    getCorrespondingCourseInAllCoursesInChosenSemester(timeTableEvent);


                            //new MyTimeTableCourse Object needed
                            if ( alreadyExistingCourse == null ) {

                                MyTimeTableCourse courseToAdd = new MyTimeTableCourse(
                                        mChosenStudyCourse,
                                        mChosenSemester,
                                        timeTableEvent,
                                        studyGroup.getTitle().split("\\.")[1],
                                        false);

                                //check if timeTableEvent belongs to a subscribed course
                                for(MyTimeTableCourse subscribedCourse : getSubscribedCourses()){
                                    if (courseToAdd.isEqual(subscribedCourse)){

                                        courseToAdd.setSubscribed(true);
                                    }
                                }

                                //add new course
                                this.allCoursesInChosenSemester.add(courseToAdd);
                            }
                            //allCoursesInChosenSemester already contains an the course the event belongs to,
                            // then only add the study group to the study group list of the existing course
                            else {
                                alreadyExistingCourse.addStudyGroup(studyGroup);
                            }

                        }
                    }
                }
            }
            catch (Exception e){
                Log.e(TAG, "problem merging new data into allCoursesInChosenSemester",e);
            }
        }
    }

    /**
     * Returns the course the event belongs to if it already exists in allCoursesInChosenSemester
     * @param event the {@link TimeTableEventVo} to search for
     * @return course from allCoursesInChosenSemester; null if no corresponding course was found
     */
    private MyTimeTableCourse getCorrespondingCourseInAllCoursesInChosenSemester(TimeTableEventVo event) {
        for ( MyTimeTableCourse courseInChosenSemester : this.allCoursesInChosenSemester) {
            if (courseInChosenSemester.getTitle().equals(MyTimeTableUtils.getCourseName(event))){
                return courseInChosenSemester;
            }
        }
        return null;
    }


    /**
     * Setzt die beim letzten Mal ausgewählten Werte und die letzten Suchergebnisse.
     */
    private void loadSelectionFromSharedPreferences(){
        final Gson gson = new Gson();

        SharedPreferences sharedPreferences = this.getContext().getSharedPreferences(PREFS_COURSE_LIST, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(sharedPreferences.contains(PREFS_CHOSEN_STUDY_COURSE)){
            final String chosenCourseJson = sharedPreferences.getString(PREFS_CHOSEN_STUDY_COURSE,"");
            final TimeTableStudyCourseVo chosenCourse = gson.fromJson(chosenCourseJson, TimeTableStudyCourseVo.class);
            mChosenStudyCourse = chosenCourse;
            mView.setSemesterItems(mChosenStudyCourse.getSemesters());
            mView.setSelectedGroupText(chosenCourse.getTitle());
        }

        if(sharedPreferences.contains(PREFS_CHOSEN_SEMESTER)) {
            final String chosenSemesterJson = sharedPreferences.getString(PREFS_CHOSEN_SEMESTER, "");
            final TimeTableSemesterVo chosenSemester = gson.fromJson(chosenSemesterJson, TimeTableSemesterVo.class);
            mChosenSemester = chosenSemester;
            mView.setSelectedSemesterText(chosenSemester.getTitle());
            mView.toggleSemesterPickerVisibility(true);
            mView.setSemesterPickerEnabled(true);



            //load last request result for the last chosen study course and semester
            if (sharedPreferences.contains(PREFS_ALL_COURSES_OF_CHOSEN_STUDYCOURSE_AND_SEMESTER)) {
                final String resultJson = correctUmlauts(sharedPreferences.getString(PREFS_ALL_COURSES_OF_CHOSEN_STUDYCOURSE_AND_SEMESTER, ""));
                final MyTimeTableCourse[] coursesFromSharedPreferences = gson.fromJson(resultJson, MyTimeTableCourse[].class);

                //for each course in coursesFromSharedPreferences,
                // check if the course is in the courses the user subscribed to
                // and update the subscribed property
                for(MyTimeTableCourse loadedCourse : coursesFromSharedPreferences){
                    boolean found = false;
                    for(MyTimeTableCourse subscribedCourse : getSubscribedCourses()){
                        if(loadedCourse.isEqual(subscribedCourse)){
                            found = true;
                            break;
                        }
                    }
                    loadedCourse.setSubscribed( found );

                }
                //set all courses of the currently chosen study course and semester (not the variable for the subscribed courses)
                allCoursesInChosenSemester = new ArrayList<MyTimeTableCourse>(Arrays.asList(coursesFromSharedPreferences));

                mCourseListAdapter.setItems(allCoursesInChosenSemester);
                mView.setCourseListAdapter(mCourseListAdapter);
                mView.toggleCourseListVisibility(true);
                mCourseListAdapter.notifyDataSetChanged();
            }
        }
    }


    private final MyTimeTableDialogView.IViewListener mViewListener = new MyTimeTableDialogView.IViewListener() {

        @Override
        public void onStudyCourseChosen(final String _StudyCourseId) {
            //reset needed because a new study course had been chosen
            mView.resetSemesterPicker();
            mView.toggleCourseListVisibility(false);

            allCoursesInChosenSemester.clear();
            mCourseListAdapter.setItems(allCoursesInChosenSemester);
            mCourseListAdapter.notifyDataSetChanged();

            mChosenStudyCourse = null;
            mChosenSemester = null;


            boolean studyCourseEmpty = true;

            for (TimeTableStudyCourseVo studyCourse : mResponse.getStudyCourses()) {
                if (studyCourse != null && studyCourse.getId() != null && studyCourse.getId().equals(_StudyCourseId)) {

                    mChosenStudyCourse = studyCourse;

                    // Check if study course has any semesters available
                    if (studyCourse.getSemesters() != null) {
                        studyCourseEmpty = false;

                        //save chosen study course to shared preferences
                        SharedPreferences sharedPreferences = getContext().getSharedPreferences(PREFS_COURSE_LIST, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String chosenStudyCourseJson = correctUmlauts(new Gson().toJson(studyCourse));
                        editor.putString(PREFS_CHOSEN_STUDY_COURSE, chosenStudyCourseJson);
                        editor.commit();

                        //set items of semester picker
                        mView.setSemesterItems(studyCourse.getSemesters());
                    }
                    // No terms are available
                    else {
                        studyCourseEmpty = true;
                    }

                    //chosen study course found
                    break;
                }
                else {
                    // No Id is available, chosen study course not found
                    studyCourseEmpty = true;
                }
            }

            if (studyCourseEmpty) {
                mView.toggleSemesterPickerVisibility(false);
                Utils.showToast(R.string.timetable_error);
            }
            else {
                mView.toggleSemesterPickerVisibility(true);
            }

        }

        /**
         *
         * @param _SemesterId
         */
        @Override
        public void onSemesterChosen(String _SemesterId) {
            mView.toggleCourseListVisibility(false);
            allCoursesInChosenSemester.clear();
            mCourseListAdapter.setItems(allCoursesInChosenSemester);
            mCourseListAdapter.notifyDataSetChanged();

            mChosenSemester = null;

            for (TimeTableSemesterVo semester : mChosenStudyCourse.getSemesters()) {

                if (semester.getId().equals(_SemesterId)) {

                    //set chosen semester
                    mChosenSemester = semester;
                    //save chosen semester to shared preferences
                    final Gson gson = new Gson();
                    final String chosenSemesterJson = correctUmlauts(gson.toJson(mChosenSemester));
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences(PREFS_COURSE_LIST, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(PREFS_CHOSEN_SEMESTER, chosenSemesterJson);
                    editor.commit();

                    //get timetable (all courses/events) for each study group in the chosen semester
                    for (TimeTableStudyGroupVo timeTableStudyGroupVo : mChosenSemester.getStudyGroups()) {

                        //get timetable of the timeTableStudyGroupVo
                        MyTimeTableCallback<ArrayList<TimeTableWeekVo>> callback =
                                new MyTimeTableCallback<ArrayList<TimeTableWeekVo>>(
                                        mChosenStudyCourse, mChosenSemester, timeTableStudyGroupVo) {

                            @Override
                            public void onResponse(Call<ArrayList<TimeTableWeekVo>> call,
                                                   Response<ArrayList<TimeTableWeekVo>> response) {
                                super.onResponse(call, response);

                                if(response.code() >= 200) {
                                    //week-wise timetables of the remaining semester for the current studyGroup in the for-loop
                                    List<TimeTableWeekVo> timeTableWeek = response.body();

                                    //add all time table events of the remaining semester (= list of timetableWeeks)
                                    // to the list of allCoursesInChosenSemester
                                    addToAllCoursesInChosenSemester(timeTableWeek, this.getStudyGroup());


                                    // es gibt zu jedem Request unterschiedliche Anzahl von Anforderungen und Antworten
                                    // wir warten, bis alle Antworten eingetroffen sind.
                                    //Wir sortieren diesen letzten Stand der Liste
                                    //Wichtig: --requestCounter nicht requestCounter--! Hier muss erst decrementiert werden und dann der vergleich stattfinden.
                                    if (--requestCounter <= 0) {

                                        try {
                                            Collections.sort(allCoursesInChosenSemester,
                                                    new CourseTitleStudyGroupTitleComparator());
                                        } catch ( final RuntimeException e ) {
                                            Log.e(TAG, "Fehler beim Sortieren", e); //$NON-NLS
                                        }

                                        mView.toggleCourseListVisibility(true);
                                        mCourseListAdapter.setItems(allCoursesInChosenSemester);
                                        mCourseListAdapter.notifyDataSetChanged();
                                        mView.setCourseListAdapter(mCourseListAdapter);

                                        final Gson gson = new Gson();
                                        final String chosenSemesterJson = correctUmlauts(
                                                gson.toJson(allCoursesInChosenSemester));
                                        editor.putString(PREFS_ALL_COURSES_OF_CHOSEN_STUDYCOURSE_AND_SEMESTER, chosenSemesterJson);
                                        editor.commit();

                                    }
                                }

                            }

                            @Override
                            public void onFailure(Call<ArrayList<TimeTableWeekVo>> call, Throwable t) {
                                super.onFailure(call, t);
                            }
                        };

                        NetworkHandler.getInstance().fetchTimeTableEvents(timeTableStudyGroupVo.getTimeTableId(), callback);
                        requestCounter++;
                    }
                }
            }
        }

    };

    private final Callback<TimeTableResponse> mTimeTableResponseCallback = new Callback<TimeTableResponse>() {
        @Override
        public void onResponse(Call<TimeTableResponse> call, Response<TimeTableResponse> response) {
            if ( response.body() != null ) {

                //store the response, to work on later
                mResponse = response.body();

                mView.setStudyCourseItems(mResponse.getStudyCourses());
            }
//            Log.d(TAG, "success: request  " + response.raw().request().url()
//                    + ", status: "+response.code());
        }

        @Override
        public void onFailure(Call<TimeTableResponse> call, Throwable t) {
            Log.d(TAG, "failure: request " + call.request().url());
        }
    };

    private MyTimeTableDialogView mView;
    private TimeTableResponse mResponse;
    private TimeTableStudyCourseVo mChosenStudyCourse;
    private TimeTableSemesterVo mChosenSemester;
    //list of courses for the study course and semester currently chosen in MyTimeTableDialog
    private List<MyTimeTableCourse> allCoursesInChosenSemester = new ArrayList<>();

    private MyTimeTableDialogAdapter mCourseListAdapter;

    /** es gibt zu EINEM Request unterschiedliche Anzahl von Anforderungen und Antworten
     *  wir warten, bis alle Antworten eingetroffen sind.
     */
    private volatile int requestCounter = 0;

}
