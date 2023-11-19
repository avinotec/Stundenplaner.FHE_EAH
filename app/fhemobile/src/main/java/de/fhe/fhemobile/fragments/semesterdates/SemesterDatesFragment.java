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

package de.fhe.fhemobile.fragments.semesterdates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.events.Event;
import de.fhe.fhemobile.events.EventListener;
import de.fhe.fhemobile.events.SemesterDatesChangeEvent;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.models.semesterdates.SemesterDatesModel;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.views.semesterdates.SemesterDatesView;

public class SemesterDatesFragment extends FeatureFragment {

    public static final String TAG = SemesterDatesFragment.class.getSimpleName();

    public SemesterDatesFragment() {
        super(TAG);
    }

    public static SemesterDatesFragment newInstance() {
        final SemesterDatesFragment fragment = new SemesterDatesFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mModel = SemesterDatesModel.getInstance();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (SemesterDatesView) inflater.inflate(R.layout.fragment_semester_dates, container, false);
        mView.initializeView();

        if(SemesterDatesModel.getInstance().getSemesterVos() == null) {
            NetworkHandler.getInstance().fetchSemesterDates();
        }

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

                menu.clear();
                // Add menu items here
                menuInflater.inflate(R.menu.menu_semester_dates, menu);
                menuHost.invalidateMenu();
            }

            @Override
            public void onPrepareMenu(@NonNull final Menu menu) {
                MenuProvider.super.onPrepareMenu(menu);

                final MenuItem itemPreviousSemester = menu.findItem(R.id.action_previous_semester);
                if(itemPreviousSemester != null){
                    if(mModel.getChosenSemester() == 0){
                        itemPreviousSemester.setEnabled(false);
                        itemPreviousSemester.getIcon().setAlpha(133);
                    } else {
                        itemPreviousSemester.setEnabled(true);
                        itemPreviousSemester.getIcon().setAlpha(255);
                    }
                }

                final MenuItem itemNextSemester = menu.findItem(R.id.action_next_semester);
                if(itemNextSemester != null){
                    if(mModel.getSemesterVos() != null &&
                            mModel.getChosenSemester() == mModel.getSemesterVos().length - 1){
                        itemNextSemester.setEnabled(false);
                        itemNextSemester.getIcon().setAlpha(133);
                    } else {
                        itemNextSemester.setEnabled(true);
                        itemNextSemester.getIcon().setAlpha(255);
                    }

                }
            }

            @Override
            public boolean onMenuItemSelected(@NonNull final MenuItem menuItem) {
                // Handle the menu selection
                //previous semester
                if(menuItem.getItemId() == R.id.action_previous_semester){
                    if (null != mModel && null != mModel.getSemesterVos()) {
                        if (mModel.getChosenSemester() >= 1) {
                            mModel.setChosenSemester(mModel.getChosenSemester() - 1);
                            //calls onPrepareMenu and updates menu items
                            menuHost.invalidateMenu();
                        }
                    }

                    return true;
                }
                //next semester
                if(menuItem.getItemId() == R.id.action_next_semester){
                    if (null != mModel && null != mModel.getSemesterVos()) {
                        if (mModel.getChosenSemester() < mModel.getSemesterVos().length - 1) {
                            mModel.setChosenSemester(mModel.getChosenSemester() + 1);
                            //calls onPrepareMenu and updates menu items
                            menuHost.invalidateMenu();
                        }
                    }

                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mView.registerModelListener();

        mModel.addListener(SemesterDatesChangeEvent.RECEIVED_SEMESTER_DATES, mModelChangeListener);
        mModel.addListener(SemesterDatesChangeEvent.SEMESTER_SELECTION_CHANGED, mModelChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        mView.deregisterModelListener();

        mModel.removeListener(SemesterDatesChangeEvent.RECEIVED_SEMESTER_DATES, mModelChangeListener);
        mModel.removeListener(SemesterDatesChangeEvent.SEMESTER_SELECTION_CHANGED, mModelChangeListener);
    }

    private final EventListener mModelChangeListener = new EventListener() {
        @Override
        public void onEvent(final Event event) {
            // Used to update the ActionbarTitle if the selection has changed
            if(event.getType().equals(SemesterDatesChangeEvent.RECEIVED_SEMESTER_DATES) ||
               event.getType().equals(SemesterDatesChangeEvent.SEMESTER_SELECTION_CHANGED)) {

                final TextView tvTitle = mView.findViewById(R.id.tv_semester_title);
                tvTitle.setText(mModel.getChosenSemesterVo().getLongName());
            }
        }
    };

    SemesterDatesView mView;
    SemesterDatesModel mModel;

}
