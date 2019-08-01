package de.fhe.fhemobile.vos.timetable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by paul on 12.03.15.
 */
public class StudyCourseVo {

    public StudyCourseVo() {
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

    public ArrayList<TermsVo> getTerms() {
        return mTerms;
    }

    public void setTerms(ArrayList<TermsVo> _terms) {
        mTerms = _terms;
    }

    @SerializedName("id")
    private String              mId;

    @SerializedName("title")
    private String              mTitle;

    @SerializedName("terms")
    private ArrayList<TermsVo>  mTerms;
}
