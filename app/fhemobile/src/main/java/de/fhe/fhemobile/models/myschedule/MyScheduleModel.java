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

import static de.fhe.fhemobile.utils.Define.MySchedule.SP_MYSCHEDULE;

import android.content.Context;
import android.content.SharedPreferences;

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
    }

    public static MyScheduleModel getInstance() {
        if(ourInstance == null) {
            ourInstance = new MyScheduleModel();

            // load subscribed event series for My Schedule from Shared Preferences
            final SharedPreferences sharedPreferences = Main.getAppContext().getSharedPreferences(SP_MYSCHEDULE, Context.MODE_PRIVATE);
            final String json = sharedPreferences.getString(Define.MySchedule.PREF_SUBSCRIBED_EVENTSERIES, "");

            // falls die Liste leer sein sollte, überspringen
            if (!json.isEmpty() && !"null".equals(json)) { //NON-NLS
                final Gson gson = new Gson();
                final Type listType = new TypeToken<ArrayList<MyScheduleEventSeriesVo>>(){}.getType();
                ArrayList<MyScheduleEventSeriesVo> list = gson.fromJson(json, listType);
                ourInstance.clearSubscribedEventSeries();
                for(MyScheduleEventSeriesVo eventSeriesVo : list){
                    ourInstance.addToSubscribedEventSeries(eventSeriesVo);
                }
            }
            final long lastUpdated = sharedPreferences.getLong(Define.MySchedule.PREF_DATA_LAST_UPDATED,  -1);
            if(lastUpdated != -1) ourInstance.lastUpdateSubscribedEventSeries = new Date(lastUpdated);

            if (BuildConfig.DEBUG) {
                Assert.assertNotNull("onCreate(): subscribed eventseries is not initialized", ourInstance.subscribedEventSeries);
            }
        }
        return ourInstance;
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

    public void addToSubscribedEventSeries(MyScheduleEventSeriesVo seriesVo){
        //set subscribed
        //needed for the case that an exam is being deleted by the user while fetching my schedule is adding it
        seriesVo.setSubscribed(true);
        subscribedEventSeries.put(seriesVo.getTitle(), seriesVo);
    }

    public void updateSubscribedEventSeries(MyScheduleEventSeriesVo seriesVo){
        subscribedEventSeries.put(seriesVo.getTitle(), seriesVo);
    }

    public void addToSubscribedEventSeries(List<MyScheduleEventSeriesVo> seriesVos){
        for(MyScheduleEventSeriesVo series : seriesVos){
            addToSubscribedEventSeries(series);
        }
    }

    public void removeFromSubscribedEventSeries(MyScheduleEventSeriesVo seriesVo){
        subscribedEventSeries.remove(seriesVo.getTitle());
    }

    public boolean containedInSubscribedEventSeries(MyScheduleEventSeriesVo eventSeries){
        return subscribedEventSeries.containsKey(eventSeries.getTitle());
    }

    public void clearSubscribedEventSeries(){
        subscribedEventSeries.clear();
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

    public Date getLastUpdateSubscribedEventSeries() {
        return lastUpdateSubscribedEventSeries;
    }

    public void notifyChange(final String type){
        dispatchEvent(new MyScheduleChangeEvent(type));
    }


    private static MyScheduleModel ourInstance;


    public final HashMap<String, MyScheduleEventSeriesVo> subscribedEventSeries = new HashMap<>();
    public Date lastUpdateSubscribedEventSeries;
}
