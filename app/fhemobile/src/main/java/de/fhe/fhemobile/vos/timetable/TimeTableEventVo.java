package de.fhe.fhemobile.vos.timetable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 16.03.15.
 */
public class TimeTableEventVo implements Parcelable {

    public TimeTableEventVo() {
    }

    public long getStartDate() {
        return mStartDate;
    }

    public void setStartDate(long _startDate) {
        mStartDate = _startDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String _title) {
        mTitle = _title;
    }

    public String getShortTitle() {
        return mShortTitle;
    }

    public void setShortTitle(String _shortTitle) {
        mShortTitle = _shortTitle;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String _date) {
        mDate = _date;
    }

    public String getDayOfWeek() {
        return mDayOfWeek;
    }

    public void setDayOfWeek(String _dayOfWeek) {
        mDayOfWeek = _dayOfWeek;
    }

    public int getWeekOfYear() {
        return mWeekOfYear;
    }

    public void setWeekOfYear(int _weekOfYear) {
        mWeekOfYear = _weekOfYear;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public void setStartTime(String _startTime) {
        mStartTime = _startTime;
    }

    public String getEndTime() {
        return mEndTime;
    }

    public void setEndTime(String _endTime) {
        mEndTime = _endTime;
    }

    public String getLecturer() {
        return mLecturer;
    }

    public void setLecturer(String _lecturer) {
        mLecturer = _lecturer;
    }

    public String getRoom() {
        return mRoom;
    }

    public void setRoom(String _room) {
        mRoom = _room;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String _uid) {
        mUid = _uid;
    }

    @SerializedName("startDate")
    private long mStartDate;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("shortTitle")
    private String mShortTitle;

    @SerializedName("date")
    private String mDate;

    @SerializedName("dayOfWeek")
    private String mDayOfWeek;

    @SerializedName("weekOfYear")
    private int mWeekOfYear;

    @SerializedName("startTime")
    private String mStartTime;

    @SerializedName("endTime")
    private String mEndTime;

    @SerializedName("lecturer")
    private String mLecturer;

    @SerializedName("room")
    private String mRoom;

    @SerializedName("uid")
    private String mUid;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mStartDate);
        dest.writeString(this.mTitle);
        dest.writeString(this.mShortTitle);
        dest.writeString(this.mDate);
        dest.writeString(this.mDayOfWeek);
        dest.writeInt(this.mWeekOfYear);
        dest.writeString(this.mStartTime);
        dest.writeString(this.mEndTime);
        dest.writeString(this.mLecturer);
        dest.writeString(this.mRoom);
        dest.writeString(this.mUid);
    }

    private TimeTableEventVo(Parcel in) {
        this.mStartDate = in.readLong();
        this.mTitle = in.readString();
        this.mShortTitle = in.readString();
        this.mDate = in.readString();
        this.mDayOfWeek = in.readString();
        this.mWeekOfYear = in.readInt();
        this.mStartTime = in.readString();
        this.mEndTime = in.readString();
        this.mLecturer = in.readString();
        this.mRoom = in.readString();
        this.mUid = in.readString();
    }

    public static final Parcelable.Creator<TimeTableEventVo> CREATOR = new Parcelable.Creator<TimeTableEventVo>() {
        public TimeTableEventVo createFromParcel(Parcel source) {
            return new TimeTableEventVo(source);
        }

        public TimeTableEventVo[] newArray(int size) {
            return new TimeTableEventVo[size];
        }
    };
}
