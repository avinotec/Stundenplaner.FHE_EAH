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
package de.fhe.fhemobile.fragments.canteen;

import android.app.Activity;
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

import java.util.Objects;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.SettingsActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.utils.feature.Features;
import de.fhe.fhemobile.views.canteen.CanteenView;

/**
 * Created by Nadja - 05.05.022
 */
public class CanteenFragment extends FeatureFragment {

    private static final String TAG = CanteenFragment.class.getSimpleName();

    public CanteenFragment(){
        super(TAG);
    }

    public static CanteenFragment newInstance(){
        final CanteenFragment fragment = new CanteenFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        NetworkHandler.getInstance().fetchCanteenMenus();

        // Inflate the layout for this fragment
        mView = (CanteenView) inflater.inflate(R.layout.fragment_canteen, container, false);
        mView.initializeView(getChildFragmentManager(), getLifecycle());

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //replacement of deprecated setHasOptionsMenu(), onCreateOptionsMenu() and onOptionsItemSelected()
        // see https://developer.android.com/jetpack/androidx/releases/activity#1.4.0-alpha01
        final MenuHost menuHost = requireActivity();
        final Activity activity = getActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull final Menu menu, @NonNull final MenuInflater menuInflater) {
                // Add menu items here
                menu.clear();
                menuInflater.inflate(R.menu.menu_canteen, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull final MenuItem menuItem) {
                // Handle the menu selection
                if (menuItem.getItemId() == R.id.action_settings_canteen) {
                    final Intent intent = new Intent(activity, SettingsActivity.class);
                    intent.putExtra(SettingsActivity.EXTRA_SETTINGS_ID, Features.FeatureId.CANTEEN);
                    Objects.requireNonNull(activity).startActivity(intent);
                    return true;
                }

                //replaced by swipe down gesture
//                if (menuItem.getItemId() == R.id.action_update_canteen){
//                    NetworkHandler.getInstance().fetchCanteenMenus();
//                }

                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mView.registerModelListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        mView.deregisterModelListener();
    }

    private CanteenView mView;
}
