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

import org.openapitools.client.model.Studiengang;
import org.openapitools.client.model.StudiengangReponse;
import org.openapitools.client.model.VplGruppe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.fragments.timetable.converters.MosesConverter;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.utils.ApiErrorUtils;
import de.fhe.fhemobile.utils.Utils;
import de.fhe.fhemobile.utils.timetable.TimetableSettings;
import de.fhe.fhemobile.views.timetable.TimetableDialogView;
import de.fhe.fhemobile.vos.ApiErrorResponse;
import de.fhe.fhemobile.vos.timetable.TimetableDialogResponse;
import de.fhe.fhemobile.vos.timetable.TimetableSemesterVo;
import de.fhe.fhemobile.vos.timetable.TimetableStudyGroupVo;
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
        super(TAG);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mChosenStudyProgram = null;
        mChosenSemester = null;
        mChosenStudyGroup = null;
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
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
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
        /*
         * Call the studiengang/ endpoint to receive a list of all study programs
         */
        NetworkHandler.getInstance().mosesStudiengangApi
                .studiengangGetAll(
                        null,
                        10000,
                        studiengangReponseCallback
                );
    }

    /**
     * Construct a sorted, unique set of fachsemester from a vplGruppeResponse
     */
    Callback<List<VplGruppe>> vplGruppeResponseCallback = new Callback<List<VplGruppe>>() {
        @Override
        public void onResponse(
                Call<List<VplGruppe>> call,
                Response<List<VplGruppe>> response
        ) {
            if (response.isSuccessful()) {
                List<VplGruppe> vplGruppeList = response.body();
                if (vplGruppeList == null) return;

                List<TimetableSemesterVo> semesterList =
                        mosesConverter.semesterVosListFromVplGruppe(vplGruppeList);

                List<TimetableSemesterVo> semesterUniqueList = new ArrayList<>(
                        mosesConverter.semesterVosSetFromList(semesterList)
                );

                mView.toggleSemesterPickerVisibility(true);
                mView.setSemesterItems(semesterUniqueList);
            }
        }

        @Override
        public void onFailure(
                Call<List<VplGruppe>> call,
                Throwable throwable
        ) {
        }
    };

    Callback<List<VplGruppe>> vplGruppeWithSemesterResponceCallback =
            new Callback<List<VplGruppe>>() {
                @Override
                public void onResponse(
                        Call<List<VplGruppe>> call,
                        Response<List<VplGruppe>> response
                ) {
                    if (response.isSuccessful()) {
                        List<VplGruppe> vplGruppeResponse = response.body();
                        ArrayList<TimetableStudyGroupVo> studyGroupVoList = new ArrayList<>();

                        if (vplGruppeResponse == null) return;

                        for (VplGruppe vplGruppe : vplGruppeResponse) {
                            studyGroupVoList.add(
                                    mosesConverter.studyGroupFromVplGruppe(vplGruppe)
                            );
                        }

                        mView.setStudyGroupItems(studyGroupVoList);
                    }
                }

                @Override
                public void onFailure(
                        Call<List<VplGruppe>> call,
                        Throwable throwable
                ) {
                }
            };

    Callback<StudiengangReponse> studiengangReponseCallback = new Callback<StudiengangReponse>() {
        @Override
        public void onResponse(
                Call<StudiengangReponse> call,
                Response<StudiengangReponse> response
        ) {
            if (response.isSuccessful()) {
                StudiengangReponse studiengangReponse = response.body();
                /*
                 * Moses based list of events which will converted to fit with existing code
                 */
                ArrayList<Studiengang> studiengangList;
                ArrayList<TimetableStudyProgramVo> studiengangVoList = new ArrayList<>();

                if (studiengangReponse == null) return;
                studiengangList = (ArrayList<Studiengang>) studiengangReponse.getData();

                if (studiengangList != null) {
                    for (Studiengang studiengang : studiengangList) {
                        studiengangVoList.add(
                                mosesConverter.studyProgramFromStudiengang(studiengang)
                        );
                    }
                }

                mView.setStudyProgramItems(studiengangVoList);
            }
        }

        @Override
        public void onFailure(Call<StudiengangReponse> call, Throwable throwable) {
            // TODO: handle failure branch
        }
    };

    void proceedToTimetable(final String _TimetableId) {
        ((MainActivity) getActivity()).changeFragment(
                TimetableFragment.newInstance(_TimetableId),
                true
        );
    }

    /**
     * Resets controls except the study program picker.
     */
    private void resetControls() {
        mView.toggleGroupsPickerVisibility(false);
        mView.toggleButtonEnabled(false);
        mView.resetSemesterPicker();
        mView.resetGroupsPicker();
    }

    private void prepareGroupsPicker() {
        mView.toggleGroupsPickerVisibility(true);
        mView.toggleButtonEnabled(false);
        mView.resetGroupsPicker();
    }

    /**
     * Resetting variables from previous selections.
     */
    private void resetPreviousSelections() {
        mChosenStudyProgram = null;
        mChosenSemester = null;
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
                    //redundant errorOccurred = false;
                    mView.setSemesterItems(mChosenStudyProgram.getSemestersAsSortedList());
                } else {
                    // No semesters in this study program
                    errorOccurred = true;
                }
            }

            if (errorOccurred) {
                mView.toggleSemesterPickerVisibility(false);
                Utils.showToast(R.string.timetable_error);
            } else {
                mView.toggleSemesterPickerVisibility(true);
            }
        }

        /**
         * When a study program has been selected, a request is made
         * to obtain the available semesters and form controls and variables are reset.
         *
         * @param _StudyProgramName
         * @param _StudyProgramId
         */
        @Override
        public void onStudyProgramChosenMoses(
                final String _StudyProgramName,
                final Integer _StudyProgramId
        ) {
            /*
             * Resets controls except the study program control
             * to be invisible or disabled again.
             */
            resetControls();
            resetPreviousSelections();
            // When a study program is selected save its id for later use
            chosenStudyProgramId = _StudyProgramId;
            /*
             * When a study program is selected, the vplGruppe endpoint is called to receive the
             * available groups for that study program. We use this as a workaround to create a
             * set of available semesters by extracting those from the VplGruppe objects.
             *
             * Refactor: when there's a better endpoint available consider using it.
             */
            NetworkHandler.getInstance().mosesVplGruppeApi
                    .vplgruppestudiengangIdFindAll(
                            _StudyProgramId,
                            // The callback calls the next endpoint
                            vplGruppeResponseCallback
                    );
        }

        @Override
        public void onSemesterChosenMoses(final String _Semester) {
            // When a semester has been chosen, we reset the related controls
            prepareGroupsPicker();

            chosenSemester = Integer.parseInt(_Semester);

            NetworkHandler.getInstance().mosesVplGruppeApi
                    .vplgruppestudiengangIdsemesterFindAll(
                            chosenStudyProgramId,
                            chosenSemester,
                            vplGruppeWithSemesterResponceCallback
                    );
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
            } else {
                Utils.showToast(R.string.timetable_error_incomplete);
            }
        }
    };


    /**
     * Refator/delete:
     * When everythin is done and adapted for the new API delete this method.
     */
    private final Callback<TimetableDialogResponse> mFetchStudyProgramsCallback = new Callback<TimetableDialogResponse>() {
        @Override
        public void onResponse(@NonNull final Call<TimetableDialogResponse> call, final Response<TimetableDialogResponse> response) {
            if (response.isSuccessful()) {
                mResponse = response.body();

                final ArrayList<TimetableStudyProgramVo> studyPrograms = new ArrayList<>();
                //remove "Brückenkurse" and only keep bachelor and master study programs
                for (final TimetableStudyProgramVo studyProgramVo : response.body().getStudyProgramsAsList()) {

                    if ("Bachelor".equals(studyProgramVo.getDegree())
                            || "Master".equals(studyProgramVo.getDegree())) {
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
            Log.d(TAG, "failure: request " + call.request().url() + " - " + t.getMessage());
        }
    };

    TimetableDialogView mView;

    TimetableDialogResponse mResponse;
    TimetableStudyProgramVo mChosenStudyProgram;
    TimetableSemesterVo mChosenSemester;
    Integer chosenSemester;
    Integer chosenStudyProgramId;
    String mChosenStudyGroup;
    MosesConverter mosesConverter = new MosesConverter();
}
