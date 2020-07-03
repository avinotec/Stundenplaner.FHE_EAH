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

import android.app.AlertDialog;
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
import com.google.gson.Gson;

import org.junit.Assert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.fragments.NavigationDrawerFragment;
import de.fhe.fhemobile.models.timeTableChanges.RequestModel;
import de.fhe.fhemobile.models.timeTableChanges.ResponseModel;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.utils.Utils;
import de.fhe.fhemobile.utils.feature.FeatureFragmentFactory;
import de.fhe.fhemobile.utils.feature.FeatureProvider;
import de.fhe.fhemobile.views.timetable.MyTimeTableView;
import de.fhe.fhemobile.vos.timetable.FlatDataStructure;
import de.fhe.fhemobile.vos.timetable.TimeTableEventVo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private static final String TAG = "MainActivity";

	public static List<FlatDataStructure> selectedLessons = new ArrayList();
	public static List<FlatDataStructure> sortedLessons=new ArrayList<>();
	public static List<FlatDataStructure> completeLessons = new ArrayList<>();
    private final int CHANGEREASON_EDIT = 1;
    private final int CHANGEREASON_NEW = 3;
    private final int CHANGEREASON_DELETE = 2;


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

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            final RequestModel request = new RequestModel(RequestModel.ANDROID_DEVICE, MainActivity.getFirebaseToken(),new Date().getTime()-86400000);
            String title="";
            String setID="";

            String json=request.toJson();

            NetworkHandler.getInstance().getTimeTableChanges( json, new Callback<ResponseModel>() {
                @Override
                public void onResponse(final Call<ResponseModel> call, final Response<ResponseModel> response) {

                    Assert.assertTrue( response != null );
                    //wieso assert und damit einen Absturz produzieren, wenn das einfach auftreten kann, wenn der Server nicht verfügbar ist?
                    //vorallem wenn darunter eh ein if das gleiche abfragt.
//				Assert.assertTrue( response.body() != null );

                    //DEBUG
                    if ( response.body() == null )
                    {
                        // Da ist ein Fehler in der Kommunikation
                        // 400: Bad request
                        if ( response.code() == 400 )
                        {
                            String sErrorText = response.errorBody().toString();
                            Log.d( TAG, "Error in Schedule Change Server: " + sErrorText );

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                            builder1.setMessage("Push Notifications: Error in Schedule Change Server: " + sErrorText);
                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        }

                        // nothing to do now....
                        return;
                    }


                    final Gson gson = new Gson();
                    final String json = gson.toJson(response.body());
                    Assert.assertTrue( !json.isEmpty() );

                    Log.d(TAG, "onResponse: "+response.raw().request().url());
                    Log.d(TAG, "onResponse code: "+response.code()+" geparsed: "+ json );

                    final List<ResponseModel.Change> changes = response.body().getChanges();

                    final List<String[]> negativeList = MyTimeTableView.generateNegativeLessons();
                    final Iterator<ResponseModel.Change> iterator= changes.iterator();

                    while(iterator.hasNext()){

                        final ResponseModel.Change change=iterator.next();
                        boolean isInNegativeList=false;
                        for(String[] negativeEvent:negativeList){
                            if(change.getNewEventJson().getTitle().contains(negativeEvent[0])
                                    && change.getSetSplusKey().equals(negativeEvent[1])){
                                isInNegativeList=true;
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

//                new AlertDialog.Builder(MyTimeTableFragment.this.getActivity())
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


                    for(ResponseModel.Change change : changes){

                        // Shortcut to the list
                        final List<FlatDataStructure> myTimetableList = MyTimeTableView.getLessons();

                        //Aenderung eines Events: suche den Event und ueberschreibe seine Daten
                        if(change.getChangesReason()==CHANGEREASON_EDIT) {
                            final FlatDataStructure event = FlatDataStructure.getEventByID( myTimetableList, change.getNewEventJson().getUid());
                            if(event!=null){
                                event.setEvent(change.getNewEventJson());
                            }
                        }
                        //Hinzufuegen eines neuen Events: Erstelle ein neues Element vom Typ FlatDataStructure, schreibe alle Set-, Semester- und Studiengangdaten in diesen
                        //und fuege dann die Eventdaten des neuen Events hinzu. Anschliessend in die Liste hinzufuegen.
                        if(change.getChangesReason()==CHANGEREASON_NEW) {
                            final FlatDataStructure event = FlatDataStructure.queryGetEventsByStudyGroupTitle( myTimetableList, change.getSetSplusKey()).get(0).copy();
                            event.setEvent(change.getNewEventJson());
                            MyTimeTableView.getLessons().add(event);

                        }
                        //Loeschen eines Events: Suche den Event mit der SplusID und lösche ihn aus der Liste.
                        if(change.getChangesReason()==CHANGEREASON_DELETE){
                            final FlatDataStructure event = FlatDataStructure.getEventByID( myTimetableList, change.getNewEventJson().getUid());
                            MyTimeTableView.getLessons().remove(event);
                        }
                    }

                }

                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    Log.d(TAG, "onFailure: "+t.toString());
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

    private void restoreActionBar() {
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
//Todo: Wenn ein eintrag im Backstack ist, dann eine andere Rubrik ausgewählt wird (z.B. news) und dann back-taste gedrueckt wird, überlagern sich die layouts.
//An dieser Stelle muss dann der BackStack geleert werden, wenn man wechselt.
// else{
//            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//        }

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

    //gehe durch die Liste, bis die Startzeit eines Events größer ist als die angegebene Zeit und nehme den vorherigen Event
    //und gebe den Index zurück.
    public static int getCurrentEventIndex(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy H:mm");

        for(int i=0;i<sortedLessons.size();i++){
            TimeTableEventVo event = sortedLessons.get(i).getEvent();
            try {
                if(sdf.parse(event.getDate()+" "+event.getStartTime()).compareTo(new Date())==1){
                    if(sdf.parse(event.getDate()+" "+event.getEndTime()).compareTo(new Date())==-1){
                        return (i);
                    }
                    if(i==0){
                        return i;
                    }
                    return (i-1);
                }
            } catch (ParseException e) {
                Log.e(TAG, "getCurrentEventIndex: ",e );
            }
        }
        return -1;
    }
}
