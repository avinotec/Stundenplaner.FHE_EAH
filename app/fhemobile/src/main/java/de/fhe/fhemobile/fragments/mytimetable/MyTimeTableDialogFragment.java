package de.fhe.fhemobile.fragments.mytimetable;


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
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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



    private List<FlatDataStructure> getAllEvents(List<TimeTableWeekVo> weekList, FlatDataStructure data) {
        if (weekList != null) {
            try {
                List<FlatDataStructure> dataList= new ArrayList<>();
                for(int weekIndex=0;weekIndex<weekList.size();weekIndex++){
                    List<TimeTableDayVo> dayList = weekList.get(weekIndex).getDays();
                    for(int dayIndex=0;dayIndex<dayList.size();dayIndex++){
                        List<TimeTableEventVo> eventList = dayList.get(dayIndex).getEvents();
                        for(int eventIndex=0;eventIndex<eventList.size();eventIndex++){
                            data
                                    .setEventWeek(weekList.get(weekIndex))
                                    .setEventDay(dayList.get(dayIndex))
                                    .setEvent(eventList.get(eventIndex));
                            Log.d(TAG, data.getCourse().getTitle() + "-->"
                                    + data.getSemester().getTitle() + "-->"
                                    + data.getStudyGroup().getTitle() + "-->"
                                    + data.getEventWeek().getWeekInYear() + "-->"
                                    + data.getEventDay().getDayInWeek() + "-->"
                                    + data.getEvent().getUid());
                            if(!FlatDataStructure.listContainsEvent(completeDataset,data)){
                                dataList.add(data);
                            }
                        }
                    }
                }
                return dataList;

            }
            catch (Exception e){
                Log.e(TAG, "success: Exception beim Zusammenstellen der Datensaetze.",e);
            }
        }
        return new ArrayList();
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

                            for (TermsVo term : courseVo.getTerms()) {
                                for (StudyGroupVo studyGroupVo : term.getStudyGroups()) {
                                    FlatDataStructure data = new FlatDataStructure()
                                            .setCourse(courseVo)
                                            .setSemester(term)
                                            .setStudyGroup(studyGroupVo);
                                    TimeTableCallback<List<TimeTableWeekVo>> callback = new TimeTableCallback<List<TimeTableWeekVo>>(data) {
                                        @Override
                                        public void success(List<TimeTableWeekVo> weekList, Response response) {
                                            super.success(weekList, response);
                                            Log.d(TAG, "success: Request wurde ausgefuehrt: " + response.getUrl() + " Status: " + response.getStatus());
                                            completeDataset.clear();
                                            completeDataset.addAll(getAllEvents(weekList,this.getData()));
                                            TimeTableLessonAdapter timeTableLessonAdapter = new TimeTableLessonAdapter(MyTimeTableDialogFragment.this.getContext(), completeDataset);
                                            mView.setLessonListAdapter(timeTableLessonAdapter);
                                            mView.toggleLessonListVisibility(true);
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            super.failure(error);
                                            Log.d(TAG, "failure: " + error);
                                        }
                                    };

                                    proceedToTimetable(studyGroupVo.getTimeTableId(), callback);
                                }
                            }



                       // mView.setTermsItems(courseVo.getTerms());
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
                //mView.toggleTermsPickerVisibility(true);
                Log.d(TAG, "onTermChosen: Complete Data Array Length: "+completeDataset.size());
//                    for (FlatDataStructure dataset : completeDataset) {
//                        Log.d(TAG, dataset.getCourse().getTitle() + "-->"
//                                + dataset.getSemester().getTitle() + "-->"
//                                + dataset.getStudyGroup().getTitle() + "-->"
//                                + dataset.getEventWeek().getWeekInYear() + "-->"
//                                + dataset.getEventDay().getDayInWeek() + "-->"
//                                + dataset.getEvent().getUid());
//                    }

            }

        }

        @Override
        public void onGroupChosen(String _GroupId) {
          //  mView.toggleGroupsPickerVisibility(true);
            mView.toggleButtonEnabled(false);
           // mView.resetGroupsPicker();

            mChosenTerm = null;

            for (TermsVo termsVo : mChosenCourse.getTerms()) {
                if (termsVo.getId().equals(_GroupId)) {
                    mChosenTerm = termsVo;

                    for (StudyGroupVo studyGroupVo:termsVo.getStudyGroups()){

//                        proceedToTimetable(studyGroupVo.getTimeTableId());
                    }
//                    mView.setStudyGroupItems(termsVo.getStudyGroups());
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

    private Callback<TimeTableResponse> mTimeTableResponseCallback = new Callback<TimeTableResponse>() {
        @Override
        public void success(TimeTableResponse t, Response response) {
            Log.d(TAG, "success: Request wurde ausgefuehrt: "+response.getUrl()+" Status: "+response.getStatus());
            // MS: Bei den News sind die news/0 kaputt
            if ( t != null ) {
                mResponse = t;
                mView.setStudyCourseItems(t.getStudyCourses());

            }
        }

        @Override
        public void failure(RetrofitError error) {
            Log.d(TAG, "failure: Request wurde ausgefuehrt: "+error.getUrl());

        }
    };

    private AddLessonView     mView;

    private TimeTableResponse mResponse;
    private StudyCourseVo     mChosenCourse;
    private TermsVo           mChosenTerm;
    private String            mChosenTimetableId;
    private List<FlatDataStructure> completeDataset;
}
