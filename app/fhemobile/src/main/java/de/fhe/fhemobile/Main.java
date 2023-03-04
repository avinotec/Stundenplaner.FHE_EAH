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


import android.app.Application;

import androidx.annotation.StringRes;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import de.fhe.fhemobile.utils.feature.FeatureProvider;


/**
 * Created by paul on 22.01.14.
 */
public class Main extends Application {

    private static final String TAG = Main.class.getSimpleName();
    private static Application mAppContext;


    //Threading
    public static final ExecutorService executorService = Executors.newFixedThreadPool(2);
    public static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);


    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = this;

        // load active features from xml
        FeatureProvider.loadFeatures(this);
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

    /* minSDK now >= 21. Runtime Library is no longer DEX
    //MS 201908 Multidex apk introduced
    // Or if you do override the Application class but it's not possible to change the base class,
    // then you can instead override the attachBaseContext() method and call MultiDex.install(this) to enable multidex:
    @Override
    protected void attachBaseContext(final Context base) {
        super.attachBaseContext(base);
        androidx.multidex.MultiDex.install(this);
    }
*/

}
