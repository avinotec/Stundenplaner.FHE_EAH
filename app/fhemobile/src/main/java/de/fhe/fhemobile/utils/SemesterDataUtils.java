package de.fhe.fhemobile.utils;

import de.fhe.fhemobile.vos.semesterdata.SemesterTimesVo;

/**
 * Created by Paul Cech on 13.05.15.
 */
public class SemesterDataUtils {

    public static String getHeadline(SemesterTimesVo _time) {
        String result = "";

        if(_time.getDate() != null) {
            result = _time.getDate().getName();
        }
        else if(_time.getPeriod() != null) {
            result = _time.getPeriod().getName();
        }

        return result;
    }

    public static String getSubHeadline(SemesterTimesVo _time) {
        String result = "";

        if(_time.getDate() != null) {
            result = _time.getDate().getDate();
        }
        else if(_time.getPeriod() != null) {
            result = _time.getPeriod().getBegin() + "  -  " + _time.getPeriod().getEnd();
        }

        return result;
    }

    private static final String LOG_TAG = SemesterDataUtils.class.getSimpleName();
}
