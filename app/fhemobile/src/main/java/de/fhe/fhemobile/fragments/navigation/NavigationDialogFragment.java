/*
 *  Copyright (c) 2019-2021 Ernst-Abbe-Hochschule Jena
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

package de.fhe.fhemobile.fragments.navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.views.navigation.NavigationDialogView;


/**
 * Created by Nadja 02.12.2021
 */
public class NavigationDialogFragment extends FeatureFragment {


    //Constants
    private static final String TAG = "NavigDialogFragment"; //$NON-NLS

    public NavigationDialogFragment(){
        // Required empty public constructor
    }

    public static NavigationDialogFragment newInstance(){
        final NavigationDialogFragment fragment = new NavigationDialogFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mView = (NavigationDialogView) inflater.inflate(R.layout.fragment_navigation_dialog,
                container,false);

        mView.initializeView(getChildFragmentManager(), getLifecycle());

        return mView;

    }

    private NavigationDialogView mView;
}
