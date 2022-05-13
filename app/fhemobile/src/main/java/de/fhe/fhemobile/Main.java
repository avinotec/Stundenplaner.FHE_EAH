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

import static de.fhe.fhemobile.utils.Define.MyTimeTable.SP_MYTIMETABLE;

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
import de.fhe.fhemobile.comparator.TimeTableEventComparator;
import de.fhe.fhemobile.utils.Define;
import de.fhe.fhemobile.utils.feature.FeatureProvider;
import de.fhe.fhemobile.vos.mytimetable.MyTimeTableEventSeriesVo;
import de.fhe.fhemobile.vos.mytimetable.MyTimeTableEventTimeVo;
import de.fhe.fhemobile.vos.timetable.TimeTableEventVo;


/**
 * Created by paul on 22.01.14.
 */
public class Main extends Application {

    private static final String LOG_TAG = Main.class.getSimpleName();
    private static Application mAppContext;


    //My Time Table
    //note: always keep subscribedEventSeries sorted for display in the view
    public static ArrayList<MyTimeTableEventSeriesVo> subscribedEventSeries = new ArrayList<MyTimeTableEventSeriesVo>(){
        @Override
        public boolean add(MyTimeTableEventSeriesVo myTimeTableEventSeriesVo) {
            super.add(myTimeTableEventSeriesVo);
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

        // load subscribed courses for My Time Table from Shared Preferences
        SharedPreferences sharedPreferences = getSharedPreferences(SP_MYTIMETABLE, Context.MODE_PRIVATE);
        final String json = sharedPreferences.getString(Define.MyTimeTable.PREF_SUBSCRIBED_COURSES,"");

        // falls die Liste leer sein sollte, überspringen
        if ( (json == null || !json.isEmpty()) && !"null".equals(json)) {
            final Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<MyTimeTableEventSeriesVo>>(){}.getType();
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

    public static ArrayList<MyTimeTableEventSeriesVo> getSubscribedEventSeries(){
        return subscribedEventSeries;
    }

    public static ArrayList<TimeTableEventVo> getAllSubscribedTimeTableEvents(){
        ArrayList<TimeTableEventVo> eventList = new ArrayList<>();

        for(MyTimeTableEventSeriesVo eventSeries : subscribedEventSeries){
            for(MyTimeTableEventTimeVo eventTime : eventSeries.getEvents()){

                TimeTableEventVo eventToAdd = new TimeTableEventVo(
                        eventSeries.getEventSeriesName(),
                        eventTime.getStartDateTimeInSec(),
                        eventTime.getEndDateTimeInSec(),
                        eventSeries.getLecturerMap(),
                        eventSeries.getLocationMap());

                eventList.add(eventToAdd);
            }
        }
        Collections.sort(eventList, new TimeTableEventComparator());
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
