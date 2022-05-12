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
package de.fhe.fhemobile.utils.feature;

/**
 *
 */
public class Features {

    //TODO Features
    public static boolean NEWS            = false;
    public static boolean PHONEBOOK       = false;
    public static boolean CANTEEN         = false;
    public static boolean MAPS            = false;
    public static boolean SEMESTER_DATA   = false;
    public static boolean TIMETABLE       = false;
    public static boolean IMPRINT         = false;
    public static boolean MYTIMETABLE     = false;
    public static boolean EVENTS          = false;
    public static boolean NAVIGATION      = false;
    public static boolean JOBOFFERS       = false;

    /**
     *
     */
    public interface FeatureId {
        int NEWS            = 0;
        int PHONEBOOK       = 1;
        int CANTEEN         = 2;
        int MAPS            = 3;
        int SEMESTER_DATA   = 4;
        int TIMETABLE       = 5;
        int IMPRINT         = 6;
        int MYTIMETABLE     = 7;
        int EVENTS          = 8;
        int NAVIGATION      = 9;
        int JOBOFFERS       = 10;
    }
}
