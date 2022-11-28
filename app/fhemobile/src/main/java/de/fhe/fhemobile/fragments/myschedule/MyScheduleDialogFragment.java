/*
 *  Copyright (c) 2014-2022 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
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

package de.fhe.fhemobile.fragments.myschedule;


import static android.content.ContentValues.TAG;

import static de.fhe.fhemobile.utils.myschedule.MyScheduleUtils.groupByEventTitle;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.myschedule.MyScheduleDialogAdapter;
import de.fhe.fhemobile.comparator.EventSeriesTitleComparator;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.utils.ApiErrorUtils;
import de.fhe.fhemobile.utils.Utils;
import de.fhe.fhemobile.views.myschedule.MyScheduleDialogView;
import de.fhe.fhemobile.vos.ApiErrorResponse;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventSeriesVo;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventSetVo;
import de.fhe.fhemobile.vos.timetable.TimetableDialogResponse;
import de.fhe.fhemobile.vos.timetable.TimetableSemesterVo;
import de.fhe.fhemobile.vos.timetable.TimetableStudyProgramVo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyScheduleDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * Display a modal dialog in front of the fragment of MySchedule
 */
public class MyScheduleDialogFragment extends DialogFragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MyScheduleSettingsFragment.
     */
    public static MyScheduleDialogFragment newInstance() {
        final MyScheduleDialogFragment fragment = new MyScheduleDialogFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MyScheduleDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mChosenStudyProgram = null;
        mChosenSemester = null;

        mListAdapter = new MyScheduleDialogAdapter(getContext());
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
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (MyScheduleDialogView) inflater.inflate(R.layout.fragment_myschedule_dialog,
                container, false);

        mView.initializeView(getChildFragmentManager());
        mView.setViewListener(mViewListener);

        mView.setEventListAdapter(mListAdapter);

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        NetworkHandler.getInstance().fetchStudyProgramData(mFetchStudyProgramDataCallback);
    }



    private final MyScheduleDialogView.IViewListener mViewListener = new MyScheduleDialogView.IViewListener() {

        @Override
        public void onStudyProgramChosen(final String _StudyProgramId) {
            //reset needed because a new study course had been chosen
            mView.resetSemesterPicker();
            mView.toggleEventListVisibility(false);

            mListAdapter.setItems(new ArrayList<>());
            mListAdapter.notifyDataSetChanged();

            mChosenStudyProgram = null;
            mChosenSemester = null;


            boolean studyProgramEmpty = true;

            for (final TimetableStudyProgramVo studyProgram : mStudyProgramDataResponse.getStudyProgramsAsList()) {
                if (studyProgram != null
                        && studyProgram.getId() != null
                        && studyProgram.getId().equals(_StudyProgramId)) {

                    mChosenStudyProgram = studyProgram;

                    // Check if study program has any semesters available
                    if (studyProgram.getSemesters() != null) {
                        studyProgramEmpty = false;

                        //set items of semester picker
                        mView.setSemesterItems(studyProgram.getSemestersAsSortedList());
                    }
                    // No terms are available
                    else {
                        studyProgramEmpty = true;
                    }

                    //chosen study program found
                    break;
                }
                else {
                    // No Id is available, chosen study program not found
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
        public void onSemesterChosen(final String _SemesterId) {
            mView.toggleProgressIndicatorVisibility(true);
            mView.toggleEventListVisibility(false);
            mListAdapter.setItems(new ArrayList<>());
            mListAdapter.notifyDataSetChanged();

            mChosenSemester = null;

            final Map<String, TimetableSemesterVo> semesters = mChosenStudyProgram.getSemesters();
            if (semesters.containsKey(_SemesterId)) {

                //set chosen semester
                mChosenSemester = semesters.get(_SemesterId);

                Assert.assertTrue(mChosenSemester != null);

                //get timetable (all events) for each study group in the chosen semester
                NetworkHandler.getInstance().fetchSemesterTimetable(mChosenSemester.getId(), mFetchSemesterTimetableCallback);
            }

        }

    };

    private final Callback<TimetableDialogResponse> mFetchStudyProgramDataCallback = new Callback<TimetableDialogResponse>() {
        @Override
        public void onResponse(@NonNull final Call<TimetableDialogResponse> call, final Response<TimetableDialogResponse> response) {
            if(response.body() != null){

                mStudyProgramDataResponse = response.body();

                final ArrayList<TimetableStudyProgramVo> studyPrograms = new ArrayList<>();
                //remove "Brückenkurse" and only keep bachelor and master study programs
                for(final TimetableStudyProgramVo studyProgramVo : response.body().getStudyProgramsAsList()){

                    if("Bachelor".equals(studyProgramVo.getDegree())
                            || "Master".equals(studyProgramVo.getDegree())){
                        studyPrograms.add(studyProgramVo);
                    }
                }

                mView.setStudyProgramItems(studyPrograms);
            } else {
                final ApiErrorResponse error = ApiErrorUtils.getApiErrorResponse(response);
                ApiErrorUtils.showErrorToast(error, ApiErrorUtils.ApiErrorCode.MYSCHEDULE_DIALOG_FRAGMENT_CODE3);
            }
            mView.toggleProgressIndicatorVisibility(false);
        }

        @Override
        public void onFailure(final Call<TimetableDialogResponse> call, final Throwable t) {
            ApiErrorUtils.showConnectionErrorToast(ApiErrorUtils.ApiErrorCode.MYSCHEDULE_DIALOG_FRAGMENT_CODE1);
            Log.d(TAG, "failure: request " + call.request().url() + " - "+ t.getMessage());
        }
    };



    final Callback<Map<String, MyScheduleEventSetVo>> mFetchSemesterTimetableCallback = new Callback<Map<String, MyScheduleEventSetVo>>() {
        @Override
        public void onResponse(@NonNull final Call<Map<String, MyScheduleEventSetVo>> call, final Response<Map<String, MyScheduleEventSetVo>> response) {
            if(response.body() != null){

                final List<MyScheduleEventSeriesVo> eventSeriesVos = groupByEventTitle(response.body());

                Collections.sort(eventSeriesVos, new EventSeriesTitleComparator());

                mView.toggleEventListVisibility(true);
                mListAdapter.setItems(eventSeriesVos);

            } else {
                final ApiErrorResponse error = ApiErrorUtils.getApiErrorResponse(response);
                ApiErrorUtils.showErrorToast(error, ApiErrorUtils.ApiErrorCode.MYSCHEDULE_DIALOG_FRAGMENT_CODE4);
            }
            mView.toggleProgressIndicatorVisibility(false);

        }

        @Override
        public void onFailure(final Call<Map<String, MyScheduleEventSetVo>> call, final Throwable t) {
            ApiErrorUtils.showConnectionErrorToast(ApiErrorUtils.ApiErrorCode.MYSCHEDULE_DIALOG_FRAGMENT_CODE2);
            Log.d(TAG, "failure: request " + call.request().url() + " - "+ t.getMessage());
        }
    };


    MyScheduleDialogView mView;
    TimetableDialogResponse mStudyProgramDataResponse;

    TimetableStudyProgramVo mChosenStudyProgram;
    TimetableSemesterVo mChosenSemester;

    MyScheduleDialogAdapter mListAdapter;


}
