/*
 *  Copyright (c) 2023-2023 Ernst-Abbe-Hochschule Jena
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
package de.fhe.fhemobile.fragments.myschedule;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.common.collect.Iterables;

import java.util.HashMap;
import java.util.Map;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.models.myschedule.CalendarModel;
import de.fhe.fhemobile.services.CalendarSynchronizationTask;
import de.fhe.fhemobile.utils.Utils;

public class MySchedulePreferencesFragment extends PreferenceFragmentCompat {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment {@link MySchedulePreferencesFragment}.
     */
    public static MySchedulePreferencesFragment newInstance(){
        final MySchedulePreferencesFragment fragment = new MySchedulePreferencesFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MySchedulePreferencesFragment(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.preferences_visualizer);

        //workaround/helper preference when calendar permissions are not granted
        //reason: the list of the ListPreference's dialog can not be updated once it is opened
        // thus it stays empty till opened again if permissions needed to be requested first
        mNoCalendarPermissionPref = findPreference(getResources().getString(R.string.sp_myschedule_calendar_no_permission));
        mNoCalendarPermissionPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                requestCalendarPermission();
                return true;
            }
        });

        // calendar list preference
        mCalendarListPref = findPreference(getResources().getString(R.string.sp_myschedule_calendar_to_sync));
        //initialize
        mCalendarListPref.setEntries(new CharSequence[0]);
        mCalendarListPref.setEntryValues(new CharSequence[0]);

        mCalendarListPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /**
             * Called when a Preference has been clicked.
             * @param preference The preference that was clicked
             * @return True if the click was handled.
             */
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                populateCalendarList((ListPreference) preference);
                return true;
            }
        });
        //listener for setting summary string
        mCalendarListPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                if(mCalendarListPref.getEntry() != null){
                    preference.setSummary(mCalendarListPref.getEntry());
                } else {
                    preference.setSummary(R.string.myschedule_pref_choose_calendar_summary);
                }
                return false;
            }
        });
        //listener for creating local calendar
        mCalendarListPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            /**
             * Called when a Preference has been changed by the user.
             * This is called before the state of the Preference is about to be updated and before the state is persisted.
             * @param preference The changed preference
             * @param newValue   The new value of the preference
             * @return True to update the state of the Preference with the new value.
             */
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                if(((String) newValue).equals("-1")){
                    //create local calendar and get its id
                    String calId = CalendarModel.getInstance().createLocalCalendar();
                    //update the list preference entries
                    // (the entry "create local calendar" with value -1 is going to be replaced
                    // by the name of the local calendar and its calendar id as values)
                    populateCalendarList((ListPreference) preference);
                    //set the chosen calendar top the local calendar id
                    ((ListPreference) preference).setValue(calId);
                    //return false to prevent the value being updated to -1 instead of the calId
                    return false;
                }
                //set value of the preference to newValue
                else {
                    return true;
                }
            }
        });


        // calendar switch
        mCalendarSyncSwitchPref = findPreference(getResources().getString(R.string.sp_myschedule_enable_calsync));
        mCalendarSyncSwitchPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                boolean syncEnabled = ((SwitchPreferenceCompat) preference).isChecked();

                //no calendar chosen
                if(mCalendarListPref.getValue() == null){
                    mCalendarSyncSwitchPref.setChecked(false);
                    Utils.showToast(R.string.myschedule_calsync_warning);
                }
                //calendar chosen
                else {
                    //toggle synchronisation
                    if(syncEnabled) {
                        CalendarSynchronizationTask.startPeriodicSynchronizing();
                    } else {
                        CalendarSynchronizationTask.stopPeriodicSynchronizing();
                    }
                }
                return false;
            }
        });

        // initialize values
        //set some values if a calendar is already chosen
        if(mCalendarListPref.getValue() != null){
            populateCalendarList();
            mCalendarListPref.setSummary(mCalendarListPref.getEntry());
        }
        //no calendar chosen
        else {
            CalendarSynchronizationTask.stopPeriodicSynchronizing();
            mCalendarSyncSwitchPref.setChecked(false);
        }

        if(!isCalendarPermissionGranted()){
            mNoCalendarPermissionPref.setVisible(true);
            mCalendarListPref.setVisible(false);
            mCalendarSyncSwitchPref.setEnabled(false);
        } else {
            mNoCalendarPermissionPref.setVisible(false);
            mCalendarListPref.setVisible(true);
            mCalendarSyncSwitchPref.setEnabled(true);
        }


        // delete calendar
        mDeleteCalendarPref = findPreference(getResources().getString(R.string.myschedule_pref_delete_calendar));
        mDeleteCalendarPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {

                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.dialog_delete_cal_title)
                        .setMessage(R.string.dialog_delete_cal_message)
                        .setPositiveButton(R.string.dialog_delete_cal_confirm, new DialogInterface.OnClickListener() {

                            public void onClick(final DialogInterface dialog, final int which) {
                                CalendarModel.getInstance().deleteLocalCalendar();
                                mDeleteCalendarPref.setVisible(false);
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //dialog is closed
                            }
                        }).show();

                return false;
            }
        });
        //initialize
        mDeleteCalendarPref.setVisible(false);
    }

    private void populateCalendarList(){
        populateCalendarList(mCalendarListPref);
    }

    /**
     * Set calendar options (displayed name and value/id saved to shared preferences)
     */
    private void populateCalendarList(ListPreference preference){
        Map<String, Long> availableCalendars = CalendarModel.getInstance().getCalendars();

        if(availableCalendars == null){
            availableCalendars = new HashMap<>();
        }

        //option to create a new local calendar
        if(CalendarModel.getInstance().getLocalCalendarId() == null){
            availableCalendars.put(getResources().getString(R.string.myschedule_create_local_calendar), -1l);
        } else {
            mDeleteCalendarPref.setVisible(true);
        }

        //the human-readable entries to be shown in the list
        preference.setEntries(Iterables.toArray(availableCalendars.keySet(), String.class));
        //array to find the value to save for a preference when an entry from entries is selected
        CharSequence[] values = new CharSequence[availableCalendars.values().size()];
        int i = 0;
        for(Long calId : availableCalendars.values()){
            values[i] = String.valueOf(calId);
            i++;
        }
        preference.setEntryValues(values);
    }

    private boolean isCalendarPermissionGranted() {
        return ContextCompat.checkSelfPermission(Main.getAppContext(),
                Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(Main.getAppContext(),
                Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCalendarPermission() {
        requestPermissionLauncher.launch(new String[]{
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR});
    }

    /**
     * Register the permissions callback, which handles the user's response to the
     * system permissions dialog. Save the return value, an instance of
     * ActivityResultLauncher, as an instance variable.
     */
    private final ActivityResultLauncher requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    //permissions are granted
                    if(!result.containsValue(Boolean.FALSE)){
                        mNoCalendarPermissionPref.setVisible(false);
                        mCalendarListPref.setVisible(true);
                        mCalendarSyncSwitchPref.setEnabled(true);
                        populateCalendarList();
                    }
                }
            });

    Preference mNoCalendarPermissionPref;
    ListPreference          mCalendarListPref;
    Preference              mDeleteCalendarPref;
    SwitchPreferenceCompat  mCalendarSyncSwitchPref;
}
