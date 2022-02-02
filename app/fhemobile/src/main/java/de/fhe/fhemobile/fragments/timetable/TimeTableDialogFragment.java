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
import de.fhe.fhemobile.views.timetable.TimeTableDialogView;
import de.fhe.fhemobile.vos.timetable.TimeTableSemesterVo;
import de.fhe.fhemobile.vos.timetable.TimeTableResponse;
import de.fhe.fhemobile.vos.timetable.TimeTableStudyProgramVo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimeTableDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeTableDialogFragment extends FeatureFragment {

    public static final String TAG = "TimeTableDialogFragment"; //$NON-NLS

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TimeTableDialogFragment.
     */
    public static TimeTableDialogFragment newInstance() {
        final TimeTableDialogFragment fragment = new TimeTableDialogFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public TimeTableDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mChosenCourse       = null;
        mChosenSemester     = null;
        mChosenTimetableId  = null;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (TimeTableDialogView) inflater.inflate(R.layout.fragment_time_table_dialog, container, false);
        mView.setViewListener(mViewListener);
        mView.initializeView(getChildFragmentManager());

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        NetworkHandler.getInstance().fetchTimeTable(mTimeTableResponseCallback);
    }

    private void proceedToTimetable(final String _TimeTableId) {
        ((MainActivity) getActivity()).changeFragment(TimeTableFragment.newInstance(_TimeTableId),
                true, TAG);
    }

    private final TimeTableDialogView.IViewListener mViewListener = new TimeTableDialogView.IViewListener() {
        @Override
        public void onStudyCourseChosen(String _StudyCourseId) {
            mView.toggleGroupsPickerVisibility(false);
            mView.toggleButtonEnabled(false);
            mView.resetSemesterPicker();
            mView.resetGroupsPicker();

            mChosenCourse = null;
            mChosenSemester = null;

            boolean errorOccurred = false;

            for (TimeTableStudyProgramVo courseVo : mResponse.getStudyPrograms()) {
                if (courseVo.getId() != null && courseVo.getId().equals(_StudyCourseId)) {
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
        public void onSemesterChosen(final String _SemesterId) {
            mView.toggleGroupsPickerVisibility(true);
            mView.toggleButtonEnabled(false);
            mView.resetGroupsPicker();

            mChosenSemester = null;

            for (final TimeTableSemesterVo timeTableSemesterVo : mChosenCourse.getSemesters()) {
                if (timeTableSemesterVo.getId().equals(_SemesterId)) {
                    mChosenSemester = timeTableSemesterVo;
                    mView.setStudyGroupItems(timeTableSemesterVo.getStudyGroups());
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
        public void onResponse(final Call<TimeTableResponse> call, final Response<TimeTableResponse> response) {
            if ( response.body() != null ) {
                mResponse = response.body();
                mView.setStudyCourseItems(response.body().getStudyPrograms());
            }
        }

        @Override
        public void onFailure(final Call<TimeTableResponse> call, Throwable t) {

        }
    };

    private TimeTableDialogView mView;

    private TimeTableResponse mResponse;
    private TimeTableStudyProgramVo mChosenCourse;
    private TimeTableSemesterVo mChosenSemester;
    private String            mChosenTimetableId;
}
