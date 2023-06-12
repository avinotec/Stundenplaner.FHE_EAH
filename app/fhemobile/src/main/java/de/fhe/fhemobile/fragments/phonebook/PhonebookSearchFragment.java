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

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.phonebook.EmployeeInformationActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.views.phonebook.PhonebookSearchView;


public class PhonebookSearchFragment extends FeatureFragment {

    private static final String TAG = PhonebookSearchFragment.class.getSimpleName();

    public PhonebookSearchFragment() {
        super(TAG);
    }

    public static PhonebookSearchFragment newInstance() {
        final PhonebookSearchFragment fragment = new PhonebookSearchFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        PhonebookSearchView mView = (PhonebookSearchView) inflater.inflate(R.layout.fragment_phonebook_search, container, false);
        mView.initializeView(mViewListener);
        return mView;
    }
    private final PhonebookSearchView.ViewListener mViewListener = new PhonebookSearchView.ViewListener() {
        @Override
        public void onSearchClicked(final String _FirstName, final String _LastName) {
            NetworkHandler.getInstance().fetchEmployees(_FirstName, _LastName);
        }

        @Override
        public void onEmployeesFound() {
            final Intent intent = new Intent(getActivity(), EmployeeInformationActivity.class);
            startActivity(intent);
        }
    };


}
