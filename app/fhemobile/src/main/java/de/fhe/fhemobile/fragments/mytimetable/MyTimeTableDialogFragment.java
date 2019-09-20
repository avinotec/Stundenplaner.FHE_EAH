package de.fhe.fhemobile.fragments.mytimetable;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
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
import de.fhe.fhemobile.adapters.timetable.TimeTableLessonAdapter;
import de.fhe.fhemobile.comparator.LessonTitle_StudyGroupTitle_Comparator;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.network.TimeTableCallback;
import de.fhe.fhemobile.utils.Utils;
import de.fhe.fhemobile.views.timetable.AddLessonView;
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

        if (getArguments() != null) {

        }
        completeDataset = Collections.synchronizedList(new ArrayList<FlatDataStructure>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (AddLessonView) inflater.inflate(R.layout.add_time_table, container, false);
        mView.setViewListener(mViewListener);
        mView.initializeView(getChildFragmentManager());

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        NetworkHandler.getInstance().fetchTimeTable(mTimeTableResponseCallback);
    }

    private void proceedToTimetable(String _TimeTableId, TimeTableCallback callback) {
        NetworkHandler.getInstance().fetchTimeTableEvents(_TimeTableId,callback);

    }

//weeklist ist der Datensatz, der beim Request erhalten wird (alle events eines Sets)
//dataList ist die Liste, die am Ende alle Daten der verschiedenen Requests beinhaltet.
//data ist der neue Datensatz, der in die Liste eingepflegt werden soll.

    private List<FlatDataStructure> getAllEvents(List<TimeTableWeekVo> weekList,List<FlatDataStructure> dataList ,FlatDataStructure data) {
        if (weekList != null) {
            try {

                for(int weekIndex=0;weekIndex<weekList.size();weekIndex++){

                    List<TimeTableDayVo> dayList = weekList.get(weekIndex).getDays();
                    for(int dayIndex=0;dayIndex<dayList.size();dayIndex++){
                        List<TimeTableEventVo> eventList = dayList.get(dayIndex).getEvents();
                        for(int eventIndex=0;eventIndex<eventList.size();eventIndex++){
                            FlatDataStructure datacopy = data.copy();
                            datacopy
                                    .setEventWeek(weekList.get(weekIndex))
                                    .setEventDay(dayList.get(dayIndex))
                                    .setEvent(eventList.get(eventIndex));

                            dataList.add(datacopy);
                        }
                    }
                }
                return dataList;
            }
            catch (Exception e){
                Log.e(TAG, "success: Exception beim Zusammenstellen der Datensaetze.",e);
            }
        }
        return dataList;
    }



    private AddLessonView.IViewListener mViewListener = new AddLessonView.IViewListener() {
        @Override
        public void onTermChosen(String _TermId) {
            mView.toggleGroupsPickerVisibility(false);
            mView.toggleButtonEnabled(false);
            mView.resetTermsPicker();
            mView.resetGroupsPicker();

            mChosenCourse = null;
            mChosenTerm   = null;

            boolean errorOccurred = false;

            for (StudyCourseVo courseVo : mResponse.getStudyCourses()) {
                if (courseVo.getId() != null && courseVo.getId().equals(_TermId)) {
                    mChosenCourse = courseVo;

                    // Check if course has any terms available
                    if (courseVo.getTerms() != null) {
                        errorOccurred = false;
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
                mView.toggleTermsPickerVisibility(true);
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
            mView.toggleButtonEnabled(false);
           // mView.resetGroupsPicker();

            mChosenTerm = null;


            final List<FlatDataStructure> dataList= new ArrayList<>();
            for (TermsVo termsVo : mChosenCourse.getTerms()) {
                if (termsVo.getId().equals(_GroupId)) {

                    mChosenTerm = termsVo;

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

                                    Log.d(TAG, "success: Request wurde ausgefuehrt: " + response.raw().request().url() + " Status: " + response.code());
                                    //Gemergte liste aller zurückgekehrten Requests. Die Liste wächst mit jedem Request.
                                    //Hier (im success) haben wir neue Daten bekommen.

//TODO: überprüfen ob courseEvents nötig ist
                                    List<FlatDataStructure> courseEvents = getAllEvents(weekList, dataList, this.getData());
                                    Log.d(TAG, "success: length"+courseEvents.size());
                                    //Wir sortieren diesen letzten Stand der Liste
                                    requestCounter--;
                                    if(requestCounter==0) {
                                        Collections.sort(courseEvents, new LessonTitle_StudyGroupTitle_Comparator());
                                        TimeTableLessonAdapter timeTableLessonAdapter = new TimeTableLessonAdapter(MyTimeTableDialogFragment.this.getContext(), courseEvents);
                                        mView.setLessonListAdapter(timeTableLessonAdapter);
                                        mView.toggleLessonListVisibility(true);
                                        timeTableLessonAdapter.notifyDataSetChanged();
                                    }
                                }

                            }

                            @Override
                            public void onFailure(Call<List<TimeTableWeekVo>> call, Throwable t) {
                                super.onFailure(call, t);
                                Log.d(TAG, "failure: " + t);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
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

    private Callback<TimeTableResponse> mTimeTableResponseCallback = new Callback<TimeTableResponse>() {
        @Override
        public void onResponse(Call<TimeTableResponse> call, Response<TimeTableResponse> response) {
            if ( response.body() != null ) {
                mResponse = response.body();
                for (StudyCourseVo course : response.body().getStudyCourses()){
                    Log.d(TAG, "onResponse: "+course.getTitle());
                }


                mView.setStudyCourseItems(response.body().getStudyCourses());

            }
            Log.d(TAG, "success: Request wurde ausgefuehrt: "+response.raw().request().url()+" Status: "+response.code());
            // MS: Bei den News sind die news/0 kaputt
        }

        @Override
        public void onFailure(Call<TimeTableResponse> call, Throwable t) {
            Log.d(TAG, "failure: Request wurde ausgefuehrt: "+call.request().url());
        }
    };

    private AddLessonView     mView;

    private TimeTableResponse mResponse;
    private StudyCourseVo     mChosenCourse;
    private TermsVo           mChosenTerm;
    private String            mChosenTimetableId;
    private List<FlatDataStructure> completeDataset;
}
