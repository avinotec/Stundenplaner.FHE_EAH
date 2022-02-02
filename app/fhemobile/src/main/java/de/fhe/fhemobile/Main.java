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

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.StringRes;

import com.google.gson.Gson;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fhe.fhemobile.utils.Define;
import de.fhe.fhemobile.utils.feature.FeatureProvider;
import de.fhe.fhemobile.vos.mytimetable.MyTimeTableCourse;


/**
 * Created by paul on 22.01.14.
 */
public class Main extends Application {

    private static final String LOG_TAG = Main.class.getSimpleName();
    private static Application mAppContext;

    //My Time Table
    public static List<MyTimeTableCourse> subscribedCourses = new ArrayList();

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = this;

        // load active features from xml
        FeatureProvider.loadFeatures(this);

        // load subscribed courses for My Time Table from Shared Preferences
        final SharedPreferences sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        final String json = sharedPreferences.getString(Define.SHARED_PREFERENCES_SUBSCRIBED_COURSES,"");

        // falls die Liste leer sein sollte, überspringen
        if ( ! "".equals(json)) {
            final Gson gson = new Gson();
            final MyTimeTableCourse[] list = gson.fromJson(json, MyTimeTableCourse[].class);
            subscribedCourses = new ArrayList<>(Arrays.asList(list));
        }

        Assert.assertTrue("onCreate(): subscribed courses is not initialized", subscribedCourses != null);
    }

    /**
     * return String from Resource ID
     * @param _ResId requested ID
     * @return corresponding String
     */
    public static String getSafeString(@StringRes final int _ResId) {
        return mAppContext.getString(_ResId);
    }

    public static Application getAppContext() {
        return mAppContext;
    }

    public static List<MyTimeTableCourse> getSubscribedCourses(){
        return subscribedCourses;
    }

    /**
     * Clear subscribed courses
     */
    public static void clearSubscribedCourses(){
        subscribedCourses.clear();
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
