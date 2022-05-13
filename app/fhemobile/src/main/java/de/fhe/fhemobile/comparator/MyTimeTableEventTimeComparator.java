package de.fhe.fhemobile.comparator;

import java.util.Comparator;

import de.fhe.fhemobile.vos.mytimetable.MyTimeTableEventDateVo;

public class MyTimeTableEventTimeComparator implements Comparator<MyTimeTableEventDateVo> {

    @Override
    public int compare(MyTimeTableEventDateVo o1, MyTimeTableEventDateVo o2) {
        return Long.compare(o1.getStartDateTimeInSec(), o2.getStartDateTimeInSec());
    }
}
