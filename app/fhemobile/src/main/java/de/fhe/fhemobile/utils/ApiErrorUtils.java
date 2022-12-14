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
package de.fhe.fhemobile.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.vos.ApiErrorResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Nadja on 28.10.2022
 */
public final class ApiErrorUtils {

    private static final String TAG = ApiErrorUtils.class.getSimpleName();

    public static class ApiErrorCode {

        public static final String TIMETABLE_FRAGMENT_CODE1 = "4001";
        public static final String TIMETABLE_FRAGMENT_CODE2 = "4002";
        public static final String TIMETABLE_FRAGMENT_CODE3 = "4003";
        public static final String MYSCHEDULE_DIALOG_FRAGMENT_CODE1 = "4004";
        public static final String MYSCHEDULE_DIALOG_FRAGMENT_CODE2 = "4005";
        public static final String MYSCHEDULE_DIALOG_FRAGMENT_CODE3 = "4006";
        public static final String MYSCHEDULE_DIALOG_FRAGMENT_CODE4 = "4007";
        public static final String TIMETABLE_DIALOG_FRAGMENT_CODE1 = "4008";
        public static final String TIMETABLE_DIALOG_FRAGMENT_CODE2 = "4009";

        public static final String NETWORK_HANDLER_CODE1 = "4010";
        public static final String NETWORK_HANDLER_CODE2 = "4011";
        public static final String NETWORK_HANDLER_CODE3 = "4012";
        public static final String NETWORK_HANDLER_CODE4 = "4013";
        public static final String NETWORK_HANDLER_CODE5 = "4014";
        public static final String NETWORK_HANDLER_CODE6 = "4015";
        public static final String NETWORK_HANDLER_CODE7 = "4016";
        public static final String NETWORK_HANDLER_CODE8 = "4017";
        public static final String NETWORK_HANDLER_CODE9 = "4018";
        public static final String NETWORK_HANDLER_CODE10 = "4019";
        public static final String NETWORK_HANDLER_CODE11 = "4020";
        public static final String NETWORK_HANDLER_CODE12 = "4021";
    }

    public static ApiErrorResponse getApiErrorResponse(final Response response) {
        final Gson gson = new Gson();
        ApiErrorResponse error = new ApiErrorResponse();
        try {
            final ResponseBody body = response.errorBody();
            if (body != null) {
                error = gson.fromJson(body.string(), ApiErrorResponse.class);
            }
        } catch (final Exception e) {
            Log.e(TAG, "Problem parsing ApiErrorResponse", e);
        }
        return error;
    }

    /**
     * Show toast displaying "Error XXXX: message"
     *
     * @param errorResponse     {@link ApiErrorResponse} containing the message
     * @param internalErrorCode error code XXX
     */
    public static void showErrorToast(final ApiErrorResponse errorResponse, final String internalErrorCode) {
        try {
            String details = "";
            if(errorResponse.getId() == 1001){
                details = ": " + Main.getAppContext().getString(R.string.no_data_available);
            }
            final String message = Main.getAppContext().getString(R.string.error) + " "
                    + internalErrorCode + details;
            Utils.showToast(message);
        } catch (Exception e) {
            Log.e(TAG, "Problem showing Error Toast", e);
            showErrorToast(internalErrorCode);
        }
    }

    /**
     * Show toast displaying "Error XXXX: internal problems"
     *
     * @param internalErrorCode error code XXXX
     */
    public static void showErrorToast(final String internalErrorCode) {
        final String message = Main.getAppContext().getString(R.string.error) + " "
                + internalErrorCode + ": "
                + Main.getAppContext().getString(R.string.internal_problems);
        Utils.showToast(message);
    }

    /**
     * Show toast displaying "Error XXXX: Cannot establish connection!"
     *
     * @param internalErrorCode error code XXXX
     */
    public static void showConnectionErrorToast(final String internalErrorCode) {
        final String message = Main.getAppContext().getString(R.string.error) + " "
                + internalErrorCode + ": "
                + Main.getAppContext().getString(R.string.connection_failed);

        Utils.showToast(message);
    }
}
