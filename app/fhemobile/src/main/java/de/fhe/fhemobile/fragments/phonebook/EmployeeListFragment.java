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

package de.fhe.fhemobile.fragments.phonebook;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.views.phonebook.EmployeeListView;


public class EmployeeListFragment extends Fragment {

    @FunctionalInterface
    public interface EmployeeListCallbacks {
        void onEmployeeChosen(Integer _ListPosition);
    }

    public EmployeeListFragment() {
        // Required empty public constructor
    }

    public static EmployeeListFragment newInstance() {
        final EmployeeListFragment fragment = new EmployeeListFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: Save instance list
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        EmployeeListView mView = (EmployeeListView) inflater.inflate(R.layout.fragment_employee_list, container, false);
        mView.initializeView(mViewListener);
        return mView;
    }

    @Override
    public void onAttach(@NonNull final Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (EmployeeListCallbacks) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity
                    + " must implement EmployeeListCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private final EmployeeListView.ViewListener mViewListener = new EmployeeListView.ViewListener() {
        @Override
        public void onListItemClicked(final Integer _ListPosition) {
            mCallbacks.onEmployeeChosen(_ListPosition);
        }
    };

    EmployeeListCallbacks mCallbacks;

}
