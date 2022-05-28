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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Map;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.utils.timetable.TimeTableSettings;
import de.fhe.fhemobile.utils.Utils;
import de.fhe.fhemobile.views.timetable.TimeTableDialogView;
import de.fhe.fhemobile.vos.timetable.TimeTableSemesterVo;
import de.fhe.fhemobile.vos.timetable.TimeTableDialogResponse;
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

    public static final String TAG = TimeTableDialogFragment.class.getSimpleName();

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

        mChosenStudyProgram = null;
        mChosenSemester     = null;
        mChosenStudyGroup = null;
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
        NetworkHandler.getInstance().fetchStudyProgramData(mFetchStudyProgramDataCallback);
    }

    void proceedToTimetable(final String _TimeTableId) {
        ((MainActivity) getActivity()).changeFragment(TimeTableFragment.newInstance(_TimeTableId),
                true, TimeTableFragment.TAG);
    }

    private final TimeTableDialogView.IViewListener mViewListener = new TimeTableDialogView.IViewListener() {
        @Override
        public void onStudyProgramChosen(String _StudyProgramId) {
            mView.toggleGroupsPickerVisibility(false);
            mView.toggleButtonEnabled(false);
            mView.resetSemesterPicker();
            mView.resetGroupsPicker();

            mChosenStudyProgram = null;
            mChosenSemester = null;

            boolean errorOccurred = false;
            Map<String, TimeTableStudyProgramVo> studyPrograms = mResponse.getStudyPrograms();

            if (studyPrograms.containsKey(_StudyProgramId)) {
                mChosenStudyProgram = studyPrograms.get(_StudyProgramId);

                // Check if study program has any semesters
                if (mChosenStudyProgram.getSemestersAsSortedList() != null) {
                    errorOccurred = false;
                    mView.setSemesterItems(mChosenStudyProgram.getSemestersAsSortedList());
                }
                else {
                    // No semesters in this study program
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

            Map<String, TimeTableSemesterVo> semesters = mChosenStudyProgram.getSemesters();
            if(semesters.containsKey(_SemesterId)) {
                mChosenSemester = semesters.get(_SemesterId);
                mView.setStudyGroupItems(mChosenSemester.getStudyGroupList());
            }
        }

        /**
         * Choosing the group determines the timetable
         * @param _TimeTableId
         */
        @Override
        public void onStudyGroupChosen(String _TimeTableId) {
            mView.toggleButtonEnabled(true);
            mChosenStudyGroup = _TimeTableId;
        }

        @Override
        public void onSearchClicked() {
            if (mChosenStudyGroup != null) {
                if (mView.isRememberActivated()) {
                    TimeTableSettings.saveTimeTableSelection(mChosenStudyGroup);
                }
                proceedToTimetable(mChosenStudyGroup);

                mChosenStudyProgram = null;
                mChosenSemester = null;
                mChosenStudyGroup = null;
            }
            else {
                Utils.showToast(R.string.timetable_error_incomplete);
            }
        }
    };


    private final Callback<TimeTableDialogResponse> mFetchStudyProgramDataCallback = new Callback<TimeTableDialogResponse>() {
        @Override
        public void onResponse(final Call<TimeTableDialogResponse> call, final Response<TimeTableDialogResponse> response) {
            if ( response.body() != null ) {
                mResponse = response.body();

                ArrayList<TimeTableStudyProgramVo> studyPrograms = new ArrayList<>();
                //remove "Brückenkurse" and only keep bachelor and master study programs
                for(TimeTableStudyProgramVo studyProgramVo : response.body().getStudyProgramsAsList()){

                    if("Bachelor".equals(studyProgramVo.getDegree())
                            || "Master".equals(studyProgramVo.getDegree())){
                        studyPrograms.add(studyProgramVo);
                    }
                }

                mView.setStudyCourseItems(studyPrograms);
            }
        }

        @Override
        public void onFailure(final Call<TimeTableDialogResponse> call, Throwable t) {
            showErrorToast();
            Log.d(TAG, "failure: request " + call.request().url() + " - "+ t.getMessage());
        }
    };

    static void showErrorToast() {
        Toast.makeText(Main.getAppContext(), "Cannot establish connection!",
                Toast.LENGTH_LONG).show();
    }

    TimeTableDialogView     mView;

    TimeTableDialogResponse mResponse;
    TimeTableStudyProgramVo mChosenStudyProgram;
    TimeTableSemesterVo     mChosenSemester;
    String                  mChosenStudyGroup;
}
