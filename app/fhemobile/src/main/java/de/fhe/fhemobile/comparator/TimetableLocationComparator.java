package de.fhe.fhemobile.comparator;

import java.util.Comparator;

import de.fhe.fhemobile.vos.timetable.TimetableLocationVo;

/**
 * Comparator for {@link TimetableLocationVo} objects,
 * needed for sorting location lists for proper comparing via "equals"
 *
 * Created by Nadja - 28.11.2022
 */
public class TimetableLocationComparator implements Comparator<TimetableLocationVo> {

    @Override
    public int compare(TimetableLocationVo t0, TimetableLocationVo t1) {
        return t0.getId().compareTo(t1.getId());
    }
}
