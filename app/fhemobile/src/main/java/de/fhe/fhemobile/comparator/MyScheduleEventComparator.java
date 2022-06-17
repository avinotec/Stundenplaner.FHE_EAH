package de.fhe.fhemobile.comparator;

import java.util.Comparator;

import de.fhe.fhemobile.vos.myschedule.MyScheduleEventVo;

public class MyScheduleEventComparator implements Comparator<MyScheduleEventVo> {

    @Override
    public int compare(MyScheduleEventVo o1, MyScheduleEventVo o2) {
        return Long.compare(o1.getStartDateTime(), o2.getEndDateTime());
    }
}
