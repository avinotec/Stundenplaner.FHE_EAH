package de.fhe.fhemobile.vos.timetable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 12.03.15.
 */
public class StudyGroupVo implements Parcelable {

    public StudyGroupVo() {
    }

    protected StudyGroupVo(Parcel in) {
        mTitle = in.readString();
        mTimeTableId = in.readString();
    }

    public static final Creator<StudyGroupVo> CREATOR = new Creator<StudyGroupVo>() {
        @Override
        public StudyGroupVo createFromParcel(Parcel in) {
            return new StudyGroupVo(in);
        }

        @Override
        public StudyGroupVo[] newArray(int size) {
            return new StudyGroupVo[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mTimeTableId);
    }
}
