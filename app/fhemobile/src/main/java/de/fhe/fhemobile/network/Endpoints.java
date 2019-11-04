/*
 * Copyright (c) 2014-2019 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fhe.fhemobile.network;

public class Endpoints {

    public static final String LIVE_URL             = "http://app.fh-erfurt.de:8080";
    //public static final String LIVE_URL             = "http://193.174.232.89:8080";
    //public static final String LOCAL_URL            = "http://192.168.1.102:8080";
    //public static final String SIMON_LOCAL_URL      = "http://10.12.24.58:8000";

    public static final String BASE_URL             = LIVE_URL;
    public static final String APP_NAME             = "/fheapp/";

    //TODO
    public static final String SCHEDULE_CHANGE_SERVER = "https://lustigtestt.de/fhjena/rest_api/public/changes";


    /* Example
        http://193.174.232.89:8080/fheapp/api/eah/timetable

        news:
        http://193.174.232.89:8080/fheapp/api/eah/news/
        http://193.174.232.89:8080/fheapp/api/eah/news/0    <-- kaputt
        http://193.174.232.89:8080/fheapp/api/eah/news/1
        http://193.174.232.89:8080/fheapp/api/eah/news/2

    *
    * */

    // ---------------------------------------------------------------------------------------------
    //                                           Enpoints
    // ---------------------------------------------------------------------------------------------

    public static final String RSS                  = "api/" + SiteEndpoints.SITE_PATH_PARAM + "/news";
    public static final String MENSA                = "api/" + SiteEndpoints.SITE_PATH_PARAM + "/canteens";
    public static final String SEMESTER             = "api/" + SiteEndpoints.SITE_PATH_PARAM + "/Semester"; //Semestertermine
    public static final String PHONEBOOK            = "api/" + SiteEndpoints.SITE_PATH_PARAM + "/persons";  //funktioniert nicht
    public static final String IMPRESSUM            = "api/" + SiteEndpoints.SITE_PATH_PARAM + "/impress";
    public static final String WEATHER              = "api/" + SiteEndpoints.SITE_PATH_PARAM + "/weather";
    public static final String AQUA                 = "api/" + SiteEndpoints.SITE_PATH_PARAM + "/aqua";
    public static final String TIMETABLE            = "api/" + SiteEndpoints.SITE_PATH_PARAM + "/timetable";
    public static final String TIMETABLE_EVENTS     = "api/" + SiteEndpoints.SITE_PATH_PARAM + "/timetable/events";

    // ---------------------------------------------------------------------------------------------
    //                                          Parameters
    // ---------------------------------------------------------------------------------------------
    public static final String PARAM_FNAME          = "firstName";
    public static final String PARAM_LNAME          = "lastName";
    public static final String PARAM_TIMETABLE_ID   = "timetableId";
    public static final String PARAM_PUSH_DEVICE_ID = "pushId";
    public static final String PARAM_SUBSCRIBED_LESSONS = "subscribedLessons";
    public static final String PARAM_CHANGES_TIME_STAMP = "timeStamp";

}
