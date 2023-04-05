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
package de.fhe.fhemobile.models.myschedule;

import static android.provider.CalendarContract.ACCOUNT_TYPE_LOCAL;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.*;
import android.util.Log;


import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import java.util.HashMap;
import java.util.Map;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.utils.Utils;
import de.fhe.fhemobile.utils.myschedule.TimetableChangeType;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventSeriesVo;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventVo;

/**
 *
 * created by Nadja - 03/2023
 */
public class CalendarModel {
    //Android Developer Tutorial: https://developer.android.com/guide/topics/providers/calendar-provider

    private static final String TAG = CalendarModel.class.getSimpleName();

    private final static String localCalendarName = Main.getAppContext().getString(R.string.myschedule_calsync_calendar_name);
    private final static String localCalendarAccount = Main.getAppContext().getString(R.string.app_name);
    private final static String localCalendarAccountType = ACCOUNT_TYPE_LOCAL;

    // singleton
    private static CalendarModel ourInstance;


    //----------------------------------------------------------------------------------------------

    private final static class CalendarProjection {

        // Projection array.
        // Creating indices for this array instead of doing dynamic lookups improves performance.
        static final String[] PROJECTION = new String[]{
                Calendars._ID,                           // 0
                Calendars.ACCOUNT_NAME,                  // 1
                Calendars.CALENDAR_DISPLAY_NAME,         // 2       The display name of the calendar. Column name.
                Calendars.OWNER_ACCOUNT,                  // 3

                /*
                 * A comma separated list of reminder methods supported for this
                 * calendar in the format "#,#,#". Valid types are
                 * {@link Reminders#METHOD_DEFAULT}, {@link Reminders#METHOD_ALERT},
                 * {@link Reminders#METHOD_EMAIL}, {@link Reminders#METHOD_SMS},
                 * {@link Reminders#METHOD_ALARM}. Column name.
                 * <P>Type: TEXT</P>
                 *                                                              Reminders#METHOD_ALERT <--
                public static final String ALLOWED_REMINDERS = "allowedReminders"; */


        };

        // The indices for the projection array above.
        static final int ID_INDEX = 0;
        static final int ACCOUNT_NAME_INDEX = 1;
        static final int DISPLAY_NAME_INDEX = 2;
        static final int OWNER_ACCOUNT_INDEX = 3;
    }

    private final static class CalendarEventProjection {

        // Projection array.
        // Creating indices for this array instead of doing dynamic lookups improves performance.
        static final String[] PROJECTION = new String[]{
                Instances.EVENT_ID,      // 0
                Instances.TITLE,         // 1
                Instances.BEGIN,         // 2
                Instances.END            // 3
        };
        // The indices for the projection array above.
        static final int ID_INDEX = 0;
        static final int TITLE_INDEX = 1;
        static final int BEGIN_INDEX = 2;
        static final int END_INDEX = 3;
    }


    //----------------------------------------------------------------------------------------------

    public final static CalendarModel getInstance() {
        if(ourInstance == null) {
            ourInstance = new CalendarModel();
        }
        return ourInstance;
    }

    /**
     * Get all calenders of the user (google calendar, exchange calendar, local calendar, ...)
     * @return A map with the calendar id for each calendar name
     */
    public static Map<String, Long> getCalendars(){
        final HashMap<String, Long> result = new HashMap<>();

        // Run query
        final ContentResolver cr = Main.getAppContext().getContentResolver();
        final Uri uri = Calendars.CONTENT_URI;

        // Query all calendars (Submit the query and get a Cursor object back.)
        final Cursor cursor = cr.query(uri,
                CalendarProjection.PROJECTION,
                null,           // selection -> all calendars
                null, //selectionArgs
                null); //sortOrder

        if (cursor == null)
            return result;

        // Use the cursor to step through the returned records
        while (cursor.moveToNext()) {
            final long calID = cursor.getLong(CalendarProjection.ID_INDEX);
            final String displayName = cursor.getString(CalendarProjection.DISPLAY_NAME_INDEX);

            result.put(displayName, calID);
            Log.d(TAG, "Kalender: " + displayName + " Id:" + calID);
        }
        cursor.close();
        return result;
    }


    /**
     * Get the calendar id of the local calendar created by the app
     * @return The calendar id or null, if no such calendar was found
     */
    public static Long getLocalCalendarId(){
        // Run query
        final ContentResolver cr = Main.getAppContext().getContentResolver();
        final Uri uri = Calendars.CONTENT_URI;
        final String selection = "(("
                + Calendars.NAME + " = ?) AND ("
                + Calendars.ACCOUNT_NAME + " = ?) AND ("
                + Calendars.ACCOUNT_TYPE + " = ?)"
                + ")";
        final String[] selectionArgs = new String[]{
                localCalendarName,
                localCalendarAccount,
                localCalendarAccountType};

        //if necessary permission is not granted
        if (ContextCompat.checkSelfPermission(Main.getAppContext(),
                Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        // Submit the query and get a Cursor object back.
        final Cursor cursor = cr.query(uri,
                CalendarProjection.PROJECTION,
                selection,
                selectionArgs,
                null);

        if (cursor == null) {
            return null;
        }

        // Use the cursor to step through the returned records
//TODO das ist eine komische while Schleife, eigentlich gar keine. Da fehlt noch etwas
        while (cursor.moveToNext()) {
            final long calID = cursor.getLong(CalendarProjection.ID_INDEX);
            final String displayName = cursor.getString(CalendarProjection.DISPLAY_NAME_INDEX);
            Log.d(TAG, "Local calendar " + displayName + " found.");

            return calID;
        }

        cursor.close();
        return null;
    }

    public void deleteCalendarEntries(MyScheduleEventSeriesVo eventSeries){
        for(MyScheduleEventVo event : eventSeries.getEvents()){
            //eventId can be null if the calendar has not been synchronized since subscribing this eventSeries
            if(event.getCalEventId() != null){
                deleteCalendarEntry(event);
            }
        }
    }

    public void syncMySchedule(){
        for(MyScheduleEventSeriesVo eventSeries : MyScheduleModel.getInstance().getSubscribedEventSeries()){
            syncEventSeries(eventSeries);
        }
    }

    /**
     * Create or update the events of the corresponding event series
     * @param eventSeries
     */
    private void syncEventSeries(final MyScheduleEventSeriesVo eventSeries){

        for(final MyScheduleEventVo eventVo : eventSeries.getEvents()){
            if(eventVo.changedSinceLastCalSync()){

                //create calendar entry
                if (eventVo.getCalEventId() == null){
                    createCalendarEntry(eventVo);
                }
                //update calendar entry
                else {
                    updateCalendarEntry(eventVo);
                }
            }
        }
    }

    /**
     * Creates an event in calendar
     */
    private void createCalendarEntry(final MyScheduleEventVo scheduleEvent){

        final String chosenCalId = PreferenceManager.getDefaultSharedPreferences(Main.getAppContext())
                .getString(Main.getAppContext().getResources().getString(R.string.sp_myschedule_calendar_to_sync), "");

        final ContentValues values = new ContentValues();
        values.put(Events.CALENDAR_ID, chosenCalId);
        values.put(Events.TITLE, scheduleEvent.getTitle());
        values.put(Events.DTSTART, scheduleEvent.getStartDateTimeInSec()*1000); //time in milliseconds
        values.put(Events.DTEND, scheduleEvent.getEndDateTimeInSec()*1000);
        values.put(Events.EVENT_LOCATION, scheduleEvent.getLocationListAsString());
        values.put(Events.DESCRIPTION, scheduleEvent.getLecturerListAsString()+", Sets: "+ scheduleEvent.getLocationListAsString());
        // set timezone to Germany
        values.put(Events.EVENT_TIMEZONE, "Europe/Brussels");

        //insert event
        final ContentResolver cr = Main.getAppContext().getContentResolver();
        final Uri eventUri = cr.insert(Events.CONTENT_URI, values);
        final String lastPathSegment = eventUri.getLastPathSegment();
        final long eventId ;
        if (lastPathSegment != null) {
            eventId = Long.parseLong(lastPathSegment);
            scheduleEvent.setCalEventId(eventId);
            scheduleEvent.setChangedSinceLastCalSync(false);
        }
        else {
            // TODO check if this is a valid
            //eventId = 0;
            Log.e( TAG, "E22189 eventid is not created. FATAL ERROR, calendar entry not inserted.", new Exception("E22189 eventid is not created. FATAL ERROR, calendar entry not inserted."));
        }
    }

    private void updateCalendarEntry(final MyScheduleEventVo scheduleEvent){
        final long calEventId = scheduleEvent.getCalEventId();

        String eventTitle = scheduleEvent.getTitle();
        //mark event as deleted
        if(scheduleEvent.getTypesOfChanges().contains(TimetableChangeType.DELETION)){
            eventTitle = Main.getAppContext().getString(R.string.myschedule_calsync_event_dropped_tag) + eventTitle;
        }

        final String chosenCalId = PreferenceManager.getDefaultSharedPreferences(Main.getAppContext())
                .getString(Main.getAppContext().getResources().getString(R.string.sp_myschedule_calendar_to_sync), "");

        final ContentValues values = new ContentValues();
        values.put(Events.CALENDAR_ID, chosenCalId);
        values.put(Events.TITLE, eventTitle);
        values.put(Events.DTSTART, scheduleEvent.getStartDateTimeInSec());
        values.put(Events.DTEND, scheduleEvent.getEndDateTimeInSec());
        values.put(Events.EVENT_LOCATION, scheduleEvent.getLocationListAsString());
        values.put(Events.DESCRIPTION, scheduleEvent.getLecturerListAsString()+", Sets: "+scheduleEvent.getLocationListAsString());
        values.put(Events.EVENT_COLOR, ContextCompat.getColor(Main.getAppContext(), R.color.primary_color));
        // set timezone to Germany
        values.put(Events.EVENT_TIMEZONE, "Europe/Brussels");

        //insert event
        final Uri updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, calEventId);
        final ContentResolver cr = Main.getAppContext().getContentResolver();
        cr.update(updateUri, values, null, null);

        scheduleEvent.setChangedSinceLastCalSync(false);
    }

    private void deleteCalendarEntry(final MyScheduleEventVo scheduleEvent){
        final long calEventId = scheduleEvent.getCalEventId();

        //delete event
        final Uri updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, calEventId);
        final ContentResolver cr = Main.getAppContext().getContentResolver();
        cr.delete(updateUri, null, null);
    }


    public final static String createLocalCalendar(){
        /* If an application needs to create a local calendar, it can do this by performing the
        calendar insertion as a sync adapter, using an ACCOUNT_TYPE of ACCOUNT_TYPE_LOCAL.
        ACCOUNT_TYPE_LOCAL is a special account type for calendars that are not associated with a
        device account. Calendars of this type are not synced to a server. */
        Uri calendarUri = Uri.parse(Calendars.CONTENT_URI.toString());
        calendarUri = asSyncAdapter(calendarUri, localCalendarAccount, localCalendarAccountType);

        final ContentValues values = new ContentValues();
        values.put(Calendars.ACCOUNT_NAME, localCalendarAccount);
        values.put(Calendars.ACCOUNT_TYPE, localCalendarAccountType);
        // The new display name for the calendar
        values.put(Calendars.CALENDAR_DISPLAY_NAME, localCalendarName);
        //put other values
        values.put(Calendars.NAME, localCalendarName);
        values.put(Calendars.CALENDAR_COLOR, ContextCompat.getColor(Main.getAppContext(), R.color.primary_color));
        values.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_ROOT);
        values.put(Calendars.VISIBLE, 1);
        values.put(Calendars.SYNC_EVENTS, 1);
        // set timezone to Germany
        values.put(Calendars.CALENDAR_TIME_ZONE, "Europe/Brussels");
        values.put(Calendars.CAN_PARTIALLY_UPDATE, 1);

        final Uri uri = Main.getAppContext().getContentResolver().insert(calendarUri, values);
        if(uri != null){
            final String calId = uri.getLastPathSegment();
            PreferenceManager.getDefaultSharedPreferences(Main.getAppContext()).edit()
                    .putString(Main.getAppContext().getResources().getString(R.string.sp_myschedule_calendar_to_sync), calId).apply();
            return calId;
        }
        return null;
    }

    /**
     * Delete local calendar
     */
    public void deleteLocalCalendar(){
        final Long localCalendarID = getLocalCalendarId();

        if (localCalendarID == null) {
            Utils.showToast(R.string.myschedule_delete_calendar_error);
            return;
        }

        final Uri.Builder builder = Calendars.CONTENT_URI.buildUpon();
        final Uri calendarToRemoveUri = builder.appendPath(localCalendarID.toString())
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, localCalendarAccount)
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, localCalendarAccountType)
                .build();

        Main.getAppContext().getContentResolver().delete(calendarToRemoveUri, null, null);
        unlinkAllCalendarEventsAndMyScheduleEvents();

        PreferenceManager.getDefaultSharedPreferences(Main.getAppContext()).edit()
                .putString(Main.getAppContext().getResources().getString(R.string.sp_myschedule_calendar_to_sync), "").apply();

    }


    /**
     * Helper method to return a URI for use with a sync adapter
     * @param uri
     * @param account
     * @param accountType
     * @return
     */
    private static Uri asSyncAdapter(Uri uri, String account, String accountType) {
        return uri.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, account)
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, accountType).build();
    }


    /**
     * Remove all calendar event ids stored in {@link MyScheduleEventVo}s
     * and mark as "needed to be synchronized in a future synchronisation"
     */
    private void unlinkAllCalendarEventsAndMyScheduleEvents(){
        for(MyScheduleEventSeriesVo eventSeries : MyScheduleModel.getInstance().getSubscribedEventSeries()){
            for(MyScheduleEventVo event : eventSeries.getEvents()){
                event.setCalEventId(null);
                // set mChangedSinceLastCalSync to true to mark event as "to synchronize"
                // in the next future synchronisation run
                event.setChangedSinceLastCalSync(true);
            }
        }
    }


}
