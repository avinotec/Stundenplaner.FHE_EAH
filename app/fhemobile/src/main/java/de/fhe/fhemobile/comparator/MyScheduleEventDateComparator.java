package de.fhe.fhemobile.comparator;

import java.util.Comparator;

import de.fhe.fhemobile.vos.myschedule.MyScheduleEventDateVo;

public class MyScheduleEventDateComparator implements Comparator<MyScheduleEventDateVo> {

    @Override
    public int compare(MyScheduleEventDateVo o1, MyScheduleEventDateVo o2) {
        return Long.compare(o1.getStartDateTimeInSec(), o2.getStartDateTimeInSec());
    }
}
