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

package de.fhe.fhemobile.fragments.timetable;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Map;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.utils.ApiErrorUtils;
import de.fhe.fhemobile.utils.timetable.TimetableSettings;
import de.fhe.fhemobile.utils.Utils;
import de.fhe.fhemobile.views.timetable.TimetableDialogView;
import de.fhe.fhemobile.vos.ApiErrorResponse;
import de.fhe.fhemobile.vos.timetable.TimetableSemesterVo;
import de.fhe.fhemobile.vos.timetable.TimetableDialogResponse;
import de.fhe.fhemobile.vos.timetable.TimetableStudyProgramVo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimetableDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimetableDialogFragment extends FeatureFragment {

    public static final String TAG = TimetableDialogFragment.class.getSimpleName();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TimetableDialogFragment.
     */
    public static TimetableDialogFragment newInstance() {
        final TimetableDialogFragment fragment = new TimetableDialogFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public TimetableDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mChosenStudyProgram = null;
        mChosenSemester     = null;
        mChosenStudyGroup   = null;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (TimetableDialogView) inflater.inflate(R.layout.fragment_timetable_dialog, container, false);
        mView.setViewListener(mViewListener);
        mView.initializeView(getChildFragmentManager());

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //replacement of deprecated setHasOptionsMenu(), onCreateOptionsMenu() and onOptionsItemSelected()
        // see https://developer.android.com/jetpack/androidx/releases/activity#1.4.0-alpha01
        final MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull final Menu menu, @NonNull final MenuInflater menuInflater) {
                // Add menu items here
                menu.clear();
                menuInflater.inflate(R.menu.menu_main, menu);
                menu.findItem(R.id.action_settings).setVisible(false);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull final MenuItem menuItem) {
                // Handle the menu selection
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        NetworkHandler.getInstance().fetchStudyPrograms(mFetchStudyProgramsCallback);
    }

    void proceedToTimetable(final String _TimetableId) {
        ((MainActivity) getActivity()).changeFragment(TimetableFragment.newInstance(_TimetableId),
                true, TimetableFragment.TAG);
    }

    private final TimetableDialogView.IViewListener mViewListener = new TimetableDialogView.IViewListener() {
        @Override
        public void onStudyProgramChosen(final String _StudyProgramId) {
            mView.toggleGroupsPickerVisibility(false);
            mView.toggleButtonEnabled(false);
            mView.resetSemesterPicker();
            mView.resetGroupsPicker();

            mChosenStudyProgram = null;
            mChosenSemester = null;

            boolean errorOccurred = false;
            final Map<String, TimetableStudyProgramVo> studyPrograms = mResponse.getStudyPrograms();

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

            final Map<String, TimetableSemesterVo> semesters = mChosenStudyProgram.getSemesters();
            if(semesters.containsKey(_SemesterId)) {
                mChosenSemester = semesters.get(_SemesterId);
                mView.setStudyGroupItems(mChosenSemester.getStudyGroupList());
            }
        }

        /**
         * Choosing the group determines the timetable
         * @param _TimetableId
         */
        @Override
        public void onStudyGroupChosen(final String _TimetableId) {
            mView.toggleButtonEnabled(true);
            mChosenStudyGroup = _TimetableId;
        }

        @Override
        public void onSearchClicked() {
            if (mChosenStudyGroup != null) {
                if (mView.isRememberActivated()) {
                    TimetableSettings.saveTimetableSelection(mChosenStudyGroup);
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


    private final Callback<TimetableDialogResponse> mFetchStudyProgramsCallback = new Callback<TimetableDialogResponse>() {
        @Override
        public void onResponse(@NonNull final Call<TimetableDialogResponse> call, final Response<TimetableDialogResponse> response) {
            if (response.isSuccessful()){
                mResponse = response.body();

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
                ApiErrorUtils.showErrorToast(error, ApiErrorUtils.ApiErrorCode.TIMETABLE_DIALOG_FRAGMENT_CODE1);
            }
        }

        @Override
        public void onFailure(final Call<TimetableDialogResponse> call, final Throwable t) {
            ApiErrorUtils.showConnectionErrorToast(ApiErrorUtils.ApiErrorCode.TIMETABLE_DIALOG_FRAGMENT_CODE2);
            Log.d(TAG, "failure: request " + call.request().url() + " - "+ t.getMessage());
        }
    };

    TimetableDialogView mView;

    TimetableDialogResponse mResponse;
    TimetableStudyProgramVo mChosenStudyProgram;
    TimetableSemesterVo mChosenSemester;
    String                  mChosenStudyGroup;
}