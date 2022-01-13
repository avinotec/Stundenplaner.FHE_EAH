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
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.network.TimeTableCallback;
import de.fhe.fhemobile.utils.Utils;
import de.fhe.fhemobile.views.mytimetable.MyTimeTableDialogView;
import de.fhe.fhemobile.vos.timetable.FlatDataStructure;
import de.fhe.fhemobile.vos.timetable.SemesterVo;
import de.fhe.fhemobile.vos.timetable.StudyCourseVo;
import de.fhe.fhemobile.vos.timetable.StudyGroupVo;
import de.fhe.fhemobile.vos.timetable.TimeTableDayVo;
import de.fhe.fhemobile.vos.timetable.TimeTableEventVo;
import de.fhe.fhemobile.vos.timetable.TimeTableResponse;
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

        loadSelectionFromSharedPreferences();

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        NetworkHandler.getInstance().fetchTimeTable(mTimeTableResponseCallback);
    }

    private void proceedToTimetable(final String _TimeTableId, final TimeTableCallback callback) {
        NetworkHandler.getInstance().fetchTimeTableEvents(_TimeTableId, callback);

    }
    /**
     * @param timeTableWeeks list of TimeTableWeeks of the remaining for the chosen study group
     * @param allCoursesInChosenSemester ist die Liste, die am Ende alle Daten der verschiedenen Requests beinhaltet.
     * @param data ist der neue Datensatz, der in die Liste eingepflegt werden soll.
     **/
    private static void getAllEvents(final List<TimeTableWeekVo> timeTableWeeks,
                                     final List<FlatDataStructure> allCoursesInChosenSemester,
                                     final FlatDataStructure data) {

        if (timeTableWeeks != null) {
            try {
                for(int weekIndex = 0; weekIndex < timeTableWeeks.size(); weekIndex++){

                    List<TimeTableDayVo> dayList = timeTableWeeks.get(weekIndex).getDays();

                    for(int dayIndex = 0; dayIndex<dayList.size(); dayIndex++){
                        List<TimeTableEventVo> eventList = dayList.get(dayIndex).getEvents();

                        for(int eventIndex = 0; eventIndex < eventList.size(); eventIndex++){
                            for(FlatDataStructure addedEvent : getSubscribedCourses()){
                                if (addedEvent.getEvent().getUid()
                                        .equals(eventList.get(eventIndex).getUid())){
                                    data.setAdded(true);
                                    break;
                                }
                            }
                            //durchsuche die komplette Liste nach der EventUID, des momentan hinzuzufügenden Events
                        	FlatDataStructure exists = null;
                        	for ( FlatDataStructure savedEvent : allCoursesInChosenSemester) {
//		                        Log.d(TAG, "EventUID1: "+savedEvent.getEvent().getUid()+" EventUID2: "+eventList.get(eventIndex).getUid()+" setTitle: "+ savedEvent.getStudyGroup().getTitle()+" result: "+savedEvent.getEvent().getUid().equals(eventList.get(eventIndex).getUid()));
                        		if (savedEvent.getEvent().getUid().equals(eventList.get(eventIndex).getUid())){
                        			exists = savedEvent;
                        			break;
		                        }
	                        }
                            // Kommt die UID noch nicht in der Liste vor, hinzufügen
                        	if ( exists == null ) {
		                        FlatDataStructure datacopy = data.copy();
		                        datacopy
				                        .setEventWeek(timeTableWeeks.get(weekIndex))
				                        .setEventDay(dayList.get(dayIndex))
				                        .setEvent(eventList.get(eventIndex));
		                        datacopy.getSets().add(datacopy.getStudyGroup().getTitle().split("\\.")[1]);
		                        boolean found = false;
		                        for(FlatDataStructure selectedItem: getSubscribedCourses()){
		                            if(datacopy.getEvent().getUid()
                                            .equals(selectedItem.getEvent().getUid())){
		                                found = true;
		                                break;
                                    }
                                }

                                datacopy.setAdded( found ) ;

		                        allCoursesInChosenSemester.add(datacopy);
	                        }
                            //Stattdessen füge bei dem existierenden Eintrag das Set des neuen Events hinzu.
                        	else{
                                //Kommt die UID schon in der Liste vor, braucht der Event nicht hinzugefügt werden, da es ein Duplikat wäre.
                        	    exists.getSets().add(data.getStudyGroup().getTitle().split("\\.")[1]);
	                        }
                        }
                    }
                }
            }
            catch (Exception e){
                Log.e(TAG, "success: Exception beim Zusammenstellen der Datensaetze.",e);
            }
        }
    }


    /**
     * Setzt die beim letzten Mal ausgewählten Werte und die letzten Suchergebnisse.
     */
    private void loadSelectionFromSharedPreferences(){
        final Gson gson = new Gson();

        SharedPreferences sharedPreferences = this.getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(sharedPreferences.contains(PREFS_CHOSEN_STUDY_COURSE)){
            final String chosenCourseJson = sharedPreferences.getString(PREFS_CHOSEN_STUDY_COURSE,"");
            final StudyCourseVo chosenCourse = gson.fromJson(chosenCourseJson,StudyCourseVo.class);
            mChosenStudyCourse = chosenCourse;
            mView.setSemesterItems(mChosenStudyCourse.getSemesters());
            mView.setSelectedGroupText(chosenCourse.getTitle());
        }

        if(sharedPreferences.contains(PREFS_CHOSEN_SEMESTER)) {
            final String chosenSemesterJson = sharedPreferences.getString(PREFS_CHOSEN_SEMESTER, "");
            final SemesterVo chosenSemester = gson.fromJson(chosenSemesterJson, SemesterVo.class);
            mChosenSemester = chosenSemester;
            mView.setSelectedSemesterText(chosenSemester.getTitle());
            mView.toggleSemesterPickerVisibility(true);
            mView.setSemesterPickerEnabled(true);


            //load last request result for the last chosen study course and semester
            if (sharedPreferences.contains(PREFS_ALL_COURSES_OF_CHOSEN_STUDYCOURSE_AND_SEMESTER)) {
                final String resultJson = correctUmlauts(sharedPreferences.getString(PREFS_ALL_COURSES_OF_CHOSEN_STUDYCOURSE_AND_SEMESTER, ""));
                final FlatDataStructure[] result = gson.fromJson(resultJson, FlatDataStructure[].class);

                //for each course in result check if the user has added it to his/her schedule
                for(FlatDataStructure loadedElement : result){
                    boolean found = false;
                    for(FlatDataStructure selectedItem : getSubscribedCourses()){
                        if(loadedElement.getEvent().getUid().equals(selectedItem.getEvent().getUid())){
                            found = true;
                            break;
                        }
                    }
                    loadedElement.setAdded( found );

                }
                //set all courses of the currently chosen study course and semester (not the variable for the subscribed courses)
                coursesOfChosenSemester = new ArrayList<FlatDataStructure>(Arrays.asList(result));

                mCourseListAdapter.setChosenCourseList(coursesOfChosenSemester);
                mView.setCourseListAdapter(mCourseListAdapter);
                mView.toggleCourseListVisibility(true);
                mCourseListAdapter.notifyDataSetChanged();
            }
        }
    }


    private final MyTimeTableDialogView.IViewListener mViewListener = new MyTimeTableDialogView.IViewListener() {

        @Override
        public void onStudyCourseChosen(final String _StudCourseId) {
            //reset needed because a new study course had been chosen
            mView.resetSemesterPicker();
            mView.toggleCourseListVisibility(false);

            coursesOfChosenSemester.clear();
            mCourseListAdapter.setChosenCourseList(coursesOfChosenSemester);
            mCourseListAdapter.notifyDataSetChanged();

            mChosenStudyCourse = null;
            mChosenSemester = null;


            boolean studyCourseEmpty = true;

            for (StudyCourseVo studyCourse : mResponse.getStudyCourses()) {
                if (studyCourse != null && studyCourse.getId() != null && studyCourse.getId().equals(_StudCourseId)) {

                    mChosenStudyCourse = studyCourse;

                    // Check if study course has any semesters available
                    if (studyCourse.getSemesters() != null) {
                        studyCourseEmpty = false;
                        //save chosen study course to shared preferences
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

        /** es gibt zu EINEM Request unterschiedliche Anzahl von Anforderungen und Antworten
         *  wir warten, bis alle Antworten eingetroffen sind.
         */
        private volatile int requestCounter = 0;

        /**
         *
         * @param _SemesterId
         */
        @Override
        public void onSemesterChosen(String _SemesterId) {
            mView.toggleCourseListVisibility(false);
            coursesOfChosenSemester.clear();
            mCourseListAdapter.setChosenCourseList(coursesOfChosenSemester);
            mCourseListAdapter.notifyDataSetChanged();

            mChosenSemester = null;

            for (SemesterVo semester : mChosenStudyCourse.getSemesters()) {

                if (semester.getId().equals(_SemesterId)) {

                    //set chosen semester
                    mChosenSemester = semester;
                    //save chosen semester to shared preferences
                    final Gson gson = new Gson();
                    final String chosenSemesterJson = correctUmlauts(gson.toJson(mChosenSemester));
                    editor.putString(PREFS_CHOSEN_SEMESTER, chosenSemesterJson);
                    editor.commit();

                    //get timetable (all courses/events) for each study group in the chosen semester
                    for (StudyGroupVo studyGroupVo : mChosenSemester.getStudyGroups()) {
                        FlatDataStructure data = new FlatDataStructure()
                                .setStudyCourse(mChosenStudyCourse)
                                .setSemester(mChosenSemester)
                                .setStudyGroup(studyGroupVo);

                        //get timetable of the studyGroupVo
                        TimeTableCallback<List<TimeTableWeekVo>> callback =
                                new TimeTableCallback<List<TimeTableWeekVo>>(data) {

                            @Override
                            public void onResponse(Call<List<TimeTableWeekVo>> call,
                                                   Response<List<TimeTableWeekVo>> response) {
                                super.onResponse(call, response);

                                if(response.code() >= 200) {
                                    //week-wise timetables of the current studyGroup in the for-loop
                                    List<TimeTableWeekVo> timeTableWeek = response.body();

//                                    Log.d(TAG, "success: Request wurde ausgefuehrt: " + response.raw().request().url() + " Status: " + response.code());
                                    //Gemergte liste aller zurückgekehrten Requests. Die Liste wächst mit jedem Request.
                                    //Hier (im success) haben wir neue Daten bekommen.

                                    getAllEvents(timeTableWeek, coursesOfChosenSemester, this.getData());
//                                    Log.d(TAG, "success: length"+courseEvents.size());


                                    // es gibt zu jedem Request unterschiedliche Anzahl von Anforderungen und Antworten
                                    // wir warten, bis alle Antworten eingetroffen sind.
                                    //Wir sortieren diesen letzten Stand der Liste
                                    //Wichtig: --requestCounter nicht requestCounter--! Hier muss erst decrementiert werden und dann der vergleich stattfinden.
                                    if (--requestCounter <= 0) {

                                        try {
                                            Collections.sort(coursesOfChosenSemester,
                                                    new CourseTitleStudyGroupTitleComparator());
                                        } catch ( final RuntimeException e ) {
                                            Log.e(TAG, "Fehler beim Sortieren", e); //$NON-NLS
                                        }

/* TODO: Performance, fühlt sich an, als ob es immer ausgeführt würde....
                                        //Alle Duplikate rauswerfen, Sprich, Einträge, die gleich sind
                                        int nCourseLength = courseEvents.size();
                                        // we need at least 2 elements, to find duplicates and throw away the duplicate
                                        if ( nCourseLength > 2 ) {
                                            // we have to check per iteration, if we reached the end, because we delete
                                            // dynamically the list and shorten it
                                            for (int j = 0 + 1; j < courseEvents.size(); j++) {
                                                final FlatDataStructure course0 = courseEvents.get( j-1 );
                                                final FlatDataStructure course1 = courseEvents.get( j );

                                                // wenn es gleich ist, dann entfernen wir es, damit entfernen wir Duplikate
                                                if ( CourseTitleStudyGroupTitleComparator.compareStatic( course0, course1 ) == 0 ) {
                                                    courseEvents.remove(j);
                                                    j--;
                                                }
                                            }
                                        }
*/

                                        mView.toggleCourseListVisibility(true);
                                        mCourseListAdapter.setChosenCourseList(coursesOfChosenSemester);
                                        mCourseListAdapter.notifyDataSetChanged();
                                        mView.setCourseListAdapter(mCourseListAdapter);

                                        final Gson gson = new Gson();
                                        final String chosenSemesterJson = correctUmlauts(
                                                gson.toJson(coursesOfChosenSemester));
                                        editor.putString(PREFS_ALL_COURSES_OF_CHOSEN_STUDYCOURSE_AND_SEMESTER, chosenSemesterJson);
                                        editor.commit();

                                    }
                                }

                            }

                            @Override
                            public void onFailure(Call<List<TimeTableWeekVo>> call, Throwable t) {
                                super.onFailure(call, t);
//                                Log.d(TAG, "failure: " + t);
                            }
                        };

                        proceedToTimetable(studyGroupVo.getTimeTableId(), callback);
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
            Log.d(TAG, "success: request  " + response.raw().request().url()
                    + ", status: "+response.code());
        }

        @Override
        public void onFailure(Call<TimeTableResponse> call, Throwable t) {
            Log.d(TAG, "failure: request " + call.request().url());
        }
    };

    private SharedPreferences.Editor editor;
    private MyTimeTableDialogView mView;
    private TimeTableResponse mResponse;
    private StudyCourseVo mChosenStudyCourse;
    private SemesterVo mChosenSemester;
    //list of courses for the study course and semester currently chosen in MyTimeTableDialog
    private List<FlatDataStructure> coursesOfChosenSemester = new ArrayList<>();

    private MyTimeTableDialogAdapter mCourseListAdapter;
}
