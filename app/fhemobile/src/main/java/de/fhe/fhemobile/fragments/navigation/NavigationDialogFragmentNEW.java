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

import androidx.annotation.Nullable;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.views.navigation.NavigationDialogView;
import de.fhe.fhemobile.vos.navigation.NavigationDialogVo;


/**
 * Created by Nadja 02.12.2021
 */
public class NavigationDialogFragmentNEW extends FeatureFragment {

    private NavigationDialogView mView = null;
    private NavigationDialogVo mVo;


    //Constants
    private static final String TAG = "NavigDialogFragment"; //$NON-NLS


    public NavigationDialogFragmentNEW(){
        // Required empty public constructor
    }

    public static NavigationDialogFragmentNEW newInstance(){
        NavigationDialogFragmentNEW fragment = new NavigationDialogFragmentNEW();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mVo = new NavigationDialogVo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mView = (NavigationDialogView) inflater.inflate(R.layout.fragment_navigation_dialog_new,
                container,false);

        mView.initializeView(getChildFragmentManager(), getLifecycle());

        return mView;

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
