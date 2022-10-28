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
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.canteen.CanteenSettingsFragment;
import de.fhe.fhemobile.fragments.myschedule.MyScheduleSettingsFragment;
import de.fhe.fhemobile.fragments.news.NewsCategoriesFragment;
import de.fhe.fhemobile.utils.feature.Features;

public class SettingsActivity extends BaseActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent(R.layout.activity_settings);

        if (savedInstanceState == null) {
            final Intent intent = getIntent();
            mSettingsId = intent.getIntExtra(EXTRA_SETTINGS_ID, -1);
        }

        loadFragments(mSettingsId);
    }

    public void loadFragments(final int _Id) {
        switch(_Id) {

            case Features.FeatureId.NEWS:
                setFragment(NewsCategoriesFragment.newInstance());
                break;

            case Features.FeatureId.CANTEEN:
                setFragment(CanteenSettingsFragment.newInstance());
                break;
            case Features.FeatureId.MYSCHEDULE:
                setFragment(MyScheduleSettingsFragment.newInstance());
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

    public static final String  EXTRA_SETTINGS_ID  = "extraSettingsId";
    private static final String STATE_SETTINGS_ID  = "settingsId";

    private int mSettingsId;

}
