package de.fhe.fhemobile.vos.timetable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by paul on 12.03.15.
 */
public class TimeTableResponse {

    public TimeTableResponse() {
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String _title) {
        mTitle = _title;
    }

    public String getModified() {
        return mModified;
    }

    public void setModified(String _modified) {
        mModified = _modified;
    }

    public ArrayList<StudyCourseVo> getStudyCourses() {
        return mStudyCourses;
    }

    public void setStudyCourses(ArrayList<StudyCourseVo> _studyCourses) {
        mStudyCourses = _studyCourses;
    }

    @SerializedName("title")
    private String                   mTitle;

    @SerializedName("modified")
    private String                   mModified;

    @SerializedName("courseOfStudies")
    private ArrayList<StudyCourseVo> mStudyCourses;
}
