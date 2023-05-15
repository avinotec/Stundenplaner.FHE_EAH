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
package de.fhe.fhemobile.services;

import android.util.Log;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.models.myschedule.CalendarModel;
import de.fhe.fhemobile.utils.Utils;

/**
 * {@link Runnable} for syncing My Schedule to a calendar
 * <p>
 * Created by Nadja - 16.02.2023
 */
public class CalendarSynchronizationBackgroundTask implements Runnable {

    private static final String TAG = CalendarSynchronizationBackgroundTask.class.getSimpleName();

    /**
     * Construct a new {@link CalendarSynchronizationBackgroundTask}
     * that synchronizes My Schedule with the users calendar every 10 min.
     * @param showToast Boolean, whether to show a toast when synchronization is finished
     */
    public static void startPeriodicSynchronizing(boolean showToast) {
        showSyncFinishedToast = showToast;

        if (mScheduledFuture == null) {
            mScheduledFuture = Main.scheduledExecutorService.scheduleWithFixedDelay(
                    new CalendarSynchronizationBackgroundTask(), 0, 10, TimeUnit.MINUTES);
        }
    }

    /**
     * Stop the {@link CalendarSynchronizationBackgroundTask} that periodically synchronizes my schedule
     * after it finished the current run.
     */
    public static void stopPeriodicSynchronizing() {
        //mScheduledFuture.isCancelled() necessary to check because calling cancel() on a futures
        // that has been already canceled causes a SocketException
        if (mScheduledFuture != null && !mScheduledFuture.isCancelled()) {
            Main.scheduledExecutorService.schedule(
                    () -> mScheduledFuture.cancel(true), 0, TimeUnit.SECONDS);
        }
    }

    /**
     * Construct a new {@link CalendarSynchronizationBackgroundTask} that synchronizes My Schedule.
     * The task gets scheduled for one immediate execution.
     */
    public static void sync() {
        Utils.showToastFromBackgroundTask("DEBUG Info: Single calendar synchronization task started ");
        Main.scheduledExecutorService.schedule(new CalendarSynchronizationBackgroundTask(), 0, TimeUnit.SECONDS);
    }


    @Override
    public void run() {
        Log.i(TAG, "Started CalendarSynchronizationBackgroundTask.run()");

        CalendarModel.syncMySchedule();

        //show sync finished toast when switch gets enabled,
        // do not show when the scheduled period is triggering task execution
        if(showSyncFinishedToast){
            Utils.showToastFromBackgroundTask(R.string.myschedule_calsync_finished);
            showSyncFinishedToast = false;
        }
    }

    private static boolean showSyncFinishedToast = false;
    private static ScheduledFuture mScheduledFuture;
}
