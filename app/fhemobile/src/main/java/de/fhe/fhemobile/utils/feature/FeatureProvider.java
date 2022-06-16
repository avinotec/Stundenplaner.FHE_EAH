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
public final class FeatureProvider {

    /**
     * Loads the feature settings from the corresponding XML-file into the Features class
     * @param _Context
     */
    public static void loadFeatures(final Context _Context) {
        Features.NEWS           = _Context.getResources().getBoolean(R.bool.feature_news);
        Features.PHONEBOOK      = _Context.getResources().getBoolean(R.bool.feature_phonebook);
        Features.CANTEEN        = _Context.getResources().getBoolean(R.bool.feature_canteen);
        Features.MAPS           = _Context.getResources().getBoolean(R.bool.feature_maps);
        Features.SEMESTER_DATA  = _Context.getResources().getBoolean(R.bool.feature_semester_data);
        Features.TIMETABLE      = _Context.getResources().getBoolean(R.bool.feature_timetable);
        Features.MYSCHEDULE = _Context.getResources().getBoolean(R.bool.feature_myschedule);
        Features.IMPRINT        = _Context.getResources().getBoolean(R.bool.feature_imprint);
        Features.EVENTS         = _Context.getResources().getBoolean(R.bool.feature_events);
        Features.NAVIGATION     = _Context.getResources().getBoolean(R.bool.feature_navigation);
        Features.JOBOFFERS      = _Context.getResources().getBoolean(R.bool.feature_joboffers);
    }

    /**
     *
     * @return
     */
    public static List<DrawerItem> getFeaturedItems() {
        final ArrayList<DrawerItem> list = new ArrayList<>();

        if (Features.TIMETABLE) {
            list.add(new DrawerItem(Features.FeatureId.TIMETABLE, getFeatureTitle(Features.FeatureId.TIMETABLE)));
        }

        if (Features.MYSCHEDULE) {
            list.add(new DrawerItem(Features.FeatureId.MYSCHEDULE, getFeatureTitle(Features.FeatureId.MYSCHEDULE)));
        }

        if (Features.CANTEEN) {
            list.add(new DrawerItem(Features.FeatureId.CANTEEN, getFeatureTitle(Features.FeatureId.CANTEEN)));
        }

        if (Features.MAPS) {
            list.add(new DrawerItem(Features.FeatureId.MAPS, getFeatureTitle(Features.FeatureId.MAPS)));
        }

        if (Features.NAVIGATION) {
            list.add(new DrawerItem(Features.FeatureId.NAVIGATION, getFeatureTitle(Features.FeatureId.NAVIGATION)));
        }

        if (Features.NEWS) {
            list.add(new DrawerItem(Features.FeatureId.NEWS, getFeatureTitle(Features.FeatureId.NEWS)));
        }

        if (Features.EVENTS) {
            list.add(new DrawerItem(Features.FeatureId.EVENTS, getFeatureTitle(Features.FeatureId.EVENTS)));
        }

        if (Features.SEMESTER_DATA) {
            list.add(new DrawerItem(Features.FeatureId.SEMESTER_DATA, getFeatureTitle(Features.FeatureId.SEMESTER_DATA)));
        }

//        if (Features.PHONEBOOK) {
//            list.add(new DrawerItem(Features.FeatureId.PHONEBOOK, getFeatureTitle(Features.FeatureId.PHONEBOOK)));
//        }

        if(Features.JOBOFFERS){
            list.add(new DrawerItem(Features.FeatureId.JOBOFFERS, getFeatureTitle(Features.FeatureId.JOBOFFERS)));
        }

        if (Features.IMPRINT) {
            list.add(new DrawerItem(Features.FeatureId.IMPRINT, getFeatureTitle(Features.FeatureId.IMPRINT)));
        }

        return list;
    }

    /**
     *
     * @param _FeatureId
     * @return
     */
    public static final String getFeatureTitle(final int _FeatureId) {
        String result = "";
        int stringRes = -1;

        switch (_FeatureId) {
            case Features.FeatureId.TIMETABLE:       stringRes = R.string.drawer_timetable; break;
            case Features.FeatureId.MYSCHEDULE:     stringRes = R.string.drawer_myschedule; break;
            case Features.FeatureId.CANTEEN:         stringRes = R.string.drawer_canteen;     break;
            case Features.FeatureId.MAPS:            stringRes = R.string.drawer_campus;    break;
            case Features.FeatureId.NAVIGATION:      stringRes = R.string.drawer_navigation; break;
            case Features.FeatureId.NEWS:            stringRes = R.string.drawer_news;      break;
            case Features.FeatureId.EVENTS:          stringRes = R.string.drawer_events; break;
            case Features.FeatureId.SEMESTER_DATA:   stringRes = R.string.drawer_semesterdates;     break;
            case Features.FeatureId.PHONEBOOK:       stringRes = R.string.drawer_persons;   break;
            case Features.FeatureId.JOBOFFERS:       stringRes = R.string.drawer_joboffers; break;
            case Features.FeatureId.IMPRINT:         stringRes = R.string.drawer_imprint; break;
        }

        if (stringRes != -1) {
            result = Main.getSafeString(stringRes);
        }

        return result;
    }

}
