/*
 * Copyright (c) 2014-2019 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fhe.fhemobile.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.fragments.NavigationDrawerFragment;
import de.fhe.fhemobile.utils.Utils;
import de.fhe.fhemobile.utils.feature.FeatureFragmentFactory;
import de.fhe.fhemobile.utils.feature.FeatureProvider;
import de.fhe.fhemobile.vos.timetable.FlatDataStructure;

public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private static final String TAG = "MainActivity";

	public static List<FlatDataStructure> selectedLessons = new ArrayList();
	public static List<FlatDataStructure> sortedLessons=new ArrayList<>();
	public static List<FlatDataStructure> completeLessons = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar    = (Toolbar)     findViewById(R.id.toolbar);
        
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                String token;
                try {
                    token = task.getResult().getToken();
                } catch (final NullPointerException npe )
                {
                    Log.d(TAG, "onComplete: instance missing");
                    return;
                } catch (final RuntimeExecutionException ree )
                {
                    Log.e(TAG, "Error bei der Registrierung am FirebaseServer: ",ree);
                    return;
                }
                Log.d(TAG, "onComplete: Token: "+token);
                firebaseToken=token;
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        restoreActionBar();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position, int id) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        if(mCurrentFragmentId != id) {
            mCurrentFragmentId = id;

            mTitle           = FeatureProvider.getFeatureTitle(id);
            mCurrentFragment = FeatureFragmentFactory.getFeaturedFragment(id);

            fragmentManager.beginTransaction()
                    .replace(R.id.container, mCurrentFragment)
                    .commit();

            Utils.hideKeyboard(this);
            if (getSupportActionBar() != null) {
                restoreActionBar();
            }
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);

        if (mCurrentFragment != null) {
            mCurrentFragment.onRestoreActionBar(actionBar);
        }
    }

    public void changeFragment(FeatureFragment _Fragment, boolean _AddToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, _Fragment);
        mCurrentFragment = _Fragment;
//        transaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.abc_fade_out);

        if (_AddToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            restoreActionBar();
        }
        return super.onCreateOptionsMenu(menu);
    }

    // #############################################################################################

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    
    private Toolbar         mToolbar;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence    mTitle;
    private int             mCurrentFragmentId = -1;
    private FeatureFragment mCurrentFragment;
    private static String firebaseToken;

    public static String getFirebaseToken() {
        return firebaseToken;
    }
}
