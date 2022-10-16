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
package de.fhe.fhemobile.vos.myschedule;


import static de.fhe.fhemobile.utils.timetable.TimeTableUtils.cutStudyProgramPrefix;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.fhe.fhemobile.utils.Utils;
import de.fhe.fhemobile.utils.myschedule.TimetableChangeType;
import de.fhe.fhemobile.utils.timetable.TimeTableUtils;
import de.fhe.fhemobile.vos.timetable.LecturerVo;
import de.fhe.fhemobile.vos.timetable.TimeTableLocationVo;

/**
 * Value object for a time table event in MySchedule
 *
 * Created by Nadja - 05/2022
 */
public class MyScheduleEventVo implements Parcelable {

    public MyScheduleEventVo(final String title,
                             final String eventSetId,
                             final long startDateTime,
                             final long endDateTime,
                             final List<LecturerVo> lecturerList,
                             final List<TimeTableLocationVo> locationList){
        mTitle = title;
        mEventSetId = eventSetId;
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
        dest.writeString(mEventSetId);
        dest.writeLong(mStartDateTime);
        dest.writeLong(mEndDateTime);
        dest.writeList(mLecturerList);
        dest.writeList(mLocationList);
        dest.writeArray(typesOfChanges.toArray());
    }

    MyScheduleEventVo(final Parcel in) {
        this.mTitle = in.readString();
        this.mEventSetId = in.readString();
        this.mStartDateTime = in.readLong();
        this.mEndDateTime = in.readLong();
        in.readList(mLecturerList , LecturerVo.class.getClassLoader());
        in.readList(mLocationList, TimeTableLocationVo.class.getClassLoader());
        typesOfChanges = new HashSet(Arrays.asList(in.readArray(TimetableChangeType.class.getClassLoader())));

        //remove null objects
        for(final TimeTableLocationVo location : mLocationList){
            if(location.getName() == null){
                mLocationList.remove(location);
            }
        }
    }


    public static final Parcelable.Creator<MyScheduleEventVo> CREATOR = new Parcelable.Creator<MyScheduleEventVo>() {
        public MyScheduleEventVo createFromParcel(final Parcel source) {
            return new MyScheduleEventVo(source);
        }

        public MyScheduleEventVo[] newArray(final int size) {
            return new MyScheduleEventVo[size];
        }
    };

    // End PARCELABLE ---------------------------------------------------------------------------------

    public String getTitle(){ return mTitle; }

    public String getGuiTitle() {
        return cutStudyProgramPrefix(mTitle);
    }

    /**
     * Get start date time in seconds, time zone offset included.
     * (attention: Do not use this long to create a {@link Date} instance, use getStartDate instead)
     * @return The start date time as long
     */
    public long getStartDateTimeInSec() {
        return mStartDateTime;
    }

    /**
     * Get end date time in seconds, time zone offset included.
     * (attention: Do not use this long to create a {@link Date} instance, use getStartDate instead)
     * @return The end date time as long
     */
    public long getEndDateTimeInSec() {
        return mEndDateTime;
    }

    public Date getStartDateTime(){
        //multiply by 1000 to convert from seconds to milliseconds,
        /*
        // VERSION SS22
        // subtract time zone offset because mStartDateTime is in time zone "Berlin"
        // but Date needs long in UTC
        return new Date(mStartDateTime * 1000 - offset);
        */
        // VERSION WS22/23
        // time is delivered in UTC
        // --> TODO: investigate if correct version depends on semester (because of database change)
        Date startDateTime = Utils.convertTimeFromStundenplanWebserverDate( mStartDateTime );
        return startDateTime;

    }

    public Date getEndDateTime(){
        //multiply by 1000 to convert from seconds to milliseconds,
        /*
        // VERSION SS22
        // subtract time zone offset because mEndDateTime is in time zone "Berlin"
        // but Date needs long in UTC
        return new Date(mStartDateTime * 1000 - offset);
        */
        // VERSION WS22/23
        // time is delivered in UTC
        // --> TODO: investigate if correct version depends on semester (because of database change)
        Date endDateTime = Utils.convertTimeFromStundenplanWebserverDate( mEndDateTime );
        return endDateTime;
    }

    /** in seconds */
    public String getStartTimeString(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ROOT);
        return sdf.format(getStartDateTime());
    }

    /** in seconds */
    public String getEndTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ROOT);
        return sdf.format(getEndDateTime());
    }

    public String getEventSetId() { return mEventSetId; }

    public List<TimeTableLocationVo> getLocationList() {
        return mLocationList;
    }

    public String getLocationListAsString(){
        final StringBuilder stringBuilder = new StringBuilder();

        if(mLocationList.isEmpty()) return "";

        for(final TimeTableLocationVo room : mLocationList){
            if(room.getName() != null) {
                final String roomName = room.getName().replaceAll("\\(", " (");
                stringBuilder.append(roomName).append(", ");
            }
        }

        if(stringBuilder.toString().isEmpty() || stringBuilder.length() <= 2){
            return "";
        } else {
            return stringBuilder.substring(0, stringBuilder.length() - 2);
        }
    }

    public String getWeekDayName(){
        return TimeTableUtils.getWeekDayName(getStartDateTime());
    }

    /**
     * Get abbreviation of week day, e.g. "Mo"
     * @return
     */
    public String getWeekDayShort(){
        return new SimpleDateFormat("E" ).format(getStartDateTime());
    }

    public List<LecturerVo> getLecturerList() {
        return mLecturerList;
    }

    public String getLecturerListAsString(){
        StringBuilder stringBuilder = null;
        for(final LecturerVo lecturer: mLecturerList){

            if(stringBuilder == null){
                stringBuilder = new StringBuilder();
            }else{
                stringBuilder.append(", ");
            }
            stringBuilder.append(lecturer.getName());
        }
        return stringBuilder != null ? stringBuilder.toString() : "";
    }

    public Set<TimetableChangeType> getTypesOfChanges() {
        return typesOfChanges;
    }

    public void addChange(final TimetableChangeType type){
        typesOfChanges.add(type);
    }

    public void setStartDateTimeInSec(final long mStartDateTime) {
        this.mStartDateTime = mStartDateTime;
    }

    public void setEndDateTimeInSec(final long mEndDateTime) {
        this.mEndDateTime = mEndDateTime;
    }

    public void setLecturerList(final List<LecturerVo> lecturerList) {
        this.mLecturerList = lecturerList;
    }

    public void setLocationList(final List<TimeTableLocationVo> locationList) {
        this.mLocationList = locationList;
    }

    public void setTitle(final String title) {
        this.mTitle = title;
    }

    @SerializedName("title")
    private String mTitle;

    @SerializedName("activityId")
    private String mEventSetId;

    @SerializedName("startDateTime")
    private long mStartDateTime;

    @SerializedName("endDateTime")
    private long mEndDateTime;

    @SerializedName("lecturer")
    private List<LecturerVo> mLecturerList = new ArrayList<>();

    @SerializedName("location")
    private List<TimeTableLocationVo> mLocationList = new ArrayList<>();

    @SerializedName("typesOfChanges")
    private Set<TimetableChangeType> typesOfChanges = new HashSet<>();

}
