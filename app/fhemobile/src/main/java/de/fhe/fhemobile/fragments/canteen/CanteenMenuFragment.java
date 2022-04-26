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

package de.fhe.fhemobile.fragments.canteen;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.SettingsActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.models.canteen.CanteenModel;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.utils.UserSettings;
import de.fhe.fhemobile.utils.feature.Features;
import de.fhe.fhemobile.views.canteen.CanteenView;


public class CanteenMenuFragment extends FeatureFragment {

    public CanteenMenuFragment() {
        // Required empty public constructor
    }

    public static CanteenMenuFragment newInstance() {
        final CanteenMenuFragment fragment = new CanteenMenuFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //enable settings in app bar
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (CanteenView) inflater.inflate(R.layout.fragment_canteen_food, container, false);
        mView.initializeView();

        if(CanteenModel.getInstance().getMenus() == null) {
            NetworkHandler.getInstance().fetchCanteenData();
        }

        return mView;
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

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        menu.clear();
        inflater.inflate(R.menu.main, menu);

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
            final Intent intent = new Intent(getActivity(), SettingsActivity.class);
            intent.putExtra(SettingsActivity.EXTRA_SETTINGS_ID, Features.FeatureId.CANTEEN);
            startActivity(intent);
            return true;

            //other item
        }
        return super.onOptionsItemSelected(_item);
    }

    @Override
    public void onRestoreActionBar(final ActionBar _ActionBar) {
        super.onRestoreActionBar(_ActionBar);
        _ActionBar.setTitle(UserSettings.getInstance().getChosenCanteenName());
    }

    private CanteenView mView;
}
