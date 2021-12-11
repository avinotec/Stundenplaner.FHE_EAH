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
import de.fhe.fhemobile.vos.timetable.SemesterVo;
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
        mChosenSemester = null;
        mChosenTimetableId  = null;

//        if (getArguments() != null) {
//
//        }









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

    private final TimeTableView.IViewListener mViewListener = new TimeTableView.IViewListener() {
        @Override
        public void onStudyCourseChosen(String _SemesterId) {
            mView.toggleGroupsPickerVisibility(false);
            mView.toggleButtonEnabled(false);
            mView.resetSemesterPicker();
            mView.resetGroupsPicker();

            mChosenCourse = null;
            mChosenSemester = null;

            boolean errorOccurred = false;

            for (StudyCourseVo courseVo : mResponse.getStudyCourses()) {
                if (courseVo.getId() != null && courseVo.getId().equals(_SemesterId)) {
                    mChosenCourse = courseVo;

                    // Check if course has any terms available
                    if (courseVo.getSemesters() != null) {
                        errorOccurred = false;
                        mView.setSemesterItems(courseVo.getSemesters());
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
                mView.toggleSemesterPickerVisibility(false);
                Utils.showToast(R.string.timetable_error);
            }
            else {
                mView.toggleSemesterPickerVisibility(true);
            }

        }

        @Override
        public void onSemesterChosen(String _GroupId) {
            mView.toggleGroupsPickerVisibility(true);
            mView.toggleButtonEnabled(false);
            mView.resetGroupsPicker();

            mChosenSemester = null;

            for (SemesterVo semesterVo : mChosenCourse.getSemesters()) {
                if (semesterVo.getId().equals(_GroupId)) {
                    mChosenSemester = semesterVo;
                    mView.setStudyGroupItems(semesterVo.getStudyGroups());
                }
            }
        }

        /**
         * Choosing the group determines the timetable
         * @param _TimeTableId
         */
        @Override
        public void onGroupChosen(String _TimeTableId) {
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
                mChosenSemester = null;
            }
            else {
                Utils.showToast(R.string.timetable_error_incomplete);
            }
        }
    };

    private final Callback<TimeTableResponse> mTimeTableResponseCallback = new Callback<TimeTableResponse>() {
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
    private SemesterVo        mChosenSemester;
    private String            mChosenTimetableId;
}
