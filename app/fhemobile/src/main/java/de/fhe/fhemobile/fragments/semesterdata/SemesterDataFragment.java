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

package de.fhe.fhemobile.fragments.semesterdata;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.events.Event;
import de.fhe.fhemobile.events.EventListener;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.models.semesterdata.SemesterDataModel;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.views.semesterdata.SemesterDataView;

public class SemesterDataFragment extends FeatureFragment {


    public SemesterDataFragment() {
        // Required empty public constructor
    }

    public static SemesterDataFragment newInstance() {
        final SemesterDataFragment fragment = new SemesterDataFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mModel = SemesterDataModel.getInstance();

//        if (getArguments() != null) {
//
//        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (SemesterDataView) inflater.inflate(R.layout.fragment_semester_data, container, false);
        mView.initializeView();

        if(SemesterDataModel.getInstance().getSemesterData() == null) {
            NetworkHandler.getInstance().fetchSemesterData();
        }

        return mView;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        menu.clear();
        inflater.inflate(R.menu.menu_semester_data, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    //onOptionsItemSelected-------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(final MenuItem _item) {
        /* Checks use of resource IDs in places requiring constants
            Avoid the usage of resource IDs where constant expressions are required.
            A future version of the Android Gradle Plugin will generate R classes with
            non-constant IDs in order to improve the performance of incremental compilation.
            Issue id: NonConstantResourceId
         */
        if (_item.getItemId() == R.id.action_settings) {
            if (null != mModel && null != mModel.getSemesterData()) {
                if (mModel.getChosenSemester() < mModel.getSemesterData().length - 1) {
                    mModel.setChosenSemester(mModel.getChosenSemester() + 1);
                } else {
                    mModel.setChosenSemester(0);
                }
            }

            return true;

            //other item
        }
        return super.onOptionsItemSelected(_item);
    }

    @Override
    public void onResume() {
        super.onResume();
        mView.registerModelListener();

        mModel.addListener(SemesterDataModel.ChangeEvent.RECEIVED_SEMESTER_DATA, mModelChangeListener);
        mModel.addListener(SemesterDataModel.ChangeEvent.SEMESTER_SELECTION_CHANGED, mModelChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        mView.deregisterModelListener();

        mModel.removeListener(SemesterDataModel.ChangeEvent.RECEIVED_SEMESTER_DATA, mModelChangeListener);
        mModel.removeListener(SemesterDataModel.ChangeEvent.SEMESTER_SELECTION_CHANGED, mModelChangeListener);
    }

    @Override
    public void onRestoreActionBar(final ActionBar _ActionBar) {
        super.onRestoreActionBar(_ActionBar);
        // As the onCreate method hasn't been called yet, the mModel still is a NP and can't be accessed.

        if (SemesterDataModel.getInstance().getSemesterData() != null) {
            _ActionBar.setTitle(SemesterDataModel.getInstance()
                    .getSemesterData()[SemesterDataModel.getInstance().getChosenSemester()]
                    .getLongName());
        }
    }

    private final EventListener mModelChangeListener = new EventListener() {
        @Override
        public void onEvent(final Event event) {
            // Used to update the ActionbarTitle if the selection has changed
            if(event.getType().equals(SemesterDataModel.ChangeEvent.RECEIVED_SEMESTER_DATA) ||
               event.getType().equals(SemesterDataModel.ChangeEvent.SEMESTER_SELECTION_CHANGED)) {
                setActionBarTitle(mModel.getSemesterData()[mModel.getChosenSemester()].getLongName());
            }
        }
    };

    private SemesterDataView mView;
    private SemesterDataModel mModel;

}
