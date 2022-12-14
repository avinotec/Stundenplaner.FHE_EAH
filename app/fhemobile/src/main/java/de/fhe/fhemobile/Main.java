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

package de.fhe.fhemobile;

import static de.fhe.fhemobile.utils.Define.MySchedule.SP_MYSCHEDULE;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.StringRes;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import de.fhe.fhemobile.comparator.EventSeriesTitleComparator;
import de.fhe.fhemobile.comparator.MyScheduleEventComparator;
import de.fhe.fhemobile.utils.Define;
import de.fhe.fhemobile.utils.feature.FeatureProvider;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventSeriesVo;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventVo;


/**
 * Created by paul on 22.01.14.
 */
public class Main extends Application {

    private static final String TAG = Main.class.getSimpleName();
    private static Application mAppContext;


    //Threading
    public static final ExecutorService executorService = Executors.newFixedThreadPool(2);
    public static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);


    //My Schedule
    public static Date lastUpdateSubscribedEventSeries;
    //note: always keep subscribedEventSeries sorted for display in the view
    private static HashMap<String, MyScheduleEventSeriesVo> subscribedEventSeries = new HashMap();

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = this;

        // load active features from xml
        FeatureProvider.loadFeatures(this);

        // load subscribed eventseries for My Schedule from Shared Preferences
        final SharedPreferences sharedPreferences = getSharedPreferences(SP_MYSCHEDULE, Context.MODE_PRIVATE);
        final String json = sharedPreferences.getString(Define.MySchedule.PREF_SUBSCRIBED_EVENTSERIES, "");

        // falls die Liste leer sein sollte, überspringen
        if (!json.isEmpty() && !"null".equals(json)) { //NON-NLS
            final Gson gson = new Gson();
            final Type listType = new TypeToken<ArrayList<MyScheduleEventSeriesVo>>(){}.getType();
            ArrayList<MyScheduleEventSeriesVo> list = gson.fromJson(json, listType);
            clearSubscribedEventSeries();
            for(MyScheduleEventSeriesVo eventSeriesVo : list){
                addToSubscribedEventSeries(eventSeriesVo);
            }
        }
        final long lastUpdated = sharedPreferences.getLong(Define.MySchedule.PREF_DATA_LAST_UPDATED,  -1);
        if(lastUpdated != -1) lastUpdateSubscribedEventSeries = new Date(lastUpdated);

        if (BuildConfig.DEBUG) {
            Assert.assertNotNull("onCreate(): subscribed eventseries is not initialized", subscribedEventSeries);
        }
    }

    /**
     * return String from Resource ID
     * @param _ResId requested ID
     * @return corresponding String
     */
    public static String getSafeString(@StringRes final int _ResId) {
        return mAppContext.getString(_ResId);
    }

    // getApplicationContext()
    public static Application getAppContext() {
        return mAppContext;
    }

    public static List<MyScheduleEventSeriesVo> getSortedSubscribedEventSeries(){
        List<MyScheduleEventSeriesVo> list = new ArrayList<>(subscribedEventSeries.values());
        Collections.sort(list, new EventSeriesTitleComparator());
        return list;
    }

    public static Collection<MyScheduleEventSeriesVo> getSubscribedEventSeries(){
        return subscribedEventSeries.values();
    }

    public static void addToSubscribedEventSeries(MyScheduleEventSeriesVo seriesVo){
        //set subscribed
        //needed for the case that an exam is being deleted by the user while fetching my schedule is adding it
        seriesVo.setSubscribed(true);
        subscribedEventSeries.put(seriesVo.getTitle(), seriesVo);
    }

    public static void updateSubscribedEventSeries(MyScheduleEventSeriesVo seriesVo){
        subscribedEventSeries.put(seriesVo.getTitle(), seriesVo);
    }

    public static void addToSubscribedEventSeries(List<MyScheduleEventSeriesVo> seriesVos){
        for(MyScheduleEventSeriesVo series : seriesVos){
            addToSubscribedEventSeries(series);
        }
    }

    public static void removeFromSubscribedEventSeries(MyScheduleEventSeriesVo seriesVo){
        subscribedEventSeries.remove(seriesVo.getTitle());
        int x = 1;
    }

    public static boolean containedInSubscribedEventSeries(MyScheduleEventSeriesVo eventSeries){
        return subscribedEventSeries.containsKey(eventSeries.getTitle());
    }

    public static void clearSubscribedEventSeries(){
        subscribedEventSeries.clear();
    }

    /**
     * Get a sorted list of all events contained in the subscribed event series'
     * @return List of {@link MyScheduleEventVo}, sorted by start datetime
     */
    public static ArrayList<MyScheduleEventVo> getEventsOfAllSubscribedEventSeries(){
        final ArrayList<MyScheduleEventVo> eventList = new ArrayList<>();

        for(final MyScheduleEventSeriesVo eventSeries : subscribedEventSeries.values()) {
            eventList.addAll(eventSeries.getEvents());
        }
        Collections.sort(eventList, new MyScheduleEventComparator());
        return eventList;
    }

    /**
     * Set date {@link Main#subscribedEventSeries} had been last updated,
     * and save it to shared preferences
     * @param lastUpdateSubscribedEventSeries The {@link Date}
     */
    public static void setLastUpdateSubscribedEventSeries(final Date lastUpdateSubscribedEventSeries) {
        Main.lastUpdateSubscribedEventSeries = lastUpdateSubscribedEventSeries;

        final SharedPreferences sharedPreferences = getAppContext().getSharedPreferences(SP_MYSCHEDULE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        if(Main.lastUpdateSubscribedEventSeries == null){
            editor.putLong(Define.MySchedule.PREF_DATA_LAST_UPDATED, -1);
        } else{
            editor.putLong(Define.MySchedule.PREF_DATA_LAST_UPDATED,
                    Main.lastUpdateSubscribedEventSeries.getTime());
        }
        editor.apply();
    }

    public static Date getLastUpdateSubscribedEventSeries() {
        return lastUpdateSubscribedEventSeries;
    }

    //MS 201908 Multidex apk introduced
    // Or if you do override the Application class but it's not possible to change the base class,
    // then you can instead override the attachBaseContext() method and call MultiDex.install(this) to enable multidex:
    @Override
    protected void attachBaseContext(final Context base) {
        super.attachBaseContext(base);
        androidx.multidex.MultiDex.install(this);
    }

}
