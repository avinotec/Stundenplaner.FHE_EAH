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

    public static final String BASE_URL_FHE_SERVER = "http://app.fh-erfurt.de:8080/fheapp/";
    public static final String BASE_URL_EAH         = "https://stundenplanung.eah-jena.de/";

    //Links for displaying as WebViews - Nadja 6.9.21
    //public static final String NEWS_ENDPOINT = "https://www.eah-jena.de/hochschule/nachrichten";
    public static final String NEWS_ENDPOINT = "https://www.eah-jena.de/hochschule/nachrichten#c10903";
    public static final String SEMESTERDATES_ENDPOINT = "https://www.eah-jena.de/hochschule/semestertermine";
    public static final String EVENTS_ENDPOINT = "https://www.eah-jena.de/veranstaltungskalender#c7547";
    public static final String JOBOFFERS_ENDPOINT = "https://stellenticket.eah-jena.de/de/offers/fulltextsearch/EAH-Jena/#Inhalt";


    //Testserver von Nadja
//    public static final String APP_SERVER_EAH = "http://192.168.178.157:80/api/";
    public static final String APP_SERVER_EAH = "https://wi-srv7.wi.eah-jena.de/api/";
    // https://wi-srv7.wi.eah-jena.de/api/fcm_register_user.php?
    public static final String URL_REGISTER_PUSH_NOTIFICATIONS_EAH = APP_SERVER_EAH + "fcm_register_user.php?";

    // ---------------------------------------------------------------------------------------------
    //                                           Endpoints
    // ---------------------------------------------------------------------------------------------

    public static final String RSS                  = "api/eah/news";
    public static final String CANTEEN              = "api/eah/canteens";
    public static final String SEMESTER             = "api/eah/semester"; //semester dates
    public static final String PHONEBOOK            = "api/eah/persons";  //not working
    //public static final String IMPRINT            = "api/eah/impress";    old, deprecated
    //public static final String IMPRINT_ENDPOINT = "https://www.eah-jena.de/impressum"; //URL fixed - Nadja 3.9.21
    public static final String IMPRINT_ENDPOINT = "https://www.eah-jena.de/impressum#c4092"; //URL fixed - Nadja 3.9.21

    public static final String WEATHER              = "api/eah/weather";
    //public static final String STUDYPROGRAMS            = "api/eah/timetable";
    public static final String STUDYPROGRAMS = "api/mobileapp/v1/studentset/list";
    //public static final String TIMETABLE     = "api/eah/timetable/events";
    public static final String TIMETABLE = "api/mobileapp/v1/studentset/";
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
