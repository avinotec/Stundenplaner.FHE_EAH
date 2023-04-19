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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.google.common.collect.Iterables;

import java.util.Map;

import de.fhe.fhemobile.BuildConfig;
import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.events.CalendarSyncEvent;
import de.fhe.fhemobile.events.Event;
import de.fhe.fhemobile.events.EventListener;
import de.fhe.fhemobile.models.myschedule.CalendarModel;
import de.fhe.fhemobile.services.CalendarSynchronizationBackgroundTask;
import de.fhe.fhemobile.utils.Define;
import de.fhe.fhemobile.utils.Utils;

public class MySchedulePreferencesFragment extends PreferenceFragmentCompat {

    final long CALENDAR_ID_RESERVED_LOCAL_CALENDAR = -1L;
    private static final String TAG = MySchedulePreferencesFragment.class.getSimpleName();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment {@link MySchedulePreferencesFragment}.
     */
    public static MySchedulePreferencesFragment newInstance() {
        final MySchedulePreferencesFragment fragment = new MySchedulePreferencesFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MySchedulePreferencesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.preferences_visualizer);
        initializePushNotificationsCategory();

        if (Define.ENABLE_CALENDAR_SYNC) {
            initializeCalendarSynchronizationCategory();
            initializeDeleteCalendarCategory();
        } else {
            PreferenceCategory deleteCalCategory = (PreferenceCategory) findPreference("deleteCalendarCategory");
            deleteCalCategory.setVisible(false);
            PreferenceCategory calSyncCategory = (PreferenceCategory) findPreference("calSyncCategory");
            calSyncCategory.setVisible(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        CalendarModel.getInstance().addListener(
                CalendarSyncEvent.CHOSEN_CALENDAR_DELETED,
                mChosenCalendarDeletedEventListener);
        CalendarModel.getInstance().addListener(
                CalendarSyncEvent.LOCAL_CALENDAR_DELETED,
                mLocalCalendarDeletedEventListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        CalendarModel.getInstance().removeListener(
                CalendarSyncEvent.CHOSEN_CALENDAR_DELETED,
                mChosenCalendarDeletedEventListener);
        CalendarModel.getInstance().removeListener(
                CalendarSyncEvent.LOCAL_CALENDAR_DELETED,
                mLocalCalendarDeletedEventListener);
    }

    private void initializePushNotificationsCategory() {
        SwitchPreferenceCompat notificationPref = (SwitchPreferenceCompat) findPreference(getResources().getString(R.string.sp_myschedule_enable_fcm));
        //workaround to enable push notifications by default:
        // the preference is set to false in XML to avoid push notifications being enabled
        // before the permission at api level 33 could be granted;
        // There should be no problem with enabling notifications not before this code here is run
        // because this code is run before the user can access any fragment with an add-course-to-my-schedule-option
        notificationPref.setChecked(true);

        if (!isNotificationPermissionGranted()) {
            notificationPref.setChecked(false);
        }

        notificationPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                //do not set preference value when permission is not granted
                return isNotificationPermissionGranted();
            }
        });

        notificationPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                if (!isNotificationPermissionGranted()) {
                    requestNotificationPermission();
                }
                return true;
            }
        });
    }

    private void initializeCalendarSynchronizationCategory() {
        //first, find all preference to ensure them being set when used in listeners

        //workaround/helper preference when calendar permissions are not granted
        //reason: the list of the ListPreference's dialog can not be updated once it is opened
        // thus it stays empty till opened again if permissions needed to be requested first
        mCalendarSelectionNoPermissionPref = findPreference(getResources().getString(R.string.myschedule_pref_calsync_no_permission));
        //list of calendars to choose from
        mCalendarSelectionPref = findPreference(getResources().getString(R.string.sp_myschedule_calendar_to_sync_id));
        //"button" to delete the created local calendar
        mDeleteLocalCalendarPref = findPreference(getResources().getString(R.string.myschedule_pref_delete_local_calendar));
        mCalendarSyncSwitchPref = findPreference(getResources().getString(R.string.sp_myschedule_enable_calsync));

        mCalendarSelectionNoPermissionPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                requestCalendarPermission();
                return true;
            }
        });

        // initialize calendar list preference
        mCalendarSelectionPref.setEntries(new CharSequence[0]);
        mCalendarSelectionPref.setEntryValues(new CharSequence[0]);
        //populate list of calendars on click
        mCalendarSelectionPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /**
             * Called when a Preference has been clicked.
             * @param preference The preference that was clicked
             * @return True if the click was handled.
             */
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                populateCalendarSelectionList();
                return true;
            }
        });

        //Set behaviour on when preference value is changed
        // (create local calendar when "create local calendar" is selected as calendar,
        // store name of the selected calendar in parallel - preference only stores calendar id)
        mCalendarSelectionPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            /**
             * Called when a Preference has been changed by the user.
             * This is called before the state of the Preference is about to be updated and before the state is persisted.
             * @param preference The changed preference
             * @param newValue   The new value of the preference
             * @return True to update the state of the Preference with the new value.
             */
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                //NO CALENDAR SELECTED (Auswahl wird aufgehoben)
                if ("".equals((String) newValue)) {
                    mCalendarSelectionPref.setSummary(R.string.myschedule_pref_choose_calendar_summary);
                    PreferenceManager.getDefaultSharedPreferences(Main.getAppContext()).edit()
                            .putString(Main.getAppContext().getResources().getString(R.string.sp_myschedule_calendar_to_sync_name), "").apply();
                    return true;
                }
                //CALENDAR SELECTED
                //another calendar has been chosen before and synchronization is activated
                else if(mCalendarSelectionPref.getEntry() != null && !"".equals(mCalendarSelectionPref.getEntry().toString())
                        && mCalendarSyncSwitchPref.isChecked()){
                    //show warning that calendar selection cannot be changed while sync is on
                    Utils.showToastLong(R.string.myschedule_pref_choose_calendar_warning);
                    return false;
                }
                //"create new local calendar" is chosen
                else if (("" + CALENDAR_ID_RESERVED_LOCAL_CALENDAR).equals((String) newValue)) {
                    //create local calendar and get its id
                    final String calId = CalendarModel.createLocalCalendar();
                    //update the list preference entries
                    // (the entry "create local calendar" with value CREATE_NEW_LOCAL_CALENDAR ("-1") is going to be replaced
                    // by the name of the local calendar and its calendar id as values)
                    populateCalendarSelectionList();
                    //set the chosen calendar top the local calendar id
                    mCalendarSelectionPref.setValue(calId);
                    final String localCalendarName = getResources().getString(R.string.myschedule_calsync_local_calendar_name);
                    PreferenceManager.getDefaultSharedPreferences(Main.getAppContext())
                            .edit()
                            .putString(getResources().getString(R.string.sp_myschedule_calendar_to_sync_name), localCalendarName)
                            .apply();
                    mCalendarSelectionPref.setSummary(localCalendarName);
                    //return false to prevent the value being updated to CREATE_NEW_LOCAL_CALENDAR ("-1") instead of the calId
                    return false;
                }
                //calendar chosen
                // do not set value if entry (name of the chosen calendar) is empty
                else if(mCalendarSelectionPref.getEntry() != null){
                    //set summary, selected calendar name and value to chosen calendar
                    final CharSequence calendarEntry = mCalendarSelectionPref.getEntry();
                    mCalendarSelectionPref.setSummary(calendarEntry);

                    final String calendarEntryString = calendarEntry.toString();
                    PreferenceManager.getDefaultSharedPreferences(Main.getAppContext())
                            .edit()
                            .putString(getResources().getString(R.string.sp_myschedule_calendar_to_sync_name), calendarEntryString)
                            .apply();
                    return true;
                } else {
                    Log.w(TAG, "mCalendarSelectionPref onPrefenceChange: calendar selection is changed but getEntry() is null");
                    return false;
                }
            }
        });


        //delete local calendar preference
        mDeleteLocalCalendarPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                showDeleteLocalCalendarDialog();
                //return true to state click event as processed
                return true;
            }
        });

        // toggle calendar synchronisation
        mCalendarSyncSwitchPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                final boolean syncEnabled = ((SwitchPreferenceCompat) preference).isChecked();

                //no calendar chosen
                if (mCalendarSelectionPref.getValue() == null) {
                    mCalendarSyncSwitchPref.setChecked(false);
                    Utils.showToast(R.string.myschedule_calsync_warning);
                }
                //calendar chosen
                else {
                    //toggle synchronisation
                    if (syncEnabled) {
                        //if debugging: sync immediately when sync button is enabled
                        if (BuildConfig.DEBUG) {
                            CalendarSynchronizationBackgroundTask.sync();
                        }
                        CalendarSynchronizationBackgroundTask.startPeriodicSynchronizing();
                    } else {
                        CalendarSynchronizationBackgroundTask.stopPeriodicSynchronizing();
                        askWhetherToDeleteCalendarEntries();
                    }
                }

                //return true to state click event as processed
                return true;
            }
        });


        //INITIALIZE CALENDAR PREFERENCES VALUES
        //only set to visible if there is a local calendar that can be deleted
        mDeleteLocalCalendarPref.setEnabled(false);
        //if a calendar is already chosen
        if (mCalendarSelectionPref.getValue() != null && !mCalendarSelectionPref.getValue().equals("")) {
            String chosenCalName = PreferenceManager.getDefaultSharedPreferences(Main.getAppContext())
                    .getString(getResources().getString(R.string.sp_myschedule_calendar_to_sync_name), getResources().getString(R.string.myschedule_pref_choose_calendar_summary));
            mCalendarSelectionPref.setSummary(chosenCalName);
            //enable deleting local calendar
            if(chosenCalName.equals(getResources().getString(R.string.myschedule_calsync_local_calendar_name))){
                mDeleteLocalCalendarPref.setEnabled(true);
            }
        }
        //no calendar chosen
        else {
            CalendarSynchronizationBackgroundTask.stopPeriodicSynchronizing();
            mCalendarSyncSwitchPref.setChecked(false);
        }

        //ENABLE CALENDAR PREFERENCES
        //if calendar permission is not granted
        if (!isCalendarPermissionGranted()) {
            //use helper preference that will only launch the permission request
            // without displaying an dialog with an empty calendar list
            mCalendarSelectionNoPermissionPref.setVisible(true);
            mCalendarSelectionPref.setVisible(false);
            mCalendarSyncSwitchPref.setEnabled(false);
        }
        //permission to read and write calendars is granted
        else {
            //do not use the helper preference, but the correct list preference
            // to enable the user to choose a calendar from
            mCalendarSelectionNoPermissionPref.setVisible(false);
            mCalendarSelectionPref.setVisible(true);
            mCalendarSyncSwitchPref.setEnabled(true);
        }
    }

    private void initializeDeleteCalendarCategory() {
        mDeleteCalendarPref = findPreference(getResources().getString(R.string.myschedule_pref_delete_cal));
        mDeleteCalendarNoPermissionPref = findPreference(getResources().getString(R.string.myschedule_pref_delete_cal_no_permission));

        mDeleteCalendarPref.setEntries(new CharSequence[0]);
        mDeleteCalendarPref.setEntryValues(new CharSequence[0]);

        mDeleteCalendarNoPermissionPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                requestCalendarPermission();
                return true;
            }
        });
        mDeleteCalendarPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                populateDeleteCalendarList();
                return true;
            }
        });

        mDeleteCalendarPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                showDeleteCalendarDialog((String) newValue);

                //value of the preference is not set
                return false;
            }
        });

        //if calendar permission is not granted
        if (!isCalendarPermissionGranted()) {
            //use helper preference that will only launch the permission request
            // without displaying an dialog with an empty calendar list
            mDeleteCalendarNoPermissionPref.setVisible(true);
            mDeleteCalendarPref.setVisible(false);
        }
        //permission to read and write calendars is granted
        else {
            //do not use the helper preference, but the correct list preference
            // to enable the user to choose a calendar from
            mDeleteCalendarNoPermissionPref.setVisible(false);
            mDeleteCalendarPref.setVisible(true);
        }
    }

    /**
     * Set calendar options (displayed name and value/id saved to shared preferences) for calendar synchronization
     */
    private void populateCalendarSelectionList() {
        Map<String, Long> availableCalendars = CalendarModel.getCalendars();

        //option to create a new local calendar
        if (CalendarModel.getLocalCalendarId() == null) {
            availableCalendars.put(getResources().getString(R.string.myschedule_create_local_calendar), CALENDAR_ID_RESERVED_LOCAL_CALENDAR);
        } else {
            mDeleteLocalCalendarPref.setEnabled(true);
        }

        //the human-readable entries to be shown in the list
        mCalendarSelectionPref.setEntries(Iterables.toArray(availableCalendars.keySet(), String.class));
        //array to find the value to save for a preference when an entry from entries is selected
        CharSequence[] values = new CharSequence[availableCalendars.values().size()];
        int i = 0;
        for (Long calId : availableCalendars.values()) {
            values[i] = String.valueOf(calId);
            i++;
        }
        mCalendarSelectionPref.setEntryValues(values);
    }

    /**
     * Set calendar options (displayed name and value/id saved to shared preferences) for deleting calendars
     */
    private void populateDeleteCalendarList() {
        Map<String, Long> availableCalendars = CalendarModel.getCalendars();

        //the human-readable entries to be shown in the list
        mDeleteCalendarPref.setEntries(Iterables.toArray(availableCalendars.keySet(), String.class));
        //array to find the value to save for a preference when an entry from entries is selected
        CharSequence[] values = new CharSequence[availableCalendars.values().size()];
        int i = 0;
        for (Long calId : availableCalendars.values()) {
            values[i] = String.valueOf(calId);
            i++;
        }
        mDeleteCalendarPref.setEntryValues(values);
    }

    private void showDeleteLocalCalendarDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_delete_local_cal_title)
                .setMessage(R.string.dialog_delete_local_cal_message)
                .setPositiveButton(R.string.dialog_delete_cal_confirm, new DialogInterface.OnClickListener() {

                    public void onClick(final DialogInterface dialog, final int which) {
                        //note: access preferences instead of using mCalendarSelectionPref.getEntry() here.
                        // It is null when the preference's dialog has not been opened yet, and thus entry values are not initialized.
                        String localCalendarName = getResources().getString(R.string.myschedule_calsync_local_calendar_name);
                        String chosenCalendarName = PreferenceManager.getDefaultSharedPreferences(Main.getAppContext())
                                .getString(getResources().getString(R.string.sp_myschedule_calendar_to_sync_name), "");
                        //if the calendar to delete is the one chosen for calendar synchronization, then stop synchronization
                        // (this is not the case when the user first selects "create local calendar"
                        // but then goes back on the decision and chooses another calendar,
                        // this results in a local calendar being created but not currently chosen for synchronization)
                        if (localCalendarName.equals(chosenCalendarName)) {
                            CalendarModel.getInstance().notifyChange(CalendarSyncEvent.CHOSEN_CALENDAR_DELETED);
                        }

                        CalendarModel.deleteLocalCalendar();
                        CalendarModel.getInstance().notifyChange(CalendarSyncEvent.LOCAL_CALENDAR_DELETED);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog is closed
                    }
                }).show();
    }

    private void showDeleteCalendarDialog(String calendarId) {
        String calendarName = String.valueOf(mDeleteCalendarPref.getEntries()[mDeleteCalendarPref.findIndexOfValue(calendarId)]);
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_delete_cal_title)
                .setMessage(getResources().getString(R.string.dialog_delete_cal_message, calendarName))
                .setPositiveButton(R.string.dialog_delete_cal_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (calendarId.equals(mCalendarSelectionPref.getValue())) {
                            CalendarModel.getInstance().notifyChange(CalendarSyncEvent.CHOSEN_CALENDAR_DELETED);
                        }

                        Long localCalId = CalendarModel.getLocalCalendarId();
                        if(localCalId != null && Long.parseLong(calendarId) == localCalId){
                            CalendarModel.getInstance().notifyChange(CalendarSyncEvent.LOCAL_CALENDAR_DELETED);
                        }

                        CalendarModel.deleteCalendar(Long.parseLong(calendarId));
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog is closed
                    }
                })
                .show();
    }

    private void askWhetherToDeleteCalendarEntries() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_delete_calendar_entries_title)
                .setMessage(R.string.dialog_delete_calendar_entries_message)
                .setPositiveButton(R.string.dialog_delete_calendar_entries_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CalendarModel.deleteAllMyScheduleCalendarEntries();
                    }
                })
                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog is closed
                    }
                })
                .show();

    }

    private boolean isCalendarPermissionGranted() {
        return ContextCompat.checkSelfPermission(Main.getAppContext(),
                Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(Main.getAppContext(),
                Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCalendarPermission() {
        requestCalendarPermissionLauncher.launch(new String[]{
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR});
    }

    private boolean isNotificationPermissionGranted() {
        /*
         * Ab Android Level 33:
         * Standardmäßig enthält das FCM SDK (Version 23.0.6 oder höher) die im Manifest definierte
         * POST_NOTIFICATIONS Berechtigung. Ihre App muss jedoch auch die Laufzeitversion dieser
         * Berechtigung über die Konstante android.permission.POST_NOTIFICATIONS anfordern.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(Main.getAppContext(), Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(Main.getAppContext(), Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    /**
     * Register the permissions callback, which handles the user's response to the
     * system permissions dialog. Save the return value, an instance of
     * ActivityResultLauncher, as an instance variable.
     */
    private final ActivityResultLauncher requestCalendarPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    //permissions are granted
                    final boolean denied = result.containsValue(Boolean.FALSE);
                    if (!denied) {
                        mCalendarSelectionNoPermissionPref.setVisible(false);
                        mCalendarSelectionPref.setVisible(true);
                        mCalendarSyncSwitchPref.setEnabled(true);
                        populateCalendarSelectionList();

                        mDeleteCalendarNoPermissionPref.setVisible(false);
                        mDeleteCalendarPref.setVisible(true);
                        populateDeleteCalendarList();
                    } else {
                        Utils.showToast(R.string.myschedule_calsync_error);
                    }
                }
            });
    private final ActivityResultLauncher<String> requestNotificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                SwitchPreferenceCompat notificationPref = (SwitchPreferenceCompat) findPreference(getResources().getString(R.string.sp_myschedule_enable_fcm));
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                    notificationPref.setChecked(true);
                } else {
                    notificationPref.setChecked(false);
                    Utils.showToast(R.string.myschedule_pref_fcm_error);
                }
            });

    private final EventListener mChosenCalendarDeletedEventListener = new EventListener(){
        @Override
        public void onEvent(Event event) {
            CalendarModel.chosenCalendarIsDeleted();
            mCalendarSyncSwitchPref.setChecked(false);
            mCalendarSelectionPref.setSummary(R.string.myschedule_pref_choose_calendar_summary);
        }
    };
    private final EventListener mLocalCalendarDeletedEventListener = new EventListener(){
        @Override
        public void onEvent(Event event) {
            mDeleteLocalCalendarPref.setEnabled(false);
        }
    };

    Preference              mCalendarSelectionNoPermissionPref;
    ListPreference          mCalendarSelectionPref;
    Preference              mDeleteLocalCalendarPref;
    SwitchPreferenceCompat  mCalendarSyncSwitchPref;
    Preference              mDeleteCalendarNoPermissionPref;
    ListPreference          mDeleteCalendarPref;
}
