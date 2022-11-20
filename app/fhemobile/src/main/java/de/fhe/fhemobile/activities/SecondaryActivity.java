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

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import de.fhe.fhemobile.R;

/**
 * This activity should be implemented by every activity (that is not the {@link MainActivity}),
 * and which uses the toolbar.
 * With {@link SecondaryActivity#setContent(int)} the corresponding activity layout should be provided.
 *
 * Updated and cleaned by Nadja - 28.10.2022
 */
public abstract class SecondaryActivity extends AppCompatActivity {

    private static final String TAG = SecondaryActivity.class.getSimpleName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_secondary);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_secondary_activity);
        mContainer = (FrameLayout) findViewById(R.id.container_secondary_activity);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected void setContent(final int _layoutResource) {
        final View mContent = View.inflate(this, _layoutResource, null);
        mContainer.addView(mContent);
    }

    protected void setFragment(final Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_secondary_activity, fragment)
                .commit();
    }


    protected Toolbar mToolbar;

    private FrameLayout mContainer;
}
