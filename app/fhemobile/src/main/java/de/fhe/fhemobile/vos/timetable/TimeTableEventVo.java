/*
 *  Copyright (c) 2014-2019 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package de.fhe.fhemobile.vos.timetable;

import static de.fhe.fhemobile.utils.mytimetable.MyTimeTableUtils.getCourseName;
import static de.fhe.fhemobile.utils.Utils.correctUmlauts;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 16.03.15.
 */
public class TimeTableEventVo implements Parcelable {

    public TimeTableEventVo() {
    }

    public long getFullDateWithStartTime() {
        return mFullDate;
    }

// --Commented out by Inspection START (02.11.2021 17:34):
//    public void setStartDate(long _startDate) {
//        mFullDate = _startDate;
//    }
// --Commented out by Inspection STOP (02.11.2021 17:34)

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(final String _title) {
        mTitle = _title;
    }

    @NonNull
    public String getShortTitle() {
        //Todo: vorübergehender Fix für Veranstaltungen mit shortTitle = null
        return correctUmlauts(mShortTitle != null ? mShortTitle : mTitle);
    }

    public String getGuiTitle(){
        return getCourseName(getShortTitle());
    }

// --Commented out by Inspection START (02.11.2021 17:34):
//    public void setShortTitle(String _shortTitle) {
//        mShortTitle = _shortTitle;
//    }
// --Commented out by Inspection STOP (02.11.2021 17:34)

    public String getDate() {
        return mDay;
    }

    public void setDate(final String _date) {
        mDay = _date;
    }

    public String getDayOfWeek() {
        return mDayOfWeek;
    }

// --Commented out by Inspection START (02.11.2021 17:34):
//    public void setDayOfWeek(String _dayOfWeek) {
//        mDayOfWeek = _dayOfWeek;
//    }
// --Commented out by Inspection STOP (02.11.2021 17:34)

// --Commented out by Inspection START (02.11.2021 17:35):
//    public int getWeekOfYear() {
//        return mWeekOfYear;
//    }
// --Commented out by Inspection STOP (02.11.2021 17:35)

// --Commented out by Inspection START (02.11.2021 17:34):
//    public void setWeekOfYear(int _weekOfYear) {
//        mWeekOfYear = _weekOfYear;
//    }
// --Commented out by Inspection STOP (02.11.2021 17:34)

    public String getStartTime() {
        return mStartTime;
    }

// --Commented out by Inspection START (02.11.2021 17:34):
//    public void setStartTime(String _startTime) {
//        mStartTime = _startTime;
//    }
// --Commented out by Inspection STOP (02.11.2021 17:34)

    public String getEndTime() {
        return mEndTime;
    }

// --Commented out by Inspection START (02.11.2021 17:34):
//    public void setEndTime(String _endTime) {
//        mEndTime = _endTime;
//    }
// --Commented out by Inspection STOP (02.11.2021 17:34)

    public String getLecturer() {
        return mLecturer;
    }

    public void setLecturer(final String _lecturer) {
        mLecturer = _lecturer;
    }

    public String getRoom() {
        return mRoom;
    }

    public void setRoom(final String _room) {
        mRoom = _room;
    }

    public String getUid() {
        return mUid;
    }

// --Commented out by Inspection START (02.11.2021 17:34):
//    public void setUid(String _uid) {
//        mUid = _uid;
//    }
// --Commented out by Inspection STOP (02.11.2021 17:34)

    @SerializedName("startDate")
    private long mFullDate;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("shortTitle")
    private String mShortTitle;

    @SerializedName("date")
    private String mDay;

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
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeLong(this.mFullDate);
        dest.writeString(this.mTitle);
        dest.writeString(this.mShortTitle);
        dest.writeString(this.mDay);
        dest.writeString(this.mDayOfWeek);
        dest.writeInt(this.mWeekOfYear);
        dest.writeString(this.mStartTime);
        dest.writeString(this.mEndTime);
        dest.writeString(this.mLecturer);
        dest.writeString(this.mRoom);
        dest.writeString(this.mUid);
    }

    private TimeTableEventVo(final Parcel in) {
        this.mFullDate = in.readLong();
        this.mTitle = correctUmlauts(in.readString());
        this.mShortTitle = correctUmlauts(in.readString());
        this.mDay = in.readString();
        this.mDayOfWeek = in.readString();
        this.mWeekOfYear = in.readInt();
        this.mStartTime = in.readString();
        this.mEndTime = in.readString();
        this.mLecturer = correctUmlauts(in.readString());
        this.mRoom = correctUmlauts(in.readString());
        this.mUid = in.readString();
    }

    public static final Parcelable.Creator<TimeTableEventVo> CREATOR = new Parcelable.Creator<TimeTableEventVo>() {
        public TimeTableEventVo createFromParcel(final Parcel source) {
            return new TimeTableEventVo(source);
        }

        public TimeTableEventVo[] newArray(final int size) {
            return new TimeTableEventVo[size];
        }
    };
}
