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
import java.util.Collections;

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

    private static final String LOG_TAG = Main.class.getSimpleName();
    private static Application mAppContext;


    //My Schedule
    //note: always keep subscribedEventSeries sorted for display in the view
    public static ArrayList<MyScheduleEventSeriesVo> subscribedEventSeries = new ArrayList<MyScheduleEventSeriesVo>(){
        @Override
        public boolean add(MyScheduleEventSeriesVo myScheduleEventSeriesVo) {
            super.add(myScheduleEventSeriesVo);
            Collections.sort(subscribedEventSeries, new EventSeriesTitleComparator());
            return true;
        }

        @Override
        public boolean remove(Object o) {
            super.remove(o);
            Collections.sort(subscribedEventSeries, new EventSeriesTitleComparator());
            return true;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = this;

        // load active features from xml
        FeatureProvider.loadFeatures(this);

        // load subscribed courses for My Schedule from Shared Preferences
        SharedPreferences sharedPreferences = getSharedPreferences(SP_MYSCHEDULE, Context.MODE_PRIVATE);
        final String json = sharedPreferences.getString(Define.MySchedule.PREF_SUBSCRIBED_EVENTSERIES, "");

        // falls die Liste leer sein sollte, überspringen
        if ( !json.isEmpty() && !"null".equals(json)) { //NON-NLS
            final Gson gson = new Gson();
            final Type listType = new TypeToken<ArrayList<MyScheduleEventSeriesVo>>(){}.getType();
            subscribedEventSeries = gson.fromJson(json, listType);
        }

        Assert.assertNotNull("onCreate(): subscribed courses is not initialized", subscribedEventSeries);
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

    public static ArrayList<MyScheduleEventSeriesVo> getSubscribedEventSeries(){
        return subscribedEventSeries;
    }

    public static ArrayList<MyScheduleEventVo> getAllSubscribedTimeTableEvents(){
        ArrayList<MyScheduleEventVo> eventList = new ArrayList<>();

        for(MyScheduleEventSeriesVo eventSeries : subscribedEventSeries) {
            eventList.addAll(eventSeries.getEvents());
        }
        Collections.sort(eventList, new MyScheduleEventComparator());
        return eventList;
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
