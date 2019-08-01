package de.fhe.fhemobile.vos.timetable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 12.03.15.
 */
public class StudyGroupVo {

    public StudyGroupVo() {
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String _title) {
        mTitle = _title;
    }

    public String getTimeTableId() {
        return mTimeTableId;
    }

    public void setTimeTableId(String _timeTableId) {
        mTimeTableId = _timeTableId;
    }

    @SerializedName("title")
    private String mTitle;

    @SerializedName("timetableId")
    private String mTimeTableId;
}
