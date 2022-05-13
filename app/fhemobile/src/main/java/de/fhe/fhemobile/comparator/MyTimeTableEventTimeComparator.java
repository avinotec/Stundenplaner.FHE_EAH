package de.fhe.fhemobile.comparator;

import java.util.Comparator;

import de.fhe.fhemobile.vos.mytimetable.MyTimeTableEventTimeVo;

public class MyTimeTableEventTimeComparator implements Comparator<MyTimeTableEventTimeVo> {

    @Override
    public int compare(MyTimeTableEventTimeVo o1, MyTimeTableEventTimeVo o2) {
        return Long.compare(o1.getStartDateTimeInSec(), o2.getStartDateTimeInSec());
    }
}
