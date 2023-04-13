package de.fhe.fhemobile.comparator;

import java.util.Comparator;

import de.fhe.fhemobile.vos.myschedule.MyScheduleEventDateVo;

public class MyScheduleEventDateComparator implements Comparator<MyScheduleEventDateVo> {

    @Override
    public int compare(final MyScheduleEventDateVo o1, final MyScheduleEventDateVo o2) {
        return Long.compare(o1.getStartTime(), o2.getStartTime());
    }
}
