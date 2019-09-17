package de.fhe.fhemobile.vos.timetable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by paul on 12.03.15.
 */
public class StudyCourseVo implements Parcelable {

    public StudyCourseVo() {
    }

    protected StudyCourseVo(Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
        mTerms = in.createTypedArrayList(TermsVo.CREATOR);
    }

    public static final Creator<StudyCourseVo> CREATOR = new Creator<StudyCourseVo>() {
        @Override
        public StudyCourseVo createFromParcel(Parcel in) {
            return new StudyCourseVo(in);
        }

        @Override
        public StudyCourseVo[] newArray(int size) {
            return new StudyCourseVo[size];
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeTypedList(mTerms);
    }
}
