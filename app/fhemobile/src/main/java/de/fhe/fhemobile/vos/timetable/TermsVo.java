package de.fhe.fhemobile.vos.timetable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by paul on 12.03.15.
 */
public class TermsVo {

    public TermsVo() {
    }

    public String getId() {
        return mId;
    }

    public void setId(String _id) {
        mId = _id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String _title) {
        mTitle = _title;
    }

    public ArrayList<StudyGroupVo> getStudyGroups() {
        return mStudyGroups;
    }

    public void setStudyGroups(ArrayList<StudyGroupVo> _studyGroups) {
        mStudyGroups = _studyGroups;
    }

    @SerializedName("id")
    private String                  mId;

    @SerializedName("title")
    private String                  mTitle;

    @SerializedName("studyGroups")
    private ArrayList<StudyGroupVo> mStudyGroups;
}
