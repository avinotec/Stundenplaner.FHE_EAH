package de.fhe.fhemobile.comparator;

import java.util.Comparator;

import de.fhe.fhemobile.vos.timetable.TimeTableLocationVo;

/**
 * Comparator for {@link TimeTableLocationVo} objects,
 * needed for sorting location lists for proper comparing via "equals"
 *
 * Created by Nadja - 28.11.2022
 */
public class TimetableLocationComparator implements Comparator<TimeTableLocationVo> {

    @Override
    public int compare(TimeTableLocationVo t0, TimeTableLocationVo t1) {
        return t0.getId().compareTo(t1.getId());
    }
}
