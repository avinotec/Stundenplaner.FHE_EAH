package de.fhe.fhemobile.utils;

import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.fragments.timetable.TimeTableEventsFragment;
import de.fhe.fhemobile.fragments.timetable.TimeTableFragment;

/**
 * Created by paul on 16.03.15.
 */
public class TimeTableFactory {
    public static FeatureFragment getTimeTableFragment() {
        String chosenTimeTable = TimeTableSettings.fetchTimeTableSelection();
        FeatureFragment fragment;

        if (chosenTimeTable != null) {
            fragment = TimeTableEventsFragment.newInstance(chosenTimeTable);
        }
        else {
            fragment = TimeTableFragment.newInstance();
        }

        return fragment;
    }
}
