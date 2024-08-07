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

package de.fhe.fhemobile.fragments.maps;

import android.content.Intent;
import android.os.Bundle;
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

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MapsActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.views.maps.MapsDialogView;

/**
 * Fragment where the user selects a building that's maps/floorplans should be shown (in a new acitivity/MapsActivty)
 *
 * Edit by Nadja 02.12.2021: rename from MapsFragment to MapsDialogFragment
 */
public class MapsDialogFragment extends FeatureFragment {

    public static final String TAG = MapsDialogFragment.class.getSimpleName();

    public MapsDialogFragment() {
        super(TAG);
    }

    public static MapsDialogFragment newInstance() {
        final MapsDialogFragment fragment = new MapsDialogFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final MapsDialogView mView = (MapsDialogView) inflater.inflate(R.layout.fragment_maps_dialog, container, false);

        mView.initializeView(mViewListener);

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

    private final MapsDialogView.ViewListener mViewListener = new MapsDialogView.ViewListener() {
        @Override
        public void onMapItemClicked(final Integer _Position) {
            final Intent intent = new Intent(getActivity(), MapsActivity.class);
            intent.putExtra(MapsActivity.EXTRA_MAP_ID, _Position);
            startActivity(intent);
        }
    };

}
