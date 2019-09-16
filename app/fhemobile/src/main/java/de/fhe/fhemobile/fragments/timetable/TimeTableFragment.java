package de.fhe.fhemobile.fragments.timetable;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.utils.TimeTableSettings;
import de.fhe.fhemobile.utils.Utils;
import de.fhe.fhemobile.views.timetable.TimeTableView;
import de.fhe.fhemobile.vos.timetable.StudyCourseVo;
import de.fhe.fhemobile.vos.timetable.TermsVo;
import de.fhe.fhemobile.vos.timetable.TimeTableResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimeTableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeTableFragment extends FeatureFragment {


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TimeTableFragment.
     */
    public static TimeTableFragment newInstance() {
        TimeTableFragment fragment = new TimeTableFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public TimeTableFragment() {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (TimeTableView) inflater.inflate(R.layout.fragment_time_table, container, false);
        mView.setViewListener(mViewListener);
        mView.initializeView(getChildFragmentManager());

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        NetworkHandler.getInstance().fetchTimeTable(mTimeTableResponseCallback);
    }

    private void proceedToTimetable(String _TimeTableId) {
        ((MainActivity) getActivity()).changeFragment(TimeTableEventsFragment.newInstance(_TimeTableId), true);
    }

    private TimeTableView.IViewListener mViewListener = new TimeTableView.IViewListener() {
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
                mView.toggleTermsPickerVisibility(false);
                Utils.showToast(R.string.timetable_error);
            }
            else {
                mView.toggleTermsPickerVisibility(true);
            }

        }

        @Override
        public void onGroupChosen(String _GroupId) {
            mView.toggleGroupsPickerVisibility(true);
            mView.toggleButtonEnabled(false);
            mView.resetGroupsPicker();

            mChosenTerm = null;

            for (TermsVo termsVo : mChosenCourse.getTerms()) {
                if (termsVo.getId().equals(_GroupId)) {
                    mChosenTerm = termsVo;
                    mView.setStudyGroupItems(termsVo.getStudyGroups());
                }
            }
        }

        @Override
        public void onTimeTableChosen(String _TimeTableId) {
            mView.toggleButtonEnabled(true);
            mChosenTimetableId = _TimeTableId;
        }

        @Override
        public void onSearchClicked() {
            if (mChosenTimetableId != null) {
                if (mView.isRememberActivated()) {
                    TimeTableSettings.saveTimeTableSelection(mChosenTimetableId);
                }
                proceedToTimetable(mChosenTimetableId);

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
        public void onResponse(Call<TimeTableResponse> call, Response<TimeTableResponse> response) {
            if ( response.body() != null ) {
                mResponse = response.body();
                mView.setStudyCourseItems(response.body().getStudyCourses());
            }
        }

        @Override
        public void onFailure(Call<TimeTableResponse> call, Throwable t) {

        }
    };

    private TimeTableView     mView;

    private TimeTableResponse mResponse;
    private StudyCourseVo     mChosenCourse;
    private TermsVo           mChosenTerm;
    private String            mChosenTimetableId;
}
