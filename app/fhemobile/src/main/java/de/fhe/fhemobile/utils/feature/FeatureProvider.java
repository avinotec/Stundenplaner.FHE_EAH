package de.fhe.fhemobile.utils.feature;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.drawer.DrawerItem;

/**
 * Created by paul on 18.03.15.
 */
public class FeatureProvider {

    /**
     * Loads the feature settings from the corresponding XML-file into the Features class
     * @param _Context
     */
    public static void loadFeatures(Context _Context) {
        Features.NEWS           = _Context.getResources().getBoolean(R.bool.feature_news);
        Features.PHONEBOOK      = _Context.getResources().getBoolean(R.bool.feature_phonebook);
        Features.MENSA          = _Context.getResources().getBoolean(R.bool.feature_mensa);
        Features.MAPS           = _Context.getResources().getBoolean(R.bool.feature_maps);
        Features.SEMESTER_DATA  = _Context.getResources().getBoolean(R.bool.feature_semester_data);
        Features.TIMETABLE      = _Context.getResources().getBoolean(R.bool.feature_timetable);
        Features.MYTIMETABLE    = _Context.getResources().getBoolean(R.bool.feature_mytimetable);
        Features.IMPRESS        = _Context.getResources().getBoolean(R.bool.feature_impress);
    }

    public static List<DrawerItem> getFeaturedItems() {
        ArrayList<DrawerItem> list = new ArrayList<>();

        if (Features.NEWS) {
            list.add(new DrawerItem(FeatureId.NEWS, getFeatureTitle(FeatureId.NEWS)));
        }

        if (Features.TIMETABLE) {
            list.add(new DrawerItem(FeatureId.TIMETABLE, getFeatureTitle(FeatureId.TIMETABLE)));
        }

        if (Features.MYTIMETABLE) {
            list.add(new DrawerItem(FeatureId.MYTIMETABLE, getFeatureTitle(FeatureId.MYTIMETABLE)));
        }

        if (Features.PHONEBOOK) {
            list.add(new DrawerItem(FeatureId.PHONEBOOK, getFeatureTitle(FeatureId.PHONEBOOK)));
        }

        if (Features.MENSA) {
            list.add(new DrawerItem(FeatureId.MENSA, getFeatureTitle(FeatureId.MENSA)));
        }

        if (Features.MAPS) {
            list.add(new DrawerItem(FeatureId.MAPS, getFeatureTitle(FeatureId.MAPS)));
        }

        if (Features.SEMESTER_DATA) {
            list.add(new DrawerItem(FeatureId.SEMESTER_DATA, getFeatureTitle(FeatureId.SEMESTER_DATA)));
        }

        if (Features.IMPRESS) {
            list.add(new DrawerItem(FeatureId.IMPRESS, getFeatureTitle(FeatureId.IMPRESS)));
        }

        return list;
    }

    public static String getFeatureTitle(int _FeatureId) {
        String result = "";
        int stringRes = -1;

        switch (_FeatureId) {
            case FeatureId.NEWS:            stringRes = R.string.drawer_news;      break;
            case FeatureId.PHONEBOOK:       stringRes = R.string.drawer_persons;   break;
            case FeatureId.MENSA:           stringRes = R.string.drawer_mensa;     break;
            case FeatureId.MAPS:            stringRes = R.string.drawer_campus;    break;
            case FeatureId.SEMESTER_DATA:   stringRes = R.string.drawer_dates;     break;
            case FeatureId.TIMETABLE:       stringRes = R.string.drawer_timetable; break;
            case FeatureId.MYTIMETABLE:       stringRes = R.string.drawer_mytimetable; break;
            case FeatureId.IMPRESS:         stringRes = R.string.drawer_impressum; break;
        }

        if (stringRes != -1) {
            result = Main.getSafeString(stringRes);
        }

        return result;
    }

}
