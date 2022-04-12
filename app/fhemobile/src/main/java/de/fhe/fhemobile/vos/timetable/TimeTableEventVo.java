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

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.fhe.fhemobile.utils.timetable.TimeTableUtils;

/**
 * Created by paul on 16.03.15
 * Edited by Nadja - 04/2022
 */
public class TimeTableEventVo implements Parcelable {

    final static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    public TimeTableEventVo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeLong(mStartDate);
        dest.writeLong(mEndDate);
        dest.writeLong(mStartDateTime);
        dest.writeLong(mEndDateTime);
        dest.writeString(mCourseName);
        dest.writeString(mCourseDescription);
        dest.writeTypedList(mLecturerList);
        dest.writeTypedList(mLocationList);
    }

    private TimeTableEventVo(final Parcel in) {
        mId = in.readString();
        this.mTitle = in.readString();
        mStartDate = in.readLong();
        mEndDate = in.readLong();
        this.mStartDateTime = in.readLong();
        this.mEndDateTime = in.readLong();
        this.mCourseName = in.readString();
        mCourseDescription = in.readString();
        mLecturerList = in.readArrayList(LecturerVo.class.getClassLoader());
        mLocationList = in.readArrayList(TimeTableLocationVo.class.getClassLoader());
    }


    public static final Parcelable.Creator<TimeTableEventVo> CREATOR = new Parcelable.Creator<TimeTableEventVo>() {
        public TimeTableEventVo createFromParcel(final Parcel source) {
            return new TimeTableEventVo(source);
        }

        public TimeTableEventVo[] newArray(final int size) {
            return new TimeTableEventVo[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public String getId() {
        return mId;
    }

    public long getStartDateTime() {
        return mStartDateTime;
    }

    /**
     * Returns only date (Date object with time 00:00:00)
     * @return
     */
    public Date getStartDate() {
        return new Date(mStartDate);
    }

    public String getStartTime(){
        return sdf.format(new Date(mStartDateTime).getTime());
    }

    public String getEndTime() {
        return sdf.format(new Date((mEndDateTime)).getTime());
    }

    public String getRoom(){
        StringBuilder stringBuilder = null;
        for(TimeTableLocationVo room : mLocationList){

            if(stringBuilder == null){
                stringBuilder = new StringBuilder();
            }else{
                stringBuilder.append(", ");
            }
            stringBuilder.append(room.getName());
        }
        return stringBuilder != null ? stringBuilder.toString() : "";
    }

    public String getWeekDayName(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(mStartDate));
        return TimeTableUtils.getWeekDayName(cal.get(Calendar.DAY_OF_WEEK));
    }

    public String getLecturer(){
        StringBuilder stringBuilder = null;
        for(LecturerVo lecturer: mLecturerList){

            if(stringBuilder == null){
                stringBuilder = new StringBuilder();
            }else{
                stringBuilder.append(", ");
            }
            stringBuilder.append(lecturer.getName());
        }
        return stringBuilder != null ? stringBuilder.toString() : "";
    }

    @SerializedName("activityId")
    private String mId;

    @SerializedName("activityName")
    private String mTitle;

    @SerializedName("activityStartDate")
    private long mStartDate;

    @SerializedName("activityEndDate")
    private long mEndDate;

    @SerializedName("activityStartDateTime")
    private long mStartDateTime;

    @SerializedName("activityEndDateTime")
    private long mEndDateTime;

    @SerializedName("moduleName")
    private String mCourseName;

    @SerializedName("moduleDescription")
    private String mCourseDescription;

    @SerializedName("dataStaff")
    private ArrayList<LecturerVo> mLecturerList = new ArrayList<>();

    @SerializedName("dataLocation")
    private ArrayList<TimeTableLocationVo> mLocationList = new ArrayList<>();
}
