/*
 *  Copyright (c) 2022-2022 Ernst-Abbe-Hochschule Jena
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
package de.fhe.fhemobile.services;

import android.util.Log;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.network.NetworkHandler;

/**
 * {@link Runnable} for fetching My Schedule using another but the main thread
 *
 * created by Nadja - 12/22
 */
public class FetchMyScheduleBackgroundTask implements Runnable {

    private static final String TAG = FetchMyScheduleBackgroundTask.class.getSimpleName();

    /**
     * Construct a new {@link FetchMyScheduleBackgroundTask} that fetches my schedule
     * and is run every 10 min.
     */
    public static void startPeriodicFetching(){
        if(mScheduledFuture == null){
            mScheduledFuture = Main.scheduledExecutorService.scheduleWithFixedDelay(
                    new FetchMyScheduleBackgroundTask(), 0, 10, TimeUnit.MINUTES);
        }
    }

    /**
     * Stop the {@link FetchMyScheduleBackgroundTask} that periodically fetches my schedule
     * after it finished the current run.
     */
    public static void stopPeriodicFetching(){
        //mScheduledFuture.isCancelled() necessary to check because calling cancel() on a futures
        // that has been already canceled causes a SocketException
        if(mScheduledFuture != null && !mScheduledFuture.isCancelled()){
            Main.scheduledExecutorService.schedule(
                    () -> mScheduledFuture.cancel(true),0, TimeUnit.SECONDS);
        }
    }

    /**
     * Construct a new {@link FetchMyScheduleBackgroundTask} that fetches My Schedule.
     * The task gets scheduled for one immediate execution.
     */
    public static void fetch(){
        Main.scheduledExecutorService.schedule(new FetchMyScheduleBackgroundTask(), 0, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        Log.i(TAG, "Started FetchMyScheduleBackgroundTask.run()");
        NetworkHandler.getInstance().fetchMySchedule();
    }

    private static ScheduledFuture mScheduledFuture;

}
