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

import static de.fhe.fhemobile.Main.getAllSubscribedTimeTableEvents;
import static de.fhe.fhemobile.Main.getAppContext;
import static de.fhe.fhemobile.Main.getSubscribedEventSeries;
import static de.fhe.fhemobile.Main.subscribedEventSeries;
import static de.fhe.fhemobile.utils.Define.MyTimeTable.PREF_SUBSCRIBED_COURSES;
import static de.fhe.fhemobile.utils.Define.MyTimeTable.SP_MYTIMETABLE;
import static de.fhe.fhemobile.utils.Utils.correctUmlauts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import de.fhe.fhemobile.BuildConfig;
import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.mytimetable.MyTimeTableCalendarAdapter;
import de.fhe.fhemobile.adapters.mytimetable.MyTimeTableSettingsAdapter;
import de.fhe.fhemobile.fragments.DrawerFragment;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.fragments.events.EventsWebViewFragment;
import de.fhe.fhemobile.fragments.imprint.ImprintFragment;
import de.fhe.fhemobile.fragments.joboffers.JobOffersFragment;
import de.fhe.fhemobile.fragments.news.NewsWebViewFragment;
import de.fhe.fhemobile.fragments.semesterdata.SemesterDataWebViewFragment;
import de.fhe.fhemobile.models.timeTableChanges.RequestModel;
import de.fhe.fhemobile.models.timeTableChanges.ResponseModel;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.services.PushNotificationService;
import de.fhe.fhemobile.utils.Define;
import de.fhe.fhemobile.utils.Utils;
import de.fhe.fhemobile.utils.feature.FeatureFragmentFactory;
import de.fhe.fhemobile.utils.feature.FeatureProvider;
import de.fhe.fhemobile.views.mytimetable.MyTimeTableSettingsView;
import de.fhe.fhemobile.vos.mytimetable.MyTimeTableEventSeriesVo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements DrawerFragment.NavigationDrawerCallbacks {

    static final String TAG = MainActivity.class.getSimpleName();

    private static final int CHANGEREASON_EDIT = 1;
    private static final int CHANGEREASON_NEW = 3;
    private static final int CHANGEREASON_DELETE = 2;

    public static MyTimeTableCalendarAdapter myTimeTableCalendarAdapter;
    public static MyTimeTableSettingsAdapter myTimeTableSettingsAdapter;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) setSupportActionBar(mToolbar);
        
        mDrawerFragment = (DrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();


        myTimeTableSettingsAdapter = new MyTimeTableSettingsAdapter(
                Main.getAppContext(), subscribedEventSeries);

        myTimeTableCalendarAdapter = new MyTimeTableCalendarAdapter();
        myTimeTableCalendarAdapter.setItems(getAllSubscribedTimeTableEvents());


        // Set up the drawer.
        mDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        /*
        @Override
        FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener() {

//old
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
*/


        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            //Push-Notification
            final RequestModel request = new RequestModel(RequestModel.ANDROID_DEVICE,
                    PushNotificationService.getFirebaseToken(),
                    new Date().getTime() - 86400000);

            final String json = request.toJson();

            NetworkHandler.getInstance().getTimeTableChanges(json, new Callback<ResponseModel>() {
                /**
                 *
                 * @param call
                 * @param response
                 */
                @Override
                public void onResponse(final Call<ResponseModel> call,
                                       final Response<ResponseModel> response) {

                    //wieso assert und damit einen Absturz produzieren, wenn das einfach auftreten kann, wenn der Server nicht verfügbar ist?
                    //vorallem wenn darunter eh ein if das gleiche abfragt.
                    //Assert.assertTrue( response != null );
                    //Assert.assertTrue( response.body() != null );

                    //DEBUG
                    if ( /*response == null || "always false" */ response.body() == null )
                    {
                        // Da ist ein Fehler in der Kommunikation
                        // 400: Bad request
                        if ( /* response != null && "always true" */ response.code() == 400 )
                        {
                            final String sErrorText = response.errorBody().toString();
                            Log.d( TAG, "Error in Schedule Change Server: " + sErrorText );

                            final AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                            builder1.setMessage( "Push Notifications: Error in Schedule Change Server: " + sErrorText);
                            final AlertDialog alert11 = builder1.create();
                            alert11.show();
                        }

                        // nothing to do now....
                        return;
                    }


                    final Gson gson = new Gson();
                    final String json = gson.toJson(response.body());
                    if (BuildConfig.DEBUG) Assert.assertFalse(json.isEmpty());

                    Log.d(TAG, "onResponse: " + response.raw().request().url());
                    Log.d(TAG, "onResponse code: " + response.code() + " geparsed: " + json );

                    final List<ResponseModel.Change> changes = response.body().getChanges();

                    final List<String[]> negativeList = MyTimeTableSettingsView.generateNegativeLessons();
                    final Iterator<ResponseModel.Change> iterator = changes.iterator();

                    while(iterator.hasNext()){

                        final ResponseModel.Change change = iterator.next();
                        boolean isInNegativeList = false;
                        for(final String[] negativeEvent : negativeList){
                            if ( change.getNewEventJson().getGuiTitle().contains(negativeEvent[0])
                                    && change.getSetSplusKey().equals( negativeEvent[1] ) ) {
                                isInNegativeList = true;
                                break;
                            }
                        }

                        if(isInNegativeList){
                            iterator.remove();
                        }
                    }

//				String changesAsString="";
//				for(ResponseModel.Change change: changes){
//					changesAsString+=(change.getChangesReasonText()+"/n/n");
//				}

//                new AlertDialog.Builder(MyTimeTableSettingsFragment.this.getActivity())
//                        .setTitle("Änderungen")
//                       // .setMessage(changesAsString)
//                        .setMessage("test")
//
//                        // Specifying a listener allows you to take an action before dismissing the dialog.
//                        // The dialog is automatically dismissed when a dialog button is clicked.
//                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                // Continue with delete operation
//                            }
//                        })
//                        .create()
//
//                        // A null listener allows the button to dismiss the dialog and take no further action.
//                        .show();

                    //folgender Code basiert auf generateNegativeList welche wie es Änderungen der Veranstaltungen in subscribedEventSeries berücksichtigt

                    //todo: auskommentiert im Zuge von Umbauarbeiten
                    //TODO PushNotifications
//                    for(final ResponseModel.Change change : changes){
//
//                        // Shortcut to the list
//                        final List<MyTimeTableEventSetVo> myTimetableList = subscribedEventSeries;
//
//                        //Aenderung einer Veranstaltung: suche das Event (= einzelne Veranstaltung) und ueberschreibe ihre Daten
//                        if(change.getChangesReason() == CHANGEREASON_EDIT) {
//                            final MyTimeTableEventSetVo event = MyTimeTableUtils.getEventByID(
//                                    myTimetableList, change.getNewEventJson().getId());
//                            if(event != null){
//
//                               event.setEvent(change.getNewEventJson());
//                            }
//                        }
//                        //Hinzufuegen einer neuen veranstaltung:
//                        // Erstelle ein neues Element vom Typ MyTimeTableEventSetVo, schreibe alle Set-, Semester- und Studiengangdaten in diesen
//                        //und fuege dann die Eventdaten des neuen Events hinzu. Anschliessend in die Liste hinzufuegen.
//                        if(change.getChangesReason() == CHANGEREASON_NEW) {
//                            final MyTimeTableEventSetVo event =
//                                    MyTimeTableUtils.getCoursesByStudyGroupTitle(
//                                            myTimetableList, change.getSetSplusKey()).get(0).copy();
//
//                            event.setEvent(change.getNewEventJson());
//
//                            //todo: bad static use to update MyTimeTableCalendar
//                            //MyTimeTableCalendarAdapter.addCourseAndUpdateSharedPreferences(event);
//
//                        }
//                        //Loeschen einer Veranstaltung: Suche die Veranstaltung mit der SplusID und lösche sie aus der Liste.
//                        if(change.getChangesReason() == CHANGEREASON_DELETE){
//                            final MyTimeTableEventSetVo event = MyTimeTableUtils.getEventByID(
//                                    myTimetableList, change.getNewEventJson().getId());
//
//                            //todo: bad static use to update MyTimeTableCalendar
//                            //MyTimeTableCalendarAdapter.removeCourseAndUpdateSharedPreferences(event);
//                        }
//                    }

                }

                @Override
                public void onFailure(final Call<ResponseModel> call, final Throwable t) {
                    Log.d(TAG, "onFailure: "+ t);
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
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
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
        } else
            super.onBackPressed();

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
     * @param course
     */
    public static void addToSubscribedEventSeriesAndUpdateAdapters(final MyTimeTableEventSeriesVo course){
        course.setSubscribed(true);
        subscribedEventSeries.add(course);

        saveSubscribedEventSeriesToSharedPreferences();

        myTimeTableCalendarAdapter.setItems(getAllSubscribedTimeTableEvents());
        myTimeTableCalendarAdapter.notifyDataSetChanged();
        myTimeTableSettingsAdapter.notifyDataSetChanged();
    }


    public static void removeFromSubscribedEventSeriesAndUpdateAdapters(final MyTimeTableEventSeriesVo course){
        course.setSubscribed(false);
        subscribedEventSeries.remove(course);

        saveSubscribedEventSeriesToSharedPreferences();

        myTimeTableCalendarAdapter.setItems(getAllSubscribedTimeTableEvents());
        myTimeTableCalendarAdapter.notifyDataSetChanged();
        myTimeTableSettingsAdapter.notifyDataSetChanged();
    }


    public static void clearSubscribedEventSeriesAndUpdateAdapters(){
        subscribedEventSeries.clear();

        saveSubscribedEventSeriesToSharedPreferences();

        myTimeTableCalendarAdapter.setItems(getAllSubscribedTimeTableEvents());
        myTimeTableCalendarAdapter.notifyDataSetChanged();
        myTimeTableSettingsAdapter.notifyDataSetChanged();
    }

    private static void saveSubscribedEventSeriesToSharedPreferences() {
        final Gson gson = new Gson();
        final String json = correctUmlauts(gson.toJson(getSubscribedEventSeries(), ArrayList.class));
        final SharedPreferences sharedPreferences = getAppContext().getSharedPreferences(SP_MYTIMETABLE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_SUBSCRIBED_COURSES, json);
        editor.apply();
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
