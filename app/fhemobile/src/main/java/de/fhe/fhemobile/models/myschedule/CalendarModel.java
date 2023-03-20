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

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.*;
import android.util.Log;


import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
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

    //----------------------------------------------------------------------------------------------

    private static class CalendarProjection {

        // Projection array.
        // Creating indices for this array instead of doing dynamic lookups improves performance.
        static final String[] PROJECTION = new String[]{
                Calendars._ID,                           // 0
                Calendars.ACCOUNT_NAME,                  // 1
                Calendars.CALENDAR_DISPLAY_NAME,         // 2
                Calendars.OWNER_ACCOUNT                  // 3
        };

        // The indices for the projection array above.
        static final int ID_INDEX = 0;
        static final int ACCOUNT_NAME_INDEX = 1;
        static final int DISPLAY_NAME_INDEX = 2;
        static final int OWNER_ACCOUNT_INDEX = 3;
    }

    private static class CalendarEventProjection {

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

    public static CalendarModel getInstance() {
        if(ourInstance == null) {
            ourInstance = new CalendarModel();
//            ourInstance.setChosenCalendar(new CalendarVo(
//                    "EAH - Mein Stundenplan",
//                    Main.getAppContext().getString(R.string.app_name),
//                    ACCOUNT_TYPE_LOCAL
//            ));
//            //read my schedule events already contained in calendar
//            readCalendar();
        }
        return ourInstance;
    }

    /**
     * Get all calenders of the user (google calendar, exchange calendar, local calendar, ...)
     * @return A map with the calendar id for each calendar name
     */
    public Map<String, Long> getCalendars(){
        HashMap<String, Long> result = new HashMap<>();

        // Run query
        Cursor cursor;
        ContentResolver cr = Main.getAppContext().getContentResolver();
        Uri uri = Calendars.CONTENT_URI;

        // Query all calendars (Submit the query and get a Cursor object back.)
        cursor = cr.query(uri,
                CalendarProjection.PROJECTION,
                null,           // selection -> all calendars
                null, //selectionArgs
                null); //sortOrder

        if (cursor == null) return result;

        // Use the cursor to step through the returned records
        while (cursor.moveToNext()) {
            long calID = cursor.getLong(CalendarProjection.ID_INDEX);
            String displayName = cursor.getString(CalendarProjection.DISPLAY_NAME_INDEX);
            String accountName = cursor.getString(CalendarProjection.ACCOUNT_NAME_INDEX);
            String ownerName = cursor.getString(CalendarProjection.OWNER_ACCOUNT_INDEX);

            result.put(displayName, calID);
            Log.d(TAG, "Kalender: " + displayName + " Id:" + calID);
        }
        cursor.close();
        return result;
    }


//    private Long getLocalCalendarId(){
//        // Run query
//        Cursor cursor;
//        ContentResolver cr = Main.getAppContext().getContentResolver();
//        Uri uri = Calendars.CONTENT_URI;
//        String selection = "(("
//                + Calendars.NAME + " = ?) AND ("
//                + Calendars.ACCOUNT_NAME + " = ?) AND ("
//                + Calendars.OWNER_ACCOUNT + " = ?) AND ("
//                + Calendars.ACCOUNT_TYPE + " = ?)"
//                + ")";
//        String[] selectionArgs = new String[]{
//                chosenCalendar.getCalendarName(),
//                chosenCalendar.getAccountName(),
//                chosenCalendar.getAccountName(),
//                chosenCalendar.getAccountType()};
//
//        // Submit the query and get a Cursor object back.
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
//            return null;
//        }
//        cursor = cr.query(uri,
//                PROJECTION,
//                selection,
//                selectionArgs,
//                null);
//
//        if (cursor == null) {
//            return null;
//        }
//
//        // Use the cursor to step through the returned records
//        while (cursor.moveToNext()) {
//            long calID = cursor.getLong(ID_INDEX);
//            String displayName = cursor.getString(DISPLAY_NAME_INDEX);
//            Log.d(TAG, "Local calendar " + displayName + " found.");
//
//            return calID;
//        }
//
//        cursor.close();
//        return null;
//    }
//
//


    public void syncMySchedule(){
        for(MyScheduleEventSeriesVo eventSeries : MyScheduleModel.getInstance().getSubscribedEventSeries()){
            syncEventSeries(eventSeries);
        }
    }

    /**
     * Create or update the events of the corresponding event series
     * @param eventSeries
     */
    private void syncEventSeries(MyScheduleEventSeriesVo eventSeries){

        for(MyScheduleEventVo eventVo : eventSeries.getEvents()){
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

//    /**
//     * Find calendar events with the given title
//     *
//     * @param eventSeriesTitle Title of the event series (which equals the eventSeriesTitle of all its calendar entries)
//     * @return List of {@link CalendarEventVo} created from the found calendar entries
//     */
//    private ArrayList<CalendarEventVo> findEvents(final String eventSeriesTitle){
//        ArrayList<CalendarEventVo> result = new ArrayList<>();
//
//        Cursor cursor;
//        ContentResolver cr = Main.getAppContext().getContentResolver();
//        String selection = "(("
//                + Instances.CALENDAR_ID + " = ?) AND ("
//                + Instances.TITLE + " = ?))"
//                + ")";
//        String chosenCalId = PreferenceManager.getDefaultSharedPreferences(Main.getAppContext())
//                .getString(Main.getAppContext().getResources().getString(R.string.sp_myschedule_calendar_to_sync), ""); //todo: what to set as default?
//        String[] selectionArgs = new String[]{chosenCalId, eventSeriesTitle};
//
//        // Construct the query with the desired date range.
//        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
////        ContentUris.appendId(builder, start);
//////        Specify the date range you want to search for recurring event instances
////        // + 5 ms to  dazu damit falls es gleich dem Startdatum ist er das Event nimmt
////        ContentUris.appendId(builder, end+ 5);
//
//        // Submit the query
//        cursor = cr.query(builder.build(),
//                CalendarEventProjection.PROJECTION,
//                selection,
//                selectionArgs,
//                Instances.DTSTART + " ASC");
//
//        if (cursor == null) return null;
//
//        //get CalendarEventVos
//        while (cursor.moveToNext()) {
//            final long eventId = cursor.getLong(CalendarEventProjection.ID_INDEX);
//            final String title = cursor.getString(CalendarEventProjection.TITLE_INDEX);
//            final long start = cursor.getLong(CalendarEventProjection.BEGIN_INDEX);
//            final long end = cursor.getLong(CalendarEventProjection.END_INDEX);
//
//            final CalendarEventVo calEntry = new CalendarEventVo(eventId, title, start, end);
//            result.add(calEntry);
//        }
//        cursor.close();
//
//        return result;
//    }

    /**
     * Creates an event in calendar
     */
    private void createCalendarEntry(MyScheduleEventVo scheduleEvent){

        String chosenCalId = PreferenceManager.getDefaultSharedPreferences(Main.getAppContext())
                .getString(Main.getAppContext().getResources().getString(R.string.sp_myschedule_calendar_to_sync), null); //todo: what to set as default?

        final ContentValues values = new ContentValues();
        values.put(Events.CALENDAR_ID, chosenCalId);
        values.put(Events.TITLE, scheduleEvent.getTitle());
        values.put(Events.DTSTART, scheduleEvent.getStartDateTimeInSec()*1000); //time in milliseconds
        values.put(Events.DTEND, scheduleEvent.getEndDateTimeInSec()*1000);
        values.put(Events.EVENT_LOCATION, scheduleEvent.getLocationListAsString());
        values.put(Events.DESCRIPTION, scheduleEvent.getLecturerListAsString()+", Sets: "+ scheduleEvent.getLocationListAsString());
//        values.put(Events.EVENT_COLOR, HOF_CALENDAR_COLOR); //todo set color
        // set timezone to Germany
        values.put(Events.EVENT_TIMEZONE, "Europe/Brussels");

        //insert event
        final ContentResolver cr = Main.getAppContext().getContentResolver();
        Uri eventUri = cr.insert(Events.CONTENT_URI, values);
        long eventId = Long.parseLong(eventUri.getLastPathSegment());
        scheduleEvent.setCalEventId(eventId);
        scheduleEvent.setChangedSinceLastCalSync(false);
    }

    private void updateCalendarEntry(MyScheduleEventVo scheduleEvent){
        long calEventId = scheduleEvent.getCalEventId();

        if(scheduleEvent.getTypesOfChanges().contains(TimetableChangeType.DELETION)){
            //todo: add "entfällt" to title or delete event
        }

        String chosenCalId = PreferenceManager.getDefaultSharedPreferences(Main.getAppContext())
                .getString(Main.getAppContext().getResources().getString(R.string.sp_myschedule_calendar_to_sync), null); //todo: what to set as default?

        final ContentValues values = new ContentValues();
        values.put(Events.CALENDAR_ID, chosenCalId);
        values.put(Events.TITLE, scheduleEvent.getTitle());
        values.put(Events.DTSTART, scheduleEvent.getStartDateTimeInSec());
        values.put(Events.DTEND, scheduleEvent.getEndDateTimeInSec());
        values.put(Events.EVENT_LOCATION, scheduleEvent.getLocationListAsString());
        values.put(Events.DESCRIPTION, scheduleEvent.getLecturerListAsString()+", Sets: "+scheduleEvent.getLocationListAsString());
//        values.put(Events.EVENT_COLOR, HOF_CALENDAR_COLOR); //todo set color
        // set timezone to Germany
        values.put(Events.EVENT_TIMEZONE, "Europe/Brussels");

        //insert event
        Uri updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, calEventId);
        final ContentResolver cr = Main.getAppContext().getContentResolver();
        cr.update(updateUri, values, null, null);

        scheduleEvent.setChangedSinceLastCalSync(false);
    }


//    private void createLocalCalendar(){
//        /* If an application needs to create a local calendar, it can do this by performing the
//        calendar insertion as a sync adapter, using an ACCOUNT_TYPE of ACCOUNT_TYPE_LOCAL.
//        ACCOUNT_TYPE_LOCAL is a special account type for calendars that are not associated with a
//        device account. Calendars of this type are not synced to a server. */
//        Uri calendarUri = Uri.parse(Calendars.CONTENT_URI.toString());
//        calendarUri = asSyncAdapter(calendarUri, chosenCalendar.getAccountName(), chosenCalendar.getAccountType());
//
//        ContentValues values = new ContentValues();
//        values.put(Calendars.ACCOUNT_TYPE, ACCOUNT_TYPE_LOCAL);
//        // The new display name for the calendar
//        values.put(Calendars.CALENDAR_DISPLAY_NAME, "Mein Stundenplan"); //todo: make translatable
//
//        //put other values
//        values.put(Calendars.OWNER_ACCOUNT, chosenCalendar.getAccountName());
//        values.put(Calendars.ACCOUNT_NAME, chosenCalendar.getAccountName());
//        values.put(Calendars.NAME, chosenCalendar.getCalendarName());
//        values.put(Calendars.CALENDAR_COLOR, HOF_CALENDAR_COLOR);
//        values.put(Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_ROOT);
//        values.put(Calendars.VISIBLE, 1);
//        values.put(Calendars.SYNC_EVENTS, 1);
//        // set timezone to Germany
//        values.put(Calendars.CALENDAR_TIME_ZONE, "Europe/Brussels");
//        values.put(Calendars.CAN_PARTIALLY_UPDATE, 1);
//
//        Main.getAppContext().getContentResolver().insert(calendarUri, values);
//    }
//
//    /**
//     * Delete local calendar
//     * @return True, if deletion had been successful
//     */
//    private boolean deleteLocalCalendar(){
//        final Long localCalendarID = getLocalCalendarId();
//
//        if (localCalendarID == null) return false;
//
//        final Uri.Builder builder = Calendars.CONTENT_URI.buildUpon();
//        final Uri calendarToRemoveUri = builder.appendPath(localCalendarID.toString())
//                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
//                .appendQueryParameter(Calendars.ACCOUNT_NAME, chosenCalendar.getAccountName())
//                .appendQueryParameter(Calendars.ACCOUNT_TYPE, chosenCalendar.getAccountType())
//                .build();
//
//        Main.getAppContext().getContentResolver().delete(calendarToRemoveUri, null, null);
//
//        //remove all events
//        removeAllLecturesEventIDs();
//        saveCalendarData(); // update calendar entries in shared preferences
//        return true;

//    }


    /**
     * Helper method to return a URI for use with a sync adapter
     * @param uri
     * @param account
     * @param accountType
     * @return
     */
    private Uri asSyncAdapter(Uri uri, String account, String accountType) {
        return uri.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, account)
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, accountType).build();
    }




    private static CalendarModel ourInstance;
//    private static HashMap<String, Long> calendars = new HashMap<>();
//    private CalendarVo chosenCalendar;
}
