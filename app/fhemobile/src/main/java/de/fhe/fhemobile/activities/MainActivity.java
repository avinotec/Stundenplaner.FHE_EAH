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

import static de.fhe.fhemobile.Main.getAppContext;
import static de.fhe.fhemobile.Main.getEventsOfAllSubscribedEventSeries;
import static de.fhe.fhemobile.Main.getSubscribedEventSeries;
import static de.fhe.fhemobile.Main.setLastUpdateSubscribedEventSeries;
import static de.fhe.fhemobile.Main.subscribedEventSeries;
import static de.fhe.fhemobile.utils.Define.MySchedule.PREF_SUBSCRIBED_EVENTSERIES;
import static de.fhe.fhemobile.utils.Define.MySchedule.SP_MYSCHEDULE;
import static de.fhe.fhemobile.utils.Utils.correctUmlauts;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.fhe.fhemobile.BuildConfig;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.myschedule.MyScheduleCalendarAdapter;
import de.fhe.fhemobile.adapters.myschedule.MyScheduleSettingsAdapter;
import de.fhe.fhemobile.fragments.DrawerFragment;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.fragments.events.EventsWebViewFragment;
import de.fhe.fhemobile.fragments.imprint.ImprintFragment;
import de.fhe.fhemobile.fragments.joboffers.JobOffersFragment;
import de.fhe.fhemobile.fragments.news.NewsWebViewFragment;
import de.fhe.fhemobile.fragments.semesterdata.SemesterDataWebViewFragment;
import de.fhe.fhemobile.services.PushNotificationService;
import de.fhe.fhemobile.utils.Define;
import de.fhe.fhemobile.utils.Utils;
import de.fhe.fhemobile.utils.feature.FeatureFragmentFactory;
import de.fhe.fhemobile.utils.feature.FeatureProvider;
import de.fhe.fhemobile.views.myschedule.MyScheduleCalendarView;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventSeriesVo;

public class MainActivity extends AppCompatActivity implements DrawerFragment.NavigationDrawerCallbacks {

    static final String TAG = MainActivity.class.getSimpleName();

    public static MyScheduleCalendarAdapter myScheduleCalendarAdapter;
    public static MyScheduleSettingsAdapter myScheduleSettingsAdapter;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) setSupportActionBar(mToolbar);
        
        mDrawerFragment = (DrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();


        myScheduleSettingsAdapter = new MyScheduleSettingsAdapter(
                this, subscribedEventSeries);

        myScheduleCalendarAdapter = new MyScheduleCalendarAdapter();
        myScheduleCalendarAdapter.setItems(getEventsOfAllSubscribedEventSeries());


        // Set up the drawer.
        mDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        //Google Play Services needed for Firebase Messaging
        final GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        final int googlePlayServicesCheck =
                googleApiAvailability.isGooglePlayServicesAvailable(this);

        switch (googlePlayServicesCheck){
            case ConnectionResult.SUCCESS:
                break;
            case ConnectionResult.SERVICE_MISSING:
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
            case ConnectionResult.SERVICE_DISABLED:
                if(googleApiAvailability.isUserResolvableError(googlePlayServicesCheck)){
                    //display a dialog that allows users to download the APK from the
                    // Google Play Store or enable it in the device's system settings.
                    final Dialog dialog = googleApiAvailability.getErrorDialog(this, googlePlayServicesCheck, 9000);
                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(final DialogInterface dialogInterface) {
                            MainActivity.this.finish();
                        }
                    });
                    dialog.show();
                } else {
                    Toast.makeText(this, "This device is not supported.",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
        }

        if (Define.ENABLE_PUSHNOTIFICATIONS){
            //code from Firebase Documentation
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull final Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                return;
                            }

                            // Get new FCM registration token
                            final String token = task.getResult();
                            PushNotificationService.setFcmToken(token);

                            Log.d(TAG, "Firebase Token: " + token);
                        }
                    });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        restoreActionBar();
    }

    @Override
    public void onNavigationDrawerItemSelected(final int id) {
        // update the main content by replacing fragments
        final FragmentManager fragmentManager = getSupportFragmentManager();

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

    private void restoreActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);

        if (mCurrentFragment != null) {
            mCurrentFragment.onRestoreActionBar(actionBar);
        }
    }

    /**
     *
     * @param _Fragment the new fragment
     * @param _AddToBackStack if false, then the fragment gets destroyed when removed/replaced sometime
     * @param _Tag tag of the new fragment
     */
    public void changeFragment(final FeatureFragment _Fragment, final boolean _AddToBackStack, final String _Tag) {

        mCurrentFragment = _Fragment;

        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, _Fragment, _Tag);

//        transaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.abc_fade_out);

        if (_AddToBackStack) {
            transaction.addToBackStack(_Tag);
        }

        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (mCurrentFragment != null){
            WebView webview = null;

            //check if currentFragment contains a webview
            if(mCurrentFragment instanceof NewsWebViewFragment){
                webview = ((NewsWebViewFragment) mCurrentFragment).getWebView();
            }else if( mCurrentFragment instanceof SemesterDataWebViewFragment){
                webview = ((SemesterDataWebViewFragment) mCurrentFragment).getWebView();
            }else if (mCurrentFragment instanceof ImprintFragment){
                webview = ((ImprintFragment) mCurrentFragment).getWebView();
            }else if(mCurrentFragment instanceof EventsWebViewFragment){
                webview = ((EventsWebViewFragment) mCurrentFragment).getWebView();
            }else if(mCurrentFragment instanceof JobOffersFragment){
                webview = ((JobOffersFragment) mCurrentFragment).getWebView();
            }
            if(webview != null && webview.canGoBack()){
                // if there is previous page open it
                webview.goBack();
                return;
            }

            final FragmentManager fragmentManager = getSupportFragmentManager();

            //if there is no previous page, close app
            /*
             * Zum Beenden der App muss zwei Mal zurück gedrückt werden.
             ' Damit wird vermieden, dass man aus Versehen beim Klicken auf "zurück" aus der App herausgeht.
             */
            if (backPressedTwice || fragmentManager.getBackStackEntryCount() > 0) {

                // we go really one step back and if back pressed twice, destroy the app
                super.onBackPressed();
                return;
            }

            this.backPressedTwice = true;
            Toast.makeText(this, getString(R.string.reallyClose), Toast.LENGTH_SHORT).show();

            mHandler.postDelayed(mRunnable, Define.APP_CLOSING_DOUBLECLICK_DELAY_TIME);

        } else super.onBackPressed();

    }

    /**
    Hier wird dafuer gesorgt, dass wenn in der Root-Activity der Back-Button gedrückt wird, erst beim 2. Mal Back-Button die App geschlossen wird.
    Die Variable backPressedTwice wird beim ersten Betätigen gesetzt und es wird ein Thread verzögert gestartet. In dem Thread wird die Variable
    wieder zurückgesetzt. Nur wenn "backPressedTwice" gesetzt ist, und der Back-Button ein zweites Mal betätigt wird, wird die App beendet.
    APP_CLOSING_DOUBLECLICK_DELAY_TIME (Standard 2000 ms)
    */
    boolean backPressedTwice = false;
    private final Handler mHandler = new Handler();

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            backPressedTwice = false;
        }
    };



    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        if (!mDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            restoreActionBar();
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     *
     * @param eventSeries
     */
    public static void addToSubscribedEventSeriesAndUpdateAdapters(final MyScheduleEventSeriesVo eventSeries){
        eventSeries.setSubscribed(true);
        subscribedEventSeries.add(eventSeries);

        saveSubscribedEventSeriesToSharedPreferences();

        myScheduleCalendarAdapter.setItems(getEventsOfAllSubscribedEventSeries());
        myScheduleCalendarAdapter.notifyDataSetChanged();
        myScheduleSettingsAdapter.notifyDataSetChanged();
    }


    public static void removeFromSubscribedEventSeriesAndUpdateAdapters(final MyScheduleEventSeriesVo eventSeries){
        eventSeries.setSubscribed(false);
        subscribedEventSeries.remove(eventSeries);

        saveSubscribedEventSeriesToSharedPreferences();

        myScheduleCalendarAdapter.setItems(getEventsOfAllSubscribedEventSeries());
        myScheduleCalendarAdapter.notifyDataSetChanged();
        myScheduleSettingsAdapter.notifyDataSetChanged();
    }

    public static void setSubscribedEventSeriesAndUpdateAdapters(final List<MyScheduleEventSeriesVo> newSubscribedEventSeries){
        subscribedEventSeries.clear();
        subscribedEventSeries.addAll(newSubscribedEventSeries);
        setLastUpdateSubscribedEventSeries(new Date());
        MyScheduleCalendarView.setLastUpdatedTextView();

        saveSubscribedEventSeriesToSharedPreferences();

        myScheduleCalendarAdapter.setItems(getEventsOfAllSubscribedEventSeries());
        myScheduleCalendarAdapter.notifyDataSetChanged();
        myScheduleSettingsAdapter.notifyDataSetChanged();
    }


    public static void clearSubscribedEventSeriesAndUpdateAdapters(){
        subscribedEventSeries.clear();
        setLastUpdateSubscribedEventSeries(null);
        MyScheduleCalendarView.setLastUpdatedTextView();

        saveSubscribedEventSeriesToSharedPreferences();

        myScheduleCalendarAdapter.setItems(getEventsOfAllSubscribedEventSeries());
        myScheduleCalendarAdapter.notifyDataSetChanged();
        myScheduleSettingsAdapter.notifyDataSetChanged();
    }

    public static void saveSubscribedEventSeriesToSharedPreferences() {
        final Gson gson = new Gson();
        final String json = correctUmlauts(gson.toJson(getSubscribedEventSeries(), ArrayList.class));
        final SharedPreferences sharedPreferences = getAppContext().getSharedPreferences(SP_MYSCHEDULE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_SUBSCRIBED_EVENTSERIES, json);
        editor.apply();

        // wir geben mal den erhaltenen JSON String aus. Dann können wir sehen, was Carsten uns sendet.
        if (BuildConfig.DEBUG) Log.d("MainAcitivity", "saveSubscribedEventSeriesToSharedPreferences(): received JSON: " + json );
    }


    // #############################################################################################

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private DrawerFragment mDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence    mTitle;
    private int             mCurrentFragmentId = -1;
    private FeatureFragment mCurrentFragment;
}
