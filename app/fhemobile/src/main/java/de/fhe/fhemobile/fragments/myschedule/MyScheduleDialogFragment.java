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
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.myschedule.MyScheduleDialogAdapter;
import de.fhe.fhemobile.comparator.EventSeriesTitleComparator;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.utils.Utils;
import de.fhe.fhemobile.views.myschedule.MyScheduleDialogView;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventSeriesVo;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventSetVo;
import de.fhe.fhemobile.vos.timetable.TimeTableDialogResponse;
import de.fhe.fhemobile.vos.timetable.TimeTableSemesterVo;
import de.fhe.fhemobile.vos.timetable.TimeTableStudyProgramVo;
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
    public void onCreate(Bundle savedInstanceState) {
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

            for (final TimeTableStudyProgramVo studyProgram : mStudyProgramDataResponse.getStudyProgramsAsList()) {
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
        public void onSemesterChosen(String _SemesterId) {
            mView.toggleProgressIndicatorVisibility(true);
            mView.toggleEventListVisibility(false);
            mListAdapter.setItems(new ArrayList<>());
            mListAdapter.notifyDataSetChanged();

            mChosenSemester = null;

            final Map<String, TimeTableSemesterVo> semesters = mChosenStudyProgram.getSemesters();
            if (semesters.containsKey(_SemesterId)) {

                //set chosen semester
                mChosenSemester = semesters.get(_SemesterId);

                Assert.assertTrue(mChosenSemester != null);

                //get timetable (all events) for each study group in the chosen semester
                NetworkHandler.getInstance().fetchSemesterTimeTable(mChosenSemester.getId(), mFetchSemesterTimeTableCallback);
            }

        }

    };

    private final Callback<TimeTableDialogResponse> mFetchStudyProgramDataCallback = new Callback<TimeTableDialogResponse>() {
        @Override
        public void onResponse(Call<TimeTableDialogResponse> call, Response<TimeTableDialogResponse> response) {
            if(response.body() != null){

                mStudyProgramDataResponse = response.body();

                final ArrayList<TimeTableStudyProgramVo> studyPrograms = new ArrayList<>();
                //remove "Brückenkurse" and only keep bachelor and master study programs
                for(TimeTableStudyProgramVo studyProgramVo : response.body().getStudyProgramsAsList()){

                    if("Bachelor".equals(studyProgramVo.getDegree())
                            || "Master".equals(studyProgramVo.getDegree())){
                        studyPrograms.add(studyProgramVo);
                    }
                }

                mView.setStudyProgramItems(studyPrograms);
            } else {
                showInternalProblemToast();
            }
            mView.toggleProgressIndicatorVisibility(false);
        }

        @Override
        public void onFailure(Call<TimeTableDialogResponse> call, Throwable t) {
            showConnectionErrorToast();
            Log.d(TAG, "failure: request " + call.request().url());
        }
    };



    final Callback<Map<String, MyScheduleEventSetVo>> mFetchSemesterTimeTableCallback = new Callback<Map<String, MyScheduleEventSetVo>>() {
        @Override
        public void onResponse(Call<Map<String, MyScheduleEventSetVo>> call, Response<Map<String, MyScheduleEventSetVo>> response) {
            if(response.body() != null){

                List<MyScheduleEventSeriesVo> eventSeriesVos = groupByEventTitle(response.body());

                Collections.sort(eventSeriesVos, new EventSeriesTitleComparator());

                mView.toggleEventListVisibility(true);
                mListAdapter.setItems(eventSeriesVos);

            } else {
                showInternalProblemToast();
            }
            mView.toggleProgressIndicatorVisibility(false);

        }

        @Override
        public void onFailure(Call<Map<String, MyScheduleEventSetVo>> call, Throwable t) {
            showConnectionErrorToast();
            Log.d(TAG, "failure: request " + call.request().url());
        }
    };

    static void showConnectionErrorToast() {
        Toast.makeText(Main.getAppContext(), Main.getAppContext().getString(R.string.connection_failed),
                Toast.LENGTH_LONG).show();
    }

    static void showInternalProblemToast(){
        Toast.makeText(Main.getAppContext(),
                Main.getAppContext().getString(R.string.internal_problems),
                Toast.LENGTH_LONG).show();
    }


    MyScheduleDialogView mView;
    TimeTableDialogResponse     mStudyProgramDataResponse;

    TimeTableStudyProgramVo     mChosenStudyProgram;
    TimeTableSemesterVo         mChosenSemester;

    MyScheduleDialogAdapter mListAdapter;


}
