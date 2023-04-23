/*
 *  Copyright (c) 2019-2022 Ernst-Abbe-Hochschule Jena
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

import static de.fhe.fhemobile.utils.Define.Navigation.KEY_SCANNED_ROOM;
import static de.fhe.fhemobile.utils.Define.Navigation.REQUEST_SCANNED_START_ROOM;

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
import androidx.fragment.app.FragmentResultListener;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.utils.Define;
import de.fhe.fhemobile.views.navigation.NavigationDialogView;


/**
 * Created by Nadja 02.12.2021
 */
public class NavigationDialogFragment extends FeatureFragment {

    //Constants
    static final String TAG = NavigationDialogFragment.class.getSimpleName();

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

        //listen for NavigationScannerFragment to send scanning result
        getParentFragmentManager().setFragmentResultListener(REQUEST_SCANNED_START_ROOM, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull final String requestKey, @NonNull final Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported
                final String result = bundle.getString(KEY_SCANNED_ROOM);

                Log.d(TAG, "scanned QR code received in NavigationDialogFragment: "+result);
                //send room to children
                final Bundle childBundle = new Bundle();
                childBundle.putString(Define.Navigation.KEY_SCANNED_ROOM, result);
                getChildFragmentManager().setFragmentResult(Define.Navigation.REQUEST_SCANNED_START_ROOM, childBundle);

            }
        });
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        NavigationDialogView mView = (NavigationDialogView) inflater.inflate(R.layout.fragment_navigation_dialog,
                container, false);

        mView.initializeView(getChildFragmentManager(), getLifecycle());

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
            }

            @Override
            public boolean onMenuItemSelected(@NonNull final MenuItem menuItem) {
                // Handle the menu selection
                return false;
            }
        });
    }

}
