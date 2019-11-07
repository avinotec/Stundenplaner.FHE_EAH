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

package de.fhe.fhemobile.fragments.mensa;

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
import de.fhe.fhemobile.models.mensa.MensaFoodModel;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.utils.UserSettings;
import de.fhe.fhemobile.utils.feature.FeatureId;
import de.fhe.fhemobile.views.mensa.MensaFoodView;


public class MensaFoodFragment extends FeatureFragment {

    private MensaFoodView mView = null;

    public MensaFoodFragment() {
        // Required empty public constructor
    }

    public static MensaFoodFragment newInstance() {
        MensaFoodFragment fragment = new MensaFoodFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (getArguments() != null) {
//
//        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (MensaFoodView) inflater.inflate(R.layout.fragment_mensa_food, container, false);
        mView.initView(null);

        if(MensaFoodModel.getInstance().getFoodItems() == null) {
            NetworkHandler.getInstance().fetchMensaData();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        menu.clear();
        inflater.inflate(R.menu.main, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    //onOptionsItemSelected-------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem _item) {
        switch (_item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                intent.putExtra(SettingsActivity.EXTRA_SETTINGS_ID, FeatureId.MENSA);
                startActivity(intent);
                return true;

            //other item
            default:
                return super.onOptionsItemSelected(_item);
        }
    }

    @Override
    public void onRestoreActionBar(ActionBar _ActionBar) {
        super.onRestoreActionBar(_ActionBar);
        _ActionBar.setTitle(UserSettings.getInstance().getChosenMensaName());
    }
}
