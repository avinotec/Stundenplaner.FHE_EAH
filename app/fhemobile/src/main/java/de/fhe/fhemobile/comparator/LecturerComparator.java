package de.fhe.fhemobile.comparator;

import java.util.Comparator;

import de.fhe.fhemobile.vos.timetable.LecturerVo;

/**
 * Comparator for {@link LecturerVo} objects,
 * needed for sorting lecturer lists for proper comparing via "equals"
 *
 * Created by Nadja - 28.11.2022
 */
public class LecturerComparator implements Comparator<LecturerVo> {

    @Override
    public int compare(final LecturerVo l0, final LecturerVo l1) {
        return l0.getId().compareTo(l1.getId());
    }
}
