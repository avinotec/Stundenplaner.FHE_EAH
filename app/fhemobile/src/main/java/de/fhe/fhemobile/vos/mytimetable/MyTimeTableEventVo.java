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
package de.fhe.fhemobile.vos.mytimetable;

import static de.fhe.fhemobile.utils.timetable.TimeTableUtils.cutStudyProgramPrefix;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.fhe.fhemobile.utils.timetable.TimeTableUtils;
import de.fhe.fhemobile.vos.timetable.LecturerVo;
import de.fhe.fhemobile.vos.timetable.TimeTableLocationVo;

/**
 * Value object for a time table event in MyTimeTable
 *
 * Created by Nadja - 05/2022
 */
public class MyTimeTableEventVo implements Parcelable{

    static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", new Locale("de", "DE"));

    public MyTimeTableEventVo() {
    }

    public MyTimeTableEventVo(String title,
                            long startDateTime,
                            long endDateTime,
                            List<LecturerVo> lecturerList,
                            List<TimeTableLocationVo> locationList){
        mTitle = title;
        mStartDateTime = startDateTime;
        mEndDateTime = endDateTime;
        mLecturerList = lecturerList;
        mLocationList = locationList;

    }

    // PARCELABLE ---------------------------------------------------------------------------------
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(mTitle);
        dest.writeLong(mStartDateTime);
        dest.writeLong(mEndDateTime);
        dest.writeList(mLecturerList);
        dest.writeList(mLocationList);
    }

    private MyTimeTableEventVo(final Parcel in) {
        this.mTitle = in.readString();
        this.mStartDateTime = in.readLong();
        this.mEndDateTime = in.readLong();
        in.readList(mLecturerList , LecturerVo.class.getClassLoader());
        in.readList(mLocationList, TimeTableLocationVo.class.getClassLoader());

        //remove null objects
        for(TimeTableLocationVo location : mLocationList){
            if(location.getName() == null){
                mLocationList.remove(location);
            }
        }
    }


    public static final Parcelable.Creator<MyTimeTableEventVo> CREATOR = new Parcelable.Creator<MyTimeTableEventVo>() {
        public MyTimeTableEventVo createFromParcel(final Parcel source) {
            return new MyTimeTableEventVo(source);
        }

        public MyTimeTableEventVo[] newArray(final int size) {
            return new MyTimeTableEventVo[size];
        }
    };

    // End PARCELABLE ---------------------------------------------------------------------------------

    public String getGuiTitle() {
        return cutStudyProgramPrefix(mTitle);
    }

    public long getStartDateTime() {
        //multiply by 1000 to convert from seconds to milliseconds
        return mStartDateTime * 1000;
    }

    public long getEndDateTime() {
        //multiply by 1000 to convert from seconds to milliseconds
        return mEndDateTime * 1000;
    }

    public String getStartTimeString(){
        //multiply by 1000 to convert from seconds to milliseconds
        return sdf.format(new Date(mStartDateTime * 1000));
    }

    public String getEndTimeString() {
        //multiply by 1000 to convert from seconds to milliseconds
        return sdf.format(new Date((mEndDateTime * 1000)));
    }

    public List<TimeTableLocationVo> getLocationList() {
        return mLocationList;
    }

    public String getLocationListAsString(){
        StringBuilder stringBuilder = new StringBuilder();

        if(mLocationList.isEmpty()) return "";

        for(TimeTableLocationVo room : mLocationList){
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

    public String getWeekDayName(){
        return TimeTableUtils.getWeekDayName(new Date(mStartDateTime * 1000));
    }

    public String getLecturerListAsString(){
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

    @SerializedName("title")
    private String mTitle;

    @SerializedName("startDateTime")
    private long mStartDateTime;

    @SerializedName("endDateTime")
    private long mEndDateTime;

    @SerializedName("lecturer")
    private List<LecturerVo> mLecturerList = new ArrayList<>();

    @SerializedName("location")
    private List<TimeTableLocationVo> mLocationList = new ArrayList<>();

}
