package de.fhe.fhemobile.network;

/**
 * Created by kern on 15.09.14.
 */
public class Endpoints {
    public static final String LIVE_URL             = "http://193.174.232.89:8080";
    public static final String LOCAL_URL            = "http://192.168.1.102:8080";
    public static final String SIMON_LOCAL_URL      = "http://10.12.24.58:8000";

    public static final String BASE_URL             = LIVE_URL;
//    public static final String BASE_URL             = SIMON_LOCAL_URL;
    public static final String APP_NAME             = "/fheapp/";


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

}
