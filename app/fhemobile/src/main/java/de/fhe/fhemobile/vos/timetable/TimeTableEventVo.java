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

    static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    public TimeTableEventVo() {
    }

    public String getGuiTitle() {
        return cutStudyProgramPrefix(mTitle);
    }

    public String getId() {
        return mId;
    }

    public long getStartDateTime() {
        //multiply by 1000 to convert from seconds to milliseconds
        return mStartDateTime * 1000;
    }

    public long getEndDateTime() {
        //multiply by 1000 to convert from seconds to milliseconds
        return mEndDateTime * 1000;
    }


    public String getStartTime(){
        //multiply by 1000 to convert from seconds to milliseconds
        return sdf.format(new Date(mStartDateTime * 1000));
    }

    public String getEndTime() {
        return sdf.format(new Date((mEndDateTime * 1000)));
    }

    public String getLocationListAsString(){
        StringBuilder stringBuilder = new StringBuilder();

        if(mLocationList.isEmpty()) return "";

        for(TimeTableLocationVo room : mLocationList.values()){
            if(room.getName() != null) {
                String roomName = room.getName().replaceAll("\\(", " (");
                stringBuilder.append(roomName + ", ");
            }
        }

        if(stringBuilder.toString().isEmpty() || stringBuilder.length() <= 2){
            return "";
        } else {
            return stringBuilder.substring(0, stringBuilder.length() - 2);
        }
    }

    public String getLecturerListAsString(){
        StringBuilder stringBuilder = new StringBuilder();

        if(mLecturerList.isEmpty()) return "";

        for(LecturerVo lecturer : mLecturerList.values()){
            if(lecturer.getName() != null){
                stringBuilder.append(lecturer.getName() + ", ");
            }
        }

        if(stringBuilder.toString().isEmpty() || stringBuilder.length() <= 2){
            return "";
        } else {
            return stringBuilder.substring(0, stringBuilder.length() - 2);
        }
    }

    // PARCELABLE --------------------------------------------------------------------------------

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeLong(mStartDateTime);
        dest.writeLong(mEndDateTime);
        dest.writeMap(mLecturerList);
        dest.writeMap(mLocationList);
    }

    private TimeTableEventVo(final Parcel in) {
        mId = in.readString();
        this.mTitle = in.readString();
        this.mStartDateTime = in.readLong();
        this.mEndDateTime = in.readLong();
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

    // End PARCELABLE --------------------------------------------------------------------------------


    @SerializedName("activityId")
    private String mId;

    @SerializedName("activityName")
    private String mTitle;

    @SerializedName("startDateTime")
    private long mStartDateTime;

    @SerializedName("endDateTime")
    private long mEndDateTime;

    @SerializedName("dataStaff")
    private Map<String, LecturerVo> mLecturerList = new HashMap<>();

    @SerializedName("dataLocation")
    private Map<String, TimeTableLocationVo> mLocationList = new HashMap<>();
}
