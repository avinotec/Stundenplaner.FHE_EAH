package de.fhe.fhemobile.vos.timetable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by paul on 12.03.15.
 */
public class TermsVo implements Parcelable {

    public TermsVo() {
    }

    protected TermsVo(Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
        mStudyGroups = in.createTypedArrayList(StudyGroupVo.CREATOR);
    }

    public static final Creator<TermsVo> CREATOR = new Creator<TermsVo>() {
        @Override
        public TermsVo createFromParcel(Parcel in) {
            return new TermsVo(in);
        }

        @Override
        public TermsVo[] newArray(int size) {
            return new TermsVo[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeTypedList(mStudyGroups);
    }
}
