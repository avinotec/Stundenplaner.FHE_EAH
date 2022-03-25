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

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.mytimetable.MyTimeTableDialogAdapter;
import de.fhe.fhemobile.comparator.CourseTitleComparator;
import de.fhe.fhemobile.network.MyTimeTableCallback;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.utils.mytimetable.MyTimeTableUtils;
import de.fhe.fhemobile.utils.Utils;
import de.fhe.fhemobile.views.mytimetable.MyTimeTableDialogView;
import de.fhe.fhemobile.vos.mytimetable.MyTimeTableCourseComponent;
import de.fhe.fhemobile.vos.timetable.TimeTableDayVo;
import de.fhe.fhemobile.vos.timetable.TimeTableEventVo;
import de.fhe.fhemobile.vos.timetable.TimeTableResponse;
import de.fhe.fhemobile.vos.timetable.TimeTableSemesterVo;
import de.fhe.fhemobile.vos.timetable.TimeTableStudyGroupVo;
import de.fhe.fhemobile.vos.timetable.TimeTableStudyProgramVo;
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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MyTimeTableOverviewFragment.
     */
    public static MyTimeTableDialogFragment newInstance() {
        final MyTimeTableDialogFragment fragment = new MyTimeTableDialogFragment();
        final Bundle args = new Bundle();
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
        final Dialog dialog = getDialog();
        if (dialog != null)
        {
            final int width = ViewGroup.LayoutParams.MATCH_PARENT;
            final int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mChosenStudyProgram = null;
        mChosenSemester = null;

	    mCourseListAdapter = new MyTimeTableDialogAdapter(getAppContext());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (MyTimeTableDialogView) inflater.inflate(R.layout.fragment_my_time_table_dialog,
                container, false);

        mView.initializeView(getChildFragmentManager());
        mView.setViewListener(mViewListener);

        mView.setCourseListAdapter(mCourseListAdapter);

        //todo: loading course list from shared preferences risks to display an outdated version,
        // the user has to re-select his study course and semester to update it.
        // This is not intuitive and not how the user predicts MyTimeTableDialog to work.
        // Thus this function should be commented out or replaced by a less risky method.
        //loadSelectionFromSharedPreferences();

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        NetworkHandler.getInstance().fetchTimeTable(mTimeTableResponseCallback);
    }


    /**
     * Processes all {@link TimeTableEventVo} objects to create {@link MyTimeTableCourseComponent} objects
     * that are then added to allCoursesInChosenSemester
     *
     * Method for collecting all events of all study groups from a certain study course
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
                            MyTimeTableCourseComponent alreadyExistingCourse =
                                    getCorrespondingCourseComponentInAllCoursesInChosenSemester(timeTableEvent);


                            //new MyTimeTableCourseComponent Object needed
                            if ( alreadyExistingCourse == null ) {
                                //note: with this constructor "subscribed" is set automatically
                                MyTimeTableCourseComponent courseToAdd = new MyTimeTableCourseComponent(
                                        //mChosenStudyProgram, //currently not used - Nadja 02/2022
                                        //mChosenSemester, //currently not used - Nadja 02/2022
                                        timeTableEvent,
                                        studyGroup);

                                //add new course
                                this.allCoursesInChosenSemester.add(courseToAdd);
                            }
                            //allCoursesInChosenSemester already contains the course the event belongs to
                            else {
                                //if needed, add studyGroup to study group list
                                if (!alreadyExistingCourse.getStudyGroups().contains(studyGroup.getShortTitle())){
                                    alreadyExistingCourse.addStudyGroup(studyGroup);
                                }
                                //if needed, add event to course
                                if (!alreadyExistingCourse.containsEvent(timeTableEvent)){
                                    alreadyExistingCourse.addEvent(timeTableEvent);
                                }
                            }

                        }
                    }
                }
            }
            catch (final Exception e){
                Log.e(TAG, "problem merging new data into allCoursesInChosenSemester",e);
            }
        }
    }

    /**
     * Returns the {@link MyTimeTableCourseComponent} the event belongs to if it already exists in allCoursesInChosenSemester
     * @param event the {@link TimeTableEventVo} to search for
     * @return course component from allCoursesInChosenSemester; null if no corresponding component was found
     */
    private MyTimeTableCourseComponent getCorrespondingCourseComponentInAllCoursesInChosenSemester(
            final TimeTableEventVo event) {

        for(final MyTimeTableCourseComponent courseInChosenSemester : this.allCoursesInChosenSemester) {
            if (courseInChosenSemester.getTitle().equals(MyTimeTableUtils.getCourseComponentName(event))){
                return courseInChosenSemester;
            }
        }
        return null;
    }


    private final MyTimeTableDialogView.IViewListener mViewListener = new MyTimeTableDialogView.IViewListener() {

        @Override
        public void onStudyProgramChosen(final String _StudyProgramId) {
            //reset needed because a new study course had been chosen
            mView.resetSemesterPicker();
            mView.toggleCourseListVisibility(false);

            allCoursesInChosenSemester.clear();
            mCourseListAdapter.setItems(allCoursesInChosenSemester);
            mCourseListAdapter.notifyDataSetChanged();

            mChosenStudyProgram = null;
            mChosenSemester = null;


            boolean studyProgramEmpty = true;

            for (final TimeTableStudyProgramVo studyProgram : mResponse.getStudyPrograms()) {
                if (studyProgram != null && studyProgram.getId() != null && studyProgram.getId().equals(_StudyProgramId)) {

                    mChosenStudyProgram = studyProgram;

                    // Check if study course has any semesters available
                    if (studyProgram.getSemesters() != null) {
                        studyProgramEmpty = false;

                        //set items of semester picker
                        mView.setSemesterItems(studyProgram.getSemesters());
                    }
                    // No terms are available
                    else {
                        studyProgramEmpty = true;
                    }

                    //chosen study course found
                    break;
                }
                else {
                    // No Id is available, chosen study course not found
                    studyProgramEmpty = true;
                }
            }

            if (studyProgramEmpty) {
                mView.toggleSemesterPickerVisibility(false);
                Utils.showToast(R.string.timetable_error);
            }
            else {
                mView.toggleSemesterPickerVisibility(true);
            }

        }


        @Override
        public void onSemesterChosen(String _SemesterId) {
            mView.toggleProgressIndicatorVisibility(true);
            mView.toggleCourseListVisibility(false);
            allCoursesInChosenSemester.clear();
            mCourseListAdapter.setItems(allCoursesInChosenSemester);
            mCourseListAdapter.notifyDataSetChanged();

            mChosenSemester = null;

            for (TimeTableSemesterVo semester : mChosenStudyProgram.getSemesters()) {

                if (semester.getId().equals(_SemesterId)) {

                    //set chosen semester
                    mChosenSemester = semester;

                    //get timetable (all courses/events) for each study group in the chosen semester
                    for (TimeTableStudyGroupVo timeTableStudyGroupVo : mChosenSemester.getStudyGroups()) {

                        //get timetable of the timeTableStudyGroupVo
                        MyTimeTableCallback<ArrayList<TimeTableWeekVo>> callback =
                                new MyTimeTableCallback<ArrayList<TimeTableWeekVo>>(mChosenStudyProgram, mChosenSemester, timeTableStudyGroupVo) {

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

                                                Collections.sort(allCoursesInChosenSemester, new CourseTitleComparator());

                                                mView.toggleCourseListVisibility(true);
                                                mCourseListAdapter.setItems(allCoursesInChosenSemester);
                                                mCourseListAdapter.notifyDataSetChanged();
                                                mView.setCourseListAdapter(mCourseListAdapter);

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

                mView.setStudyProgramItems(mResponse.getStudyPrograms());
                mView.toggleProgressIndicatorVisibility(false);
            }
        }

        @Override
        public void onFailure(Call<TimeTableResponse> call, Throwable t) {
            Log.d(TAG, "failure: request " + call.request().url());
        }
    };


    private MyTimeTableDialogView mView;
    private TimeTableResponse mResponse;
    private TimeTableStudyProgramVo mChosenStudyProgram;
    private TimeTableSemesterVo mChosenSemester;
    //list of courses for the study course and semester currently chosen in MyTimeTableDialog
    private final List<MyTimeTableCourseComponent> allCoursesInChosenSemester = new ArrayList<>();

    private MyTimeTableDialogAdapter mCourseListAdapter;

    /** es gibt zu EINEM Request unterschiedliche Anzahl von Anforderungen und Antworten
     *  wir warten, bis alle Antworten eingetroffen sind.
     */
    private volatile int requestCounter = 0;


}
