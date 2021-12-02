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

package de.fhe.fhemobile.fragments.maps;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    public MapsDialogFragment() {
        // Required empty public constructor
    }

    public static MapsDialogFragment newInstance() {
        MapsDialogFragment fragment = new MapsDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (MapsDialogView) inflater.inflate(R.layout.fragment_maps_dialog, container, false);

        mView.initializeView(mViewListener);

        return mView;
    }

    private final MapsDialogView.ViewListener mViewListener = new MapsDialogView.ViewListener() {
        @Override
        public void onMapItemClicked(Integer _Position) {
            Intent intent = new Intent(getActivity(), MapsActivity.class);
            intent.putExtra(MapsActivity.EXTRA_MAP_ID, _Position);
            startActivity(intent);
        }
    };

    private MapsDialogView mView;
}
