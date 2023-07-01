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

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.canteencardbalance.canteencardreader.CardBalance;
import de.fhe.fhemobile.canteencardbalance.canteencardreader.InterCardReader;
import de.fhe.fhemobile.fragments.DrawerFragment;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.fragments.events.EventsWebViewFragment;
import de.fhe.fhemobile.fragments.imprint.ImprintFragment;
import de.fhe.fhemobile.fragments.joboffers.JobOffersFragment;
import de.fhe.fhemobile.fragments.news.NewsWebViewFragment;
import de.fhe.fhemobile.fragments.semesterdates.SemesterDatesWebViewFragment;
import de.fhe.fhemobile.models.canteen.CanteenModel;
import de.fhe.fhemobile.services.CalendarSynchronizationBackgroundTask;
import de.fhe.fhemobile.services.PushNotificationService;
import de.fhe.fhemobile.utils.Define;
import de.fhe.fhemobile.utils.Utils;
import de.fhe.fhemobile.utils.feature.FeatureFragmentFactory;
import de.fhe.fhemobile.utils.feature.FeatureProvider;

public class MainActivity extends AppCompatActivity implements DrawerFragment.NavigationDrawerCallbacks {

    static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Fix: No Network Security Config specified, using platform default - Android Log */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) setSupportActionBar(mToolbar);

        mDrawerFragment = (DrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
        if(mTitle == null){
            mTitle = getTitle();
        }


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
                    Utils.showToast(this, "This device is not supported.");
                    finish();
                }
        }
        if (//if push notifications enabled
                PreferenceManager.getDefaultSharedPreferences(Main.getAppContext())
                        .getBoolean(getResources().getString(R.string.sp_myschedule_enable_fcm), false)){
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

                            //register user at server for subscribed event series
                            //note: this was introduced to ensure registration even when the user has never opened
                            //      My Schedule Dialog or Overview which can happen in SS23 because of the registration
                            //      was missed in My Schedule Dialog for some time
                            PushNotificationService.registerSubscribedEventSeries();
                        }
                    });
        }
        if (//if calendar synchronization enabled
                PreferenceManager.getDefaultSharedPreferences(Main.getAppContext())
                        .getBoolean(getResources().getString(R.string.sp_myschedule_enable_calsync), false)){

            CalendarSynchronizationBackgroundTask.startPeriodicSynchronizing(false);
        }

        try {
            //NFC
            // Use the foreground dispatch system to allow an activity to intercept an intent
            // and claim priority over other activities that handle the same intent.
            nfcPendingIntent = PendingIntent.getActivity(
                    this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                    PendingIntent.FLAG_MUTABLE);
            IntentFilter intentFilter = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
            intentFiltersArray = new IntentFilter[]{intentFilter,};
            techListsArray = new String[][]{new String[]{IsoDep.class.getName()}};
        } catch ( final Exception e )
        {
            Log.d( TAG, "NfcAdapter: Problem mit den NfcAdapter. Wird ignoriert.");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        restoreActionBar();
        try {
            //nfc foreground dispatch system
            final NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if (nfcAdapter != null)
                nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, intentFiltersArray, techListsArray);
        }
        catch ( final Exception e )
        {
            Log.d( TAG, "NfcAdapter: Problem mit den NfcAdapter. Wird ignoriert.");
        }
}

    @Override
    protected void onPause() {
        super.onPause();
        try {
            //nfc foreground dispatch system
            final NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if (nfcAdapter != null)
                nfcAdapter.disableForegroundDispatch(this);
        } catch ( final Exception e )
        {
            Log.d( TAG, "NfcAdapter: Problem mit den NfcAdapter. Wird ignoriert.");
        }

    }

    @Override
    public void onNavigationDrawerItemSelected(final int id) {
        // update the main content by replacing fragments
        final FragmentManager fragmentManager = getSupportFragmentManager();

        if(mCurrentFragmentId != id) {
            mCurrentFragmentId = id;

            mTitle           = FeatureProvider.getFeatureTitle(id);
            mCurrentFragment = FeatureFragmentFactory.getFeatureFragment(id);

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
     */
    public void changeFragment(final FeatureFragment _Fragment, final boolean _AddToBackStack) {
        mCurrentFragment = _Fragment;
        //tag of the new fragment to later find the fragment by tag
        String tag = mCurrentFragment.getFeatureTag();

        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, _Fragment, tag);

//        transaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.abc_fade_out);

        if (_AddToBackStack) {
            transaction.addToBackStack(tag);
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
            } else if( mCurrentFragment instanceof SemesterDatesWebViewFragment){
                webview = ((SemesterDatesWebViewFragment) mCurrentFragment).getWebView();
            } else if (mCurrentFragment instanceof ImprintFragment){
                webview = ((ImprintFragment) mCurrentFragment).getWebView();
            } else if (mCurrentFragment instanceof EventsWebViewFragment){
                webview = ((EventsWebViewFragment) mCurrentFragment).getWebView();
            } else if (mCurrentFragment instanceof JobOffersFragment){
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
            Utils.showToast(this, R.string.reallyClose);

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            CardBalance balance = InterCardReader.getInstance().readTag(tag);
            if (balance != null) {
                Log.d(TAG, "Read canteen card balance: " + balance.toString());

                CanteenModel.getInstance().setCanteenCardBalance(balance);

                Utils.showToast(getResources().getString(R.string.canteen_card_balance_toast, balance.getBalance()));
            } else {
                Log.w(TAG, "Read canteen card balance is null");
            }
        }
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

    private IntentFilter[] intentFiltersArray;
    private String[][] techListsArray;
    PendingIntent nfcPendingIntent;
}
