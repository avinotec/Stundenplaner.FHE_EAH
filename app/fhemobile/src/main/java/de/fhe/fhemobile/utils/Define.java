/*
 *  Copyright (c) 2020-2022 Ernst-Abbe-Hochschule Jena
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

/**
 * we define all static defines centrally here.
 * constants etc.
 */
public final class Define {

    //!!!!!!!!!!!!!NOTE: endpoints and URLs are defined in class Endpoints

    //switches for certain features
    public static final boolean ENABLE_MYSCHEDULE_UPDATING = true;
    public static final boolean ENABLE_CALENDAR_SYNC = true;
    //enable feature news (the original one, not the temporary webview solution)
    public static final boolean ENABLE_V1_NEWS = false;

    //created by Nadja 11.03.2022
    public static final class Canteen {
        static final String PREF_SELECTED_CANTEENS = "selectedCanteens";      // $NON-NLS

        public static final String SP_CANTEEN = "spCanteen";
        public static final String SP_KEY_CANTEEN = "canteen";
        public static final String SP_KEY_CANTEEN_CARD = "spCanteenCardBalance";

        public static final String KEY_BALANCE = "CardBalance.BALANCE";
        public static final String KEY_LAST_TRANSACTION = "CardBalance.LAST_TRANSACTION";
    }

    public static final class News {
        static final String PREF_CHOSEN_NEWS_CATEGORY = "chosenNewsCategory"; // $NON-NLS
    }

    //created by Nadja 11.03.2022
    public static final class Timetable {
        public static final String KEY_TIMETABLE_ID = "paramTimeTableId";            // $NON-NLS

        public static final String SP_TIMETABLE = "spTimeTable"; //$NON-NLS
        public static final String SP_TIMETABLE_SETTINGS = "spTimeTableFavourit";         // $NON-NLS
        public static final String PREF_CHOSEN_TIMETABLE_ID = "prefChosenTimeTableId";    // $NON-NLS

        public static final String KEY_TIMETABLE_WEEK = "paramTimeTableWeek";            // $NON-NLS

    }

    //created by Nadja 11.03.2022
    public static final class MySchedule {
        public static final String SP_MYSCHEDULE = "my_schedule";                    // $NON-NLS
        public static final String PREF_SUBSCRIBED_EVENTSERIES = "my_schedule_subscribed_eventseries";    // $NON-NLS
        public static final String PREF_DATA_LAST_UPDATED = "my_schedule_last_updated";    // $NON-NLS
        public static final String PREFS_APP_LAST_OPENED = "lastAppOpened";                    // $NON-NLS

    }

    //created by Nadja 17.11.2021
    public static final class Maps {
        public static final String BUILDING_03_02_01_FLOOR_UG1 = "building_03_02_01_floor_ug1";   //$NON-NLS
        public static final String BUILDING_03_02_01_FLOOR_00 = "building_03_02_01_floor_00";   //$NON-NLS
        public static final String BUILDING_03_02_01_FLOOR_01 = "building_03_02_01_floor_01";   //$NON-NLS
        public static final String BUILDING_03_02_01_FLOOR_02 = "building_03_02_01_floor_02";   //$NON-NLS
        public static final String BUILDING_03_02_01_FLOOR_03 = "building_03_02_01_floor_03";   //$NON-NLS
        public static final String BUILDING_03_02_01_FLOOR_04 = "building_03_02_01_floor_04";   //$NON-NLS
        public static final String BUILDING_03_02_01_GROUND = "building_03_02_01_ground";    //$NON-NLS
        public static final String BUILDING_04_FLOOR_UG1 = "building_04_floor_ug1";   //$NON-NLS
        public static final String BUILDING_04_FLOOR_00 = "building_04_floor_00";   //$NON-NLS
        public static final String BUILDING_04_FLOOR_01 = "building_04_floor_01";   //$NON-NLS
        public static final String BUILDING_04_FLOOR_02 = "building_04_floor_02";   //$NON-NLS
        public static final String BUILDING_04_FLOOR_03 = "building_04_floor_03";   //$NON-NLS
        public static final String BUILDING_04_GROUND = "building_04_ground";    //$NON-NLS
        public static final String BUILDING_05_FLOOR_UG1 = "building_05_floor_ug1";   //$NON-NLS
        public static final String BUILDING_05_FLOOR_UG2 = "building_05_floor_ug2";   //$NON-NLS
        public static final String BUILDING_05_FLOOR_00 = "building_05_floor_00";   //$NON-NLS
        public static final String BUILDING_05_FLOOR_01 = "building_05_floor_01";   //$NON-NLS
        public static final String BUILDING_05_FLOOR_02 = "building_05_floor_02";   //$NON-NLS
        public static final String BUILDING_05_FLOOR_03 = "building_05_floor_03";   //$NON-NLS
        public static final String BUILDING_05_FLOOR_3Z = "building_05_floor_3Z";   //$NON-NLS
        public static final String BUILDING_05_GROUND = "building_05_ground";   //$NON-NLS
        public static final String BUILDING_06_GROUND = "building_06_ground";   //$NON-NLS
        public static final String BUILDING_06_FLOOR_UG1 = "building_06_floor_ug1";   //$NON-NLS
    }


    public static final class Navigation {
        public static final String FLOORCONNECTION_TYPE_STAIR = "staircase";    //$NON-NLS
        public static final String FLOORCONNECTION_TYPE_ELEVATOR = "elevator";  //$NON-NLS

        //Size of the grid overlying the floorplan (unit: cells - needs to be integer)
        //Note: cell numbering at gridded PNGs (docs folder) starts at 0 -> width/height = number + 1
        public static final int cellgrid_width = 45;
        public static final int cellgrid_height = 30;

        //for A* algorithm
        public static final int COSTS_CELL = 1;
        public static final int COSTS_ROOM = 3;
        public static final int COSTS_EXIT = 1;
        // floorconnections already count twice
        // because the cell at the entered floor and the one at the reached floor are both counted
        public static final int COSTS_FLOORCONNECTION = 1;

        //shared preferences used to save latest user input for navigation destination
        public static final String SP_NAVIGATION = "navigation";    //$NON-NLS

        //communication between NavigationDialogFragment and NavigationScannerFragment
        public static final String REQUEST_SCANNED_START_ROOM = "requestScannedStartRoom";  //$NON-NLS
        public static final String KEY_SCANNED_ROOM = "scannedRoom";    //$NON-NLS
    }

    /**
     * Define Class for Push notifications from Google Firebase to the App
     */
    public static final class PushNotifications {
        public static final String CHANNEL_ID = "de.fhe.fhemobile.push";  //$NON-NLS
        public static final long[] VIBRATION_PATTERN = {1000, 500, 1000, 0};

        //the following fields need to be synchronized with the backend (php and database)
        public static final String VALUE_UNDEFINED = "0";
        public static final String VALUE_TIMETABLE_CHANGED = "1";
        public static final String VALUE_EXAM_ADDED = "2";

        public static final String VALUE_ANDROID = "0";
        public static final String VALUE_LANG_DE = "DE";
        public static final String VALUE_LANG_EN = "EN";
    }


    /**
     * milliseconds within a double-click has to be executed to leave application
     */
    public static final long APP_CLOSING_DOUBLECLICK_DELAY_TIME = 2000L;


    /* Utility classes have all fields and methods declared as static.
    Creating private constructors in utility classes prevents them from being accidentally instantiated. */
    private Define() {
    }


}
