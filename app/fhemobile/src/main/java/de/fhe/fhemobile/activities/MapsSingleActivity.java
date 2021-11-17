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
package de.fhe.fhemobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.maps.MapsSingleFragment;
import de.fhe.fhemobile.models.maps.MapsModel;

public class MapsSingleActivity extends BaseActivity {
    private static final String TAG = "MapsSingleActivity";
    public static final String STATE_MAPS_ID = "stateMapsId";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContent(R.layout.activity_maps_single);

        //not used: mModel = MapsModel.getInstance();

        if (savedInstanceState != null) {
            mMapId = savedInstanceState.getInt(STATE_MAPS_ID);
        }
        else {
            final Intent intent = getIntent();
            mMapId = intent.getIntExtra(EXTRA_MAP_ID, -1);
        }

        try {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, MapsSingleFragment.newInstance(mMapId))
                    .commit();

            String title = getResources().getString(MapsModel.getInstance().getMaps().get(mMapId).getNameID());
            getSupportActionBar().setTitle(title);
        }
        catch (final Exception e){
            Log.e(TAG, "Fehler beim Aufrufen des Fragments: ",e );
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_MAPS_ID, mMapId);
    }

    private static final String LOG_TAG = MapsSingleActivity.class.getSimpleName();


    public static final String EXTRA_MAP_ID = "extraMapId";

    // --Commented out by Inspection (02.11.2021 17:02):private MapsModel           mModel;

    private Integer             mMapId;

}
