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
package de.fhe.fhemobile.network;

/**
 *
 */
public final class Endpoints {

    public static final String LIVE_URL             = "http://app.fh-erfurt.de:8080";
    //public static final String LIVE_URL             = "http://193.174.232.89:8080";
    //public static final String LOCAL_URL            = "http://192.168.1.102:8080";
    //public static final String SIMON_LOCAL_URL      = "http://10.12.24.58:8000";

    public static final String BASE_URL             = LIVE_URL;
    public static final String BASE_URL_EAH         = "https://stundenplanung.eah-jena.de/";
    public static final String APP_NAME             = "/fheapp/";

    public static final String IMPRINT_ENDPOINT = "https://www.eah-jena.de/impressum"; //URL fixed - Nadja 3.9.21
    //Links for displaying as WebViews - Nadja 6.9.21
    public static final String NEWS_ENDPOINT = "https://www.eah-jena.de/hochschule/nachrichten";
    public static final String SEMESTERDATES_ENDPOINT = "https://www.eah-jena.de/hochschule/semestertermine";
    public static final String EVENTS_ENDPOINT = "https://www.eah-jena.de/veranstaltungskalender";
    public static final String JOBOFFERS_ENDPOINT = "https://stellenticket.eah-jena.de/de/offers/fulltextsearch/EAH-Jena/#Inhalt";

    //TODO
    //Testserver von Moritz
    //public static final String APP_SERVER_EAH = "https://lustigtestt.de/fhjena/rest_api/public/changes";
    //Testserver von Nadja
    public static final String APP_SERVER_EAH = "http://192.168.178.157:80/api/";
    //TODO der Server muss noch richtig in Gang gebracht werden.
//    public static final String APP_SERVER_EAH = "http://wi-srv7.wi.eah-jena.de/api/";
    public static final String URL_REGISTER_PUSH_NOTIFICATIONS_EAH = APP_SERVER_EAH + "fcm_register_user.php?";

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
    //                                           Endpoints
    // ---------------------------------------------------------------------------------------------

    public static final String RSS                  = "api/" + SiteEndpoints.SITE_PATH_PARAM + "/news";
    public static final String CANTEEN              = "api/" + SiteEndpoints.SITE_PATH_PARAM + "/canteens";
    public static final String SEMESTER             = "api/" + SiteEndpoints.SITE_PATH_PARAM + "/semester"; //semester dates
    public static final String PHONEBOOK            = "api/" + SiteEndpoints.SITE_PATH_PARAM + "/persons";  //not working
    //public static final String IMPRINT            = "api/" + SiteEndpoints.SITE_PATH_PARAM + "/impress";
    public static final String WEATHER              = "api/" + SiteEndpoints.SITE_PATH_PARAM + "/weather";
    //public static final String TIMETABLE            = "api/" + SiteEndpoints.SITE_PATH_PARAM + "/timetable";
    public static final String TIMETABLE            = "api/mobileapp/v1/studentset/list";
    //public static final String TIMETABLE_EVENTS     = "api/" + SiteEndpoints.SITE_PATH_PARAM + "/timetable/events";
    public static final String TIMETABLE_EVENTS     = "api/mobileapp/v1/studentset/";
    public static final String MY_SCHEDULE          = "api/mobileapp/v1/pos/";
    public static final String MODULE               = "api/mobileapp/v1/module/";


    // ---------------------------------------------------------------------------------------------
    //                                          Parameters
    // ---------------------------------------------------------------------------------------------
    public static final String PARAM_FNAME          = "firstName";
    public static final String PARAM_LNAME          = "lastName";
    public static final String PARAM_STUDYGROUP_ID  = "studyGroupId";
    public static final String PARAM_SEMESTER_ID    = "semesterId";
    public static final String PARAM_MODULE_ID      = "moduleId";
    public static final String PARAM_CANTEEN_ID     = "canteenId";
    public static final String PARAM_NEWSLIST_ID    = "newsListId";

    /* Utility classes have all fields and methods declared as static.
    Creating private constructors in utility classes prevents them from being accidentally instantiated. */
	private Endpoints() {
	}
}
