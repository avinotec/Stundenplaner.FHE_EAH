package de.fhe.fhemobile.vos.myschedule;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for a timetable module
 */
public class ModuleVo {

    public Map<String, MyScheduleEventSetVo> getEventSets() {
        return mEventSets;
    }

    //@SerializedName("dataModule")
    //data like id and name
    //not needed, that's why not implemented

    @SerializedName("dataActivity")
    private Map<String, MyScheduleEventSetVo> mEventSets = new HashMap<>();
}
