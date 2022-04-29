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

import static de.fhe.fhemobile.utils.timetable.TimeTableUtils.cutStudyProgramPrefix;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        dest.writeMap(mLecturerList);
        dest.writeMap(mLocationList);
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
        in.readMap(mLecturerList , LecturerVo.class.getClassLoader());
        in.readMap(mLocationList, TimeTableLocationVo.class.getClassLoader());
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
        return cutStudyProgramPrefix(mTitle);
    }

    public String getId() {
        return mId;
    }

    public long getStartDateTime() {
        return mStartDateTime * 1000;
    }

    public long getEndDateTime() {
        return mEndDateTime * 1000;
    }

    /**
     * Returns only date (Date object with time 00:00:00)
     * @return
     */
    public Date getStartDate() {
        //multiply by 1000 to convert from seconds to milliseconds
        return new Date(mStartDate * 1000);
    }

    public String getStartTime(){
        //multiply by 1000 to convert from seconds to milliseconds
        return sdf.format(new Date(mStartDateTime * 1000));
    }

    public String getEndTime() {
        return sdf.format(new Date((mEndDateTime * 1000)));
    }

    public String getRoom(){
        StringBuilder stringBuilder = null;
        for(TimeTableLocationVo room : mLocationList.values()){

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
        cal.setTime(new Date(mStartDate * 1000));
        return TimeTableUtils.getWeekDayName(cal.get(Calendar.DAY_OF_WEEK));
    }

    public String getLecturer(){
        StringBuilder stringBuilder = null;
        for(LecturerVo lecturer: mLecturerList.values()){

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

    @SerializedName("activitydatetimeStartDateTime")
    private long mStartDateTime;

    @SerializedName("activitydatetimeEndDateTime")
    private long mEndDateTime;

    @SerializedName("moduleName")
    private String mCourseName;

    @SerializedName("moduleDescription")
    private String mCourseDescription;

    @SerializedName("dataStaff")
    private Map<String, LecturerVo> mLecturerList = new HashMap<>();

    @SerializedName("dataLocation")
    private Map<String, TimeTableLocationVo> mLocationList = new HashMap<>();
}
