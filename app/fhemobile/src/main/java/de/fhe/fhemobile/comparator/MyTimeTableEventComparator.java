package de.fhe.fhemobile.comparator;

import java.util.Comparator;
import de.fhe.fhemobile.vos.mytimetable.MyTimeTableEventVo;

public class MyTimeTableEventComparator implements Comparator<MyTimeTableEventVo> {

    @Override
    public int compare(MyTimeTableEventVo o1, MyTimeTableEventVo o2) {
        return Long.compare(o1.getStartDateTime(), o2.getEndDateTime());
    }
}
