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

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.drawer.DrawerItem;

/**
 *
 */
public class FeatureProvider {

    /**
     * Loads the feature settings from the corresponding XML-file into the Features class
     * @param _Context
     */
    public static void loadFeatures(final Context _Context) {
        Features.NEWS           = _Context.getResources().getBoolean(R.bool.feature_news);
        Features.PHONEBOOK      = _Context.getResources().getBoolean(R.bool.feature_phonebook);
        Features.MENSA          = _Context.getResources().getBoolean(R.bool.feature_mensa);
        Features.MAPS           = _Context.getResources().getBoolean(R.bool.feature_maps);
        Features.SEMESTER_DATA  = _Context.getResources().getBoolean(R.bool.feature_semester_data);
        Features.TIMETABLE      = _Context.getResources().getBoolean(R.bool.feature_timetable);
        Features.MYTIMETABLE    = _Context.getResources().getBoolean(R.bool.feature_mytimetable);
        Features.IMPRESS        = _Context.getResources().getBoolean(R.bool.feature_impress);
        Features.EVENTS         = _Context.getResources().getBoolean(R.bool.feature_events);
        Features.NAVIGATION     = _Context.getResources().getBoolean(R.bool.feature_navigation);
    }

    /**
     *
     * @return
     */
    public static List<DrawerItem> getFeaturedItems() {
        final ArrayList<DrawerItem> list = new ArrayList<>();

        //TODO Features
        if (Features.TIMETABLE) {
            list.add(new DrawerItem(FeatureId.TIMETABLE, getFeatureTitle(FeatureId.TIMETABLE)));
        }

        if (Features.MYTIMETABLE) {
            list.add(new DrawerItem(FeatureId.MYTIMETABLE, getFeatureTitle(FeatureId.MYTIMETABLE)));
        }

        if (Features.MENSA) {
            list.add(new DrawerItem(FeatureId.MENSA, getFeatureTitle(FeatureId.MENSA)));
        }

        if (Features.MAPS) {
            list.add(new DrawerItem(FeatureId.MAPS, getFeatureTitle(FeatureId.MAPS)));
        }

        if (Features.NAVIGATION) {
            list.add(new DrawerItem(FeatureId.NAVIGATION, getFeatureTitle(FeatureId.NAVIGATION)));
        }

        if (Features.NEWS) {
            list.add(new DrawerItem(FeatureId.NEWS, getFeatureTitle(FeatureId.NEWS)));
        }

        if (Features.EVENTS) {
            list.add(new DrawerItem(FeatureId.EVENTS, getFeatureTitle(FeatureId.EVENTS)));
        }

        if (Features.SEMESTER_DATA) {
            list.add(new DrawerItem(FeatureId.SEMESTER_DATA, getFeatureTitle(FeatureId.SEMESTER_DATA)));
        }

        if (Features.PHONEBOOK) {
            list.add(new DrawerItem(FeatureId.PHONEBOOK, getFeatureTitle(FeatureId.PHONEBOOK)));
        }

        if (Features.IMPRESS) {
            list.add(new DrawerItem(FeatureId.IMPRESS, getFeatureTitle(FeatureId.IMPRESS)));
        }

        return list;
    }

    /**
     *
     * @param _FeatureId
     * @return
     */
    public final static String getFeatureTitle(final int _FeatureId) {
        String result = "";
        int stringRes = -1;

        //TODO Features
        switch (_FeatureId) {
            case FeatureId.NEWS:            stringRes = R.string.drawer_news;      break;
            case FeatureId.PHONEBOOK:       stringRes = R.string.drawer_persons;   break;
            case FeatureId.MENSA:           stringRes = R.string.drawer_mensa;     break;
            case FeatureId.MAPS:            stringRes = R.string.drawer_campus;    break;
            case FeatureId.EVENTS:          stringRes = R.string.drawer_events; break;
            case FeatureId.SEMESTER_DATA:   stringRes = R.string.drawer_semesterdates;     break;
            case FeatureId.TIMETABLE:       stringRes = R.string.drawer_timetable; break;
            case FeatureId.MYTIMETABLE:     stringRes = R.string.drawer_mytimetable; break;
            case FeatureId.IMPRESS:         stringRes = R.string.drawer_impressum; break;
            case FeatureId.NAVIGATION:      stringRes = R.string.drawer_navigation; break;
        }

        if (stringRes != -1) {
            result = Main.getSafeString(stringRes);
        }

        return result;
    }

}
