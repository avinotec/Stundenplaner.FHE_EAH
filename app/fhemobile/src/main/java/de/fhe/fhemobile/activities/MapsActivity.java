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
package de.fhe.fhemobile.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.maps.MapsFragment;
import de.fhe.fhemobile.models.maps.MapsModel;

/**
 * Activity for showing and navigating through maps/floorplans of a certain building
 *
 * Edit by Nadja 02.12.2021: renamed from MapsSingleActivity to MapsActivity
 */
public class MapsActivity extends SecondaryActivity {
    public static final String EXTRA_MAP_ID = "extraMapId";

    private static final String TAG = MapsActivity.class.getSimpleName();
    public static final String STATE_MAPS_ID = "stateMapsId";


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent(R.layout.activity_maps);

        if (savedInstanceState != null) {
            mMapId = savedInstanceState.getInt(STATE_MAPS_ID);
        } else {
            final Intent intent = getIntent();
            mMapId = intent.getIntExtra(EXTRA_MAP_ID, -1);
        }

        try {
            setFragment(MapsFragment.newInstance(mMapId));
            final String title = getResources().getString(MapsModel.getInstance().getMaps().get(mMapId).getNameID());
            getSupportActionBar().setTitle(title);
        } catch (final Resources.NotFoundException e){
            Log.e(TAG, "Fehler beim Aufrufen des Fragments: ", e);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_MAPS_ID, mMapId);
    }

    private Integer             mMapId;

}
