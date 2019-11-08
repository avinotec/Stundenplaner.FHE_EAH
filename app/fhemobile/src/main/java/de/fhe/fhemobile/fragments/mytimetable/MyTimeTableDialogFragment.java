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
import de.fhe.fhemobile.adapters.timetable.TimeTableLessonAdapter;
import de.fhe.fhemobile.comparator.LessonTitle_StudyGroupTitle_Comparator;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.network.TimeTableCallback;
import de.fhe.fhemobile.utils.Utils;
import de.fhe.fhemobile.views.timetable.AddLessonView;
import de.fhe.fhemobile.views.timetable.MyTimeTableView;
import de.fhe.fhemobile.vos.timetable.FlatDataStructure;
import de.fhe.fhemobile.vos.timetable.StudyCourseVo;
import de.fhe.fhemobile.vos.timetable.StudyGroupVo;
import de.fhe.fhemobile.vos.timetable.TermsVo;
import de.fhe.fhemobile.vos.timetable.TimeTableDayVo;
import de.fhe.fhemobile.vos.timetable.TimeTableEventVo;
import de.fhe.fhemobile.vos.timetable.TimeTableResponse;
import de.fhe.fhemobile.vos.timetable.TimeTableWeekVo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyTimeTableDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyTimeTableDialogFragment extends DialogFragment {


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TimeTableFragment.
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

        mChosenCourse       = null;
        mChosenTerm         = null;
        mChosenTimetableId  = null;

//        if (getArguments() != null) {
//
//        }
        completeDataset = Collections.synchronizedList(new ArrayList<FlatDataStructure>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (AddLessonView) inflater.inflate(R.layout.add_time_table, container, false);
        mView.setViewListener(mViewListener);

        mView.initializeView(getChildFragmentManager());
        sharedPreferences =this.getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        initSelectionSite();

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        NetworkHandler.getInstance().fetchTimeTable(mTimeTableResponseCallback);
    }

    private void proceedToTimetable(final String _TimeTableId, final TimeTableCallback callback) {
        NetworkHandler.getInstance().fetchTimeTableEvents(_TimeTableId,callback);

    }
    /**
     * @param weekList ist der Datensatz, der beim Request erhalten wird (alle events eines Sets)
     * @param dataList ist die Liste, die am Ende alle Daten der verschiedenen Requests beinhaltet.
     * @param data ist der neue Datensatz, der in die Liste eingepflegt werden soll.
     **/
    private static void getAllEvents(final List<TimeTableWeekVo> weekList, final List<FlatDataStructure> dataList , final FlatDataStructure data) {
        if (weekList != null) {
            try {

                for(int weekIndex=0;weekIndex<weekList.size();weekIndex++){

                    List<TimeTableDayVo> dayList = weekList.get(weekIndex).getDays();
                    for(int dayIndex=0;dayIndex<dayList.size();dayIndex++){
                        List<TimeTableEventVo> eventList = dayList.get(dayIndex).getEvents();
                        for(int eventIndex=0;eventIndex<eventList.size();eventIndex++){
                            for(FlatDataStructure addedEvent:MyTimeTableView.getLessons()){
                                if (addedEvent.getEvent().getUid().equals(eventList.get(eventIndex).getUid())){
                                    data.setAdded(true);
                                    break;
                                }
                            }
                            //durchsuche die komplette Liste nach der EventUID, des momentan hinzuzufügenden Event.
                        	FlatDataStructure exists = null;
                        	for(FlatDataStructure savedEvent:dataList){
//		                        Log.d(TAG, "EventUID1: "+savedEvent.getEvent().getUid()+" EventUID2: "+eventList.get(eventIndex).getUid()+" setTitle: "+ savedEvent.getStudyGroup().getTitle()+" result: "+savedEvent.getEvent().getUid().equals(eventList.get(eventIndex).getUid()));
                        		if (savedEvent.getEvent().getUid().equals(eventList.get(eventIndex).getUid())){
                        			exists=savedEvent;
                        			break;
		                        }
	                        }
                            //Kommt die UID schon in der Liste vor, braucht der Event nicht hinzugefügt werden, da es ein Duplikat wäre.
                        	if(exists==null){
		                        FlatDataStructure datacopy = data.copy();
		                        datacopy
				                        .setEventWeek(weekList.get(weekIndex))
				                        .setEventDay(dayList.get(dayIndex))
				                        .setEvent(eventList.get(eventIndex));
		                        datacopy.getSets().add(datacopy.getStudyGroup().getTitle());

		                        dataList.add(datacopy);
	                        }
                            //Stattdessen füge bei dem existierenden Eintrag das Set des neuen Events hinzu.
                        	else{
                        	    exists.getSets().add(data.getStudyGroup().getTitle());
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



    //Setzt die beim letzten mal ausgewählten Werte und die letzen Suchergebnisse.
    public void initSelectionSite(){
        Gson gson= new Gson();
        if(sharedPreferences.contains("_ChosenCourse")){
            String chosenCourseJson=sharedPreferences.getString("_ChosenCourse","");
            StudyCourseVo chosenCourse = gson.fromJson(chosenCourseJson,StudyCourseVo.class);
            mChosenCourse=chosenCourse;
            mView.setTermsItems(mChosenCourse.getTerms());
            mView.setSelectedGroupText(chosenCourse.getTitle());
        }

        if(sharedPreferences.contains("_ChosenTerm")) {
            String chosenTermJson = sharedPreferences.getString("_ChosenTerm", "");
            TermsVo chosenTerm = gson.fromJson(chosenTermJson, TermsVo.class);
            mChosenTerm = chosenTerm;
            mView.setSelectedTermText(chosenTerm.getTitle());
            mView.toggleTermsPickerVisibility(true);
            mView.setmGroupPickerEnabled(true);
            mView.setmTermsPickerEnabled(true);


            if (sharedPreferences.contains("_Result")) {
                String resultJson = sharedPreferences.getString("_Result", "");
                FlatDataStructure[] result = gson.fromJson(resultJson, FlatDataStructure[].class);
                MyTimeTableView.setCompleteLessons(new ArrayList<FlatDataStructure>(Arrays.asList(result)));
                final TimeTableLessonAdapter timeTableLessonAdapter = new TimeTableLessonAdapter(MyTimeTableDialogFragment.this.getContext());
                mView.setLessonListAdapter(timeTableLessonAdapter);
                mView.toggleLessonListVisibility(true);
                timeTableLessonAdapter.notifyDataSetChanged();
            }
        }

    }

    private final AddLessonView.IViewListener mViewListener = new AddLessonView.IViewListener() {


        @Override
        public void onTermChosen(String _TermId) {

            mView.toggleGroupsPickerVisibility(false);
            mView.resetTermsPicker();
            mView.resetGroupsPicker();
            mView.toggleLessonListVisibility(false);
            MyTimeTableView.getCompleteLessons().clear();

            mChosenCourse = null;
            mChosenTerm   = null;

            boolean errorOccurred = false;

            for (StudyCourseVo courseVo : mResponse.getStudyCourses()) {
                if (courseVo.getId() != null && courseVo.getId().equals(_TermId)) {
                    mChosenCourse = courseVo;
                    Gson gson = new Gson();
                    String chosenCourseJson=gson.toJson(courseVo);



                    // Check if course has any terms available
                    if (courseVo.getTerms() != null) {
                        errorOccurred = false;
                        editor.putString("_ChosenCourse",chosenCourseJson);
                        editor.commit();
                        mView.setTermsItems(courseVo.getTerms());
                    }
                    else {
                        // No terms are available
                        errorOccurred = true;
                    }
                    break;
                }
                else {
                    // No Id is available
                    errorOccurred = true;
                }
            }

            if (errorOccurred) {
                mView.toggleTermsPickerVisibility(false);
                Utils.showToast(R.string.timetable_error);
            }
            else {
                mView.toggleTermsPickerVisibility(true);
            }

        }

        public volatile int requestCounter=0;
        @Override
        public void onGroupChosen(String _GroupId) {
            mView.toggleGroupsPickerVisibility(false);
            //mView.toggleButtonEnabled(false);
            mView.toggleLessonListVisibility(false);
            MyTimeTableView.getCompleteLessons().clear();

            // mView.resetGroupsPicker();

            mChosenTerm = null;

            for (TermsVo termsVo : mChosenCourse.getTerms()) {
                if (termsVo.getId().equals(_GroupId)) {

                    mChosenTerm = termsVo;
                    final Gson gson = new Gson();
                    final String chosenTermJson = gson.toJson(mChosenTerm);
                    editor.putString("_ChosenTerm",chosenTermJson);
                    editor.commit();

                    for (StudyGroupVo studyGroupVo : mChosenTerm.getStudyGroups()) {
                        FlatDataStructure data = new FlatDataStructure()
                                .setCourse(mChosenCourse)
                                .setSemester(mChosenTerm)
                                .setStudyGroup(studyGroupVo);


                        TimeTableCallback<List<TimeTableWeekVo>> callback = new TimeTableCallback<List<TimeTableWeekVo>>(data) {
                            @Override
                            public void onResponse(Call<List<TimeTableWeekVo>> call, Response<List<TimeTableWeekVo>> response) {

                                super.onResponse(call, response);
                                if(response.code()>=200) {
                                    List<TimeTableWeekVo> weekList=response.body();

//                                    Log.d(TAG, "success: Request wurde ausgefuehrt: " + response.raw().request().url() + " Status: " + response.code());
                                    //Gemergte liste aller zurückgekehrten Requests. Die Liste wächst mit jedem Request.
                                    //Hier (im success) haben wir neue Daten bekommen.

                                    getAllEvents(weekList, MyTimeTableView.getCompleteLessons(), this.getData());
//                                    Log.d(TAG, "success: length"+courseEvents.size());
                                    //Wir sortieren diesen letzten Stand der Liste
                                    //Wichtig: --requestCounter nicht requestCounter--! Hier muss erst decrementiert werden und dann der vergleich stattfinden.
                                    if( (--requestCounter) <= 0) {


                                        try {
                                            Collections.sort(MyTimeTableView.getCompleteLessons(), new LessonTitle_StudyGroupTitle_Comparator());
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
                                                if ( LessonTitle_StudyGroupTitle_Comparator.compareStatic( course0, course1 ) == 0 ) {
                                                    courseEvents.remove(j);
                                                    j--;
                                                }
                                            }
                                        }
*/

                                        final TimeTableLessonAdapter timeTableLessonAdapter = new TimeTableLessonAdapter(MyTimeTableDialogFragment.this.getContext());
                                        mView.setLessonListAdapter(timeTableLessonAdapter);
                                        mView.toggleLessonListVisibility(true);
                                        timeTableLessonAdapter.notifyDataSetChanged();
                                        Gson gson = new Gson();
                                        String chosenTermJson = gson.toJson(MyTimeTableView.getCompleteLessons());
                                        editor.putString("_Result",chosenTermJson);
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

                        synchronized (this){proceedToTimetable(studyGroupVo.getTimeTableId(), callback);}
                        requestCounter++;
                    }
                }
            }
        }

        @Override
        public void onTimeTableChosen(String _TimeTableId) {
            // proceedToTimetable(_TimeTableId);
        }

        @Override
        public void onSearchClicked() {
            if (mChosenTimetableId != null) {


                mChosenTimetableId = null;
                mChosenCourse = null;
                mChosenTerm = null;
            }
            else {
                Utils.showToast(R.string.timetable_error_incomplete);
            }
        }
    };
/*
    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        Log.d(TAG, "onDestroyView");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        Log.d(TAG, "onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        Log.d(TAG, "onDetach");
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(TAG,"onCancel");
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

    }
*/
    private final Callback<TimeTableResponse> mTimeTableResponseCallback = new Callback<TimeTableResponse>() {
        @Override
        public void onResponse(Call<TimeTableResponse> call, Response<TimeTableResponse> response) {
            if ( response.body() != null ) {

                //store the response, to work on later
                mResponse = response.body();
//                for (StudyCourseVo course : response.body().getStudyCourses()){
//                    Log.d(TAG, "onResponse: "+course.getTitle());
//                }

                mView.setStudyCourseItems(mResponse.getStudyCourses());
            }
            Log.d(TAG, "success: Request wurde ausgefuehrt: "+response.raw().request().url()+" Status: "+response.code());
            // MS: Bei den News sind die news/0 kaputt
        }

        @Override
        public void onFailure(Call<TimeTableResponse> call, Throwable t) {
            Log.d(TAG, "failure: Request wurde ausgefuehrt: "+call.request().url());
        }
    };

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private AddLessonView     mView;
    private TimeTableResponse mResponse;
    private StudyCourseVo     mChosenCourse;
    private TermsVo           mChosenTerm;
    private String            mChosenTimetableId;
    private List<FlatDataStructure> completeDataset;
}
