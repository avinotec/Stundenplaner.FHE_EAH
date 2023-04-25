/*
 *  Copyright (c) 2023-2023  Ernst-Abbe-Hochschule Jena
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

import static de.fhe.fhemobile.Main.getAppContext;
import static de.fhe.fhemobile.utils.Define.MySchedule.PREF_SUBSCRIBED_EVENTSERIES;
import static de.fhe.fhemobile.utils.Define.MySchedule.SP_MYSCHEDULE;
import static de.fhe.fhemobile.utils.Utils.correctUmlauts;
import static de.fhe.fhemobile.utils.myschedule.MyScheduleUtils.isExam;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Assert;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.fhe.fhemobile.BuildConfig;
import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.adapters.myschedule.MyScheduleCalendarAdapter;
import de.fhe.fhemobile.adapters.myschedule.MyScheduleOverviewAdapter;
import de.fhe.fhemobile.comparator.EventSeriesTitleComparator;
import de.fhe.fhemobile.comparator.MyScheduleEventComparator;
import de.fhe.fhemobile.events.EventDispatcher;
import de.fhe.fhemobile.events.MyScheduleChangeEvent;
import de.fhe.fhemobile.utils.Define;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventSeriesVo;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventVo;

/**
 * Class for sending My Schedule data related events
 *
 * created by Nadja - 15.02.2023
 */
public class MyScheduleModel extends EventDispatcher {

    private static final String TAG = MyScheduleModel.class.getSimpleName();


    /* Utility classes have all fields and methods declared as static.
    Creating private constructors in utility classes prevents them from being accidentally instantiated. */
    private MyScheduleModel(){
        initModel();
    }

    public static MyScheduleModel getInstance() {
        if(ourInstance == null) {
            ourInstance = new MyScheduleModel();
        }
        return ourInstance;
    }

    /**
     * Initialize {@link MyScheduleModel} by loading My Schedule from Shared Preferences
     * and set list adapters
     */
    private void initModel() {
        // load subscribed event series for My Schedule from Shared Preferences
        final SharedPreferences sharedPreferences = Main.getAppContext().getSharedPreferences(SP_MYSCHEDULE, Context.MODE_PRIVATE);
        final String json = sharedPreferences.getString(Define.MySchedule.PREF_SUBSCRIBED_EVENTSERIES, "");

        // skip if list from shared preferences is empty
        if (!json.isEmpty() && !"null".equals(json)) { //NON-NLS
            final Gson gson = new Gson();
            final Type listType = new TypeToken<ArrayList<MyScheduleEventSeriesVo>>(){}.getType();
            ArrayList<MyScheduleEventSeriesVo> list = gson.fromJson(json, listType);
            for(MyScheduleEventSeriesVo eventSeriesVo : list){
                this.addToSubscribedEventSeries(eventSeriesVo);
            }
        }
        final long lastUpdated = sharedPreferences.getLong(Define.MySchedule.PREF_DATA_LAST_UPDATED,  -1);
        if(lastUpdated != -1) this.lastUpdateSubscribedEventSeries = new Date(lastUpdated);

        if (BuildConfig.DEBUG) {
            Assert.assertNotNull("onCreate(): subscribed eventseries is not initialized", this.subscribedEventSeries);
        }

        //adapters
        myScheduleCalendarAdapter.setItems(this.getEventsOfAllSubscribedEventSeries());
        myScheduleOverviewAdapter.setItems(this.getSortedSubscribedEventSeries());
    }

    /**
     * Add event series to subscriptions and update listview adapters
     * @param eventSeries
     */
    public void addToSubscribedEventSeriesAndUpdateAdapters(final MyScheduleEventSeriesVo eventSeries){
        //add to subscribed event series
        //set subscribed -> needed for the case that an exam is being deleted by the user while fetching my schedule is adding it
        eventSeries.setSubscribed(true);
        subscribedEventSeries.put(eventSeries.getTitle(), eventSeries);

        saveSubscribedEventSeriesToSharedPreferences();

        myScheduleCalendarAdapter.setItems(getInstance().getEventsOfAllSubscribedEventSeries());
        myScheduleOverviewAdapter.setItems(getInstance().getSortedSubscribedEventSeries());
    }

    /**
     * Remove given event series from subscriptions and update listview adapters
     * @param unsubscribedEventSeries
     */
    public void removeFromSubscribedEventSeriesAndUpdateAdapters(final MyScheduleEventSeriesVo unsubscribedEventSeries){
        CalendarModel.deleteCalendarEntries(unsubscribedEventSeries);

        unsubscribedEventSeries.setSubscribed(false);
        subscribedEventSeries.remove(unsubscribedEventSeries.getTitle());
        saveSubscribedEventSeriesToSharedPreferences();

        myScheduleCalendarAdapter.setItems(getInstance().getEventsOfAllSubscribedEventSeries());
        myScheduleOverviewAdapter.setItems(getInstance().getSortedSubscribedEventSeries());
    }

    /**
     * Update subscriptions to given list of event series and update listview adapters
     * @param updatedSubscribedEventSeries
     */
    public void updateSubscribedEventSeriesAndAdapters(final List<MyScheduleEventSeriesVo> updatedSubscribedEventSeries){

        for(MyScheduleEventSeriesVo series : updatedSubscribedEventSeries){

            if(getInstance().containedInSubscribedEventSeries(series)){
                //update subscribed event series
                subscribedEventSeries.put(series.getTitle(), series);
            } else if(isExam(series)){
                //add to subscribed event series
                //set subscribed -> needed for the case that an exam is being deleted by the user while fetching my schedule is adding it
                series.setSubscribed(true);
                subscribedEventSeries.put(series.getTitle(), series);
            } else {
                //the series has been deleted from subscribed event series (has been unsubscribed)
                // while the updates had been computed (while My Schedule had been fetched)
            }
        }
        setLastUpdateSubscribedEventSeries(new Date());

        saveSubscribedEventSeriesToSharedPreferences();

        myScheduleCalendarAdapter.setItems(getEventsOfAllSubscribedEventSeries());
        myScheduleOverviewAdapter.setItems(getSortedSubscribedEventSeries());
        notifyChange(MyScheduleChangeEvent.MYSCHEDULE_UPDATED);
    }

    public void clearSubscribedEventSeriesAndUpdateAdapters(){
        subscribedEventSeries.clear();
        getInstance().setLastUpdateSubscribedEventSeries(null);

        saveSubscribedEventSeriesToSharedPreferences();

        myScheduleCalendarAdapter.setItems(getInstance().getEventsOfAllSubscribedEventSeries());
        myScheduleOverviewAdapter.setItems(getInstance().getSortedSubscribedEventSeries());
        notifyChange(MyScheduleChangeEvent.MYSCHEDULE_UPDATED);
    }

    public void saveSubscribedEventSeriesToSharedPreferences() {
        final Gson gson = new Gson();
        final String json = correctUmlauts(gson.toJson(getInstance().getSortedSubscribedEventSeries(), ArrayList.class));
        final SharedPreferences sharedPreferences = getAppContext().getSharedPreferences(SP_MYSCHEDULE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_SUBSCRIBED_EVENTSERIES, json);
        editor.apply();

        // wir geben mal den erhaltenen JSON String aus. Dann können wir sehen, was Carsten uns sendet.
        if (BuildConfig.DEBUG){
            Log.d(TAG, "saveSubscribedEventSeriesToSharedPreferences(): JSON: " + json);
        }
    }

    /**
     * Get list of subscribed event series, sorted by title
     * @return List of {@link MyScheduleEventVo}s
     */
    public List<MyScheduleEventSeriesVo> getSortedSubscribedEventSeries(){
        List<MyScheduleEventSeriesVo> list = new ArrayList<>(subscribedEventSeries.values());
        Collections.sort(list, new EventSeriesTitleComparator());
        return list;
    }

    /**
     * Get collection of subscribed event series
     * @return Collection of {@link MyScheduleEventVo}s
     */
    public Collection<MyScheduleEventSeriesVo> getSubscribedEventSeries(){
        return subscribedEventSeries.values();
    }

    /**
     * Check if given event series is contained in subscriptions
     * @param eventSeries
     * @return
     */
    public boolean containedInSubscribedEventSeries(MyScheduleEventSeriesVo eventSeries){
        return subscribedEventSeries.containsKey(eventSeries.getTitle());
    }

    /**
     * Get a sorted list of all events contained in the subscribed event series'
     * @return List of {@link MyScheduleEventVo}, sorted by start datetime
     */
    public ArrayList<MyScheduleEventVo> getEventsOfAllSubscribedEventSeries(){
        final ArrayList<MyScheduleEventVo> eventList = new ArrayList<>();

        for(final MyScheduleEventSeriesVo eventSeries : subscribedEventSeries.values()) {
            eventList.addAll(eventSeries.getEvents());
        }
        Collections.sort(eventList, new MyScheduleEventComparator());
        return eventList;
    }

    /**
     * Set date {@link MyScheduleModel#subscribedEventSeries} had been last updated,
     * and save it to shared preferences
     * @param lastUpdateSubscribedEventSeries The {@link Date}
     */
    public void setLastUpdateSubscribedEventSeries(final Date lastUpdateSubscribedEventSeries) {
        this.lastUpdateSubscribedEventSeries = lastUpdateSubscribedEventSeries;

        final SharedPreferences sharedPreferences = Main.getAppContext().getSharedPreferences(SP_MYSCHEDULE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        if(lastUpdateSubscribedEventSeries == null){
            editor.putLong(Define.MySchedule.PREF_DATA_LAST_UPDATED, -1);
        } else{
            editor.putLong(Define.MySchedule.PREF_DATA_LAST_UPDATED,
                    this.lastUpdateSubscribedEventSeries.getTime());
        }
        editor.apply();
    }

    /**
     * Add given {@link MyScheduleEventSeriesVo} to subscriptions
     * @param seriesVo
     */
    public void addToSubscribedEventSeries(MyScheduleEventSeriesVo seriesVo){
        //set subscribed -> needed for the case that an exam is being deleted by the user while fetching my schedule is adding it
        seriesVo.setSubscribed(true);
        subscribedEventSeries.put(seriesVo.getTitle(), seriesVo);
    }

    public Date getLastUpdateSubscribedEventSeries() {
        return lastUpdateSubscribedEventSeries;
    }

    public void notifyChange(final String type){
        dispatchEvent(new MyScheduleChangeEvent(type));
    }

    public static MyScheduleCalendarAdapter getMyScheduleCalendarAdapter() {
        return myScheduleCalendarAdapter;
    }

    public static MyScheduleOverviewAdapter getMyScheduleOverviewAdapter() {
        return myScheduleOverviewAdapter;
    }

    private static MyScheduleModel ourInstance;

    //These adapters need to be static and initialized to avoid crashing when a view (from the UI thread!)
    // requests the adapter to set for the list view while the model is still initializing in the main thread.
    private static final MyScheduleCalendarAdapter myScheduleCalendarAdapter = new MyScheduleCalendarAdapter();
    private static final MyScheduleOverviewAdapter myScheduleOverviewAdapter = new MyScheduleOverviewAdapter();

    public final HashMap<String, MyScheduleEventSeriesVo> subscribedEventSeries = new HashMap<>();
    public Date lastUpdateSubscribedEventSeries;
}
