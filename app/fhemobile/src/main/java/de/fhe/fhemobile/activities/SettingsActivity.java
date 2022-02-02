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
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.mensa.MensaSettingsFragment;
import de.fhe.fhemobile.fragments.news.NewsCategoriesFragment;
import de.fhe.fhemobile.utils.feature.Features;

public class SettingsActivity extends BaseActivity {

    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContent(R.layout.activity_settings);

        if (savedInstanceState == null) {
            final Intent intent = getIntent();
            mSettingsId = intent.getIntExtra(EXTRA_SETTINGS_ID, -1);
        }

        loadFragments(mSettingsId);
    }

    public void loadFragments(final Integer _Id) {
        switch(_Id) {
            case Features.FeatureId.NEWS:
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, NewsCategoriesFragment.newInstance())
                        .commit();
                break;
            case Features.FeatureId.MENSA:
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, MensaSettingsFragment.newInstance())
                        .commit();
                break;
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putInt(STATE_SETTINGS_ID, mSettingsId);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(@NonNull final Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        mSettingsId = savedInstanceState.getInt(STATE_SETTINGS_ID);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    public static final String  EXTRA_SETTINGS_ID  = "extraSettingsId";

    private static final String STATE_SETTINGS_ID  = "settingsId";

    private Integer mSettingsId;

}
