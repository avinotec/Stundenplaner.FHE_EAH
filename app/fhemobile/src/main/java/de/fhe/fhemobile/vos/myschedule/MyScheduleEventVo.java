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


import static de.fhe.fhemobile.utils.timetable.TimetableUtils.cutStudyProgramPrefix;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.fhe.fhemobile.comparator.LecturerComparator;
import de.fhe.fhemobile.comparator.TimetableLocationComparator;
import de.fhe.fhemobile.utils.myschedule.MyScheduleUtils;
import de.fhe.fhemobile.utils.myschedule.TimetableChangeType;
import de.fhe.fhemobile.utils.timetable.TimetableUtils;
import de.fhe.fhemobile.vos.timetable.LecturerVo;
import de.fhe.fhemobile.vos.timetable.TimetableLocationVo;

/**
 * Value object for a time table event in MySchedule
 *
 * Created by Nadja - 05/2022
 */
public class MyScheduleEventVo implements Parcelable {

    public MyScheduleEventVo(final String title,
                             final String eventSetId,
                             final long germanStartTime,
                             final long germanEndTime,
                             final List<LecturerVo> lecturerList,
                             final List<TimetableLocationVo> locationList){
        mTitle = title;
        mEventSetId = eventSetId;
        mGermanStartDateTime = germanStartTime;
        mGermanEndDateTime = germanEndTime;
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
        dest.writeLong(mGermanStartDateTime);
        dest.writeLong(mGermanEndDateTime);
        dest.writeList(mLecturerList);
        dest.writeList(mLocationList);
        dest.writeArray(typesOfChanges.toArray());
        dest.writeByte((byte) (mChangedSinceLastCalSync ? 1 : 0));
        dest.writeLong(mCalEventId);
    }

    MyScheduleEventVo(final Parcel in) {
        this.mTitle = in.readString();
        this.mEventSetId = in.readString();
        this.mGermanStartDateTime = in.readLong();
        this.mGermanEndDateTime = in.readLong();
        in.readList(mLecturerList , LecturerVo.class.getClassLoader());
        in.readList(mLocationList, TimetableLocationVo.class.getClassLoader());
        typesOfChanges = new HashSet(Arrays.asList(in.readArray(TimetableChangeType.class.getClassLoader())));
        mChangedSinceLastCalSync = in.readByte() == 1;
        this.mCalEventId = in.readLong();

        //remove null objects
        for(final TimetableLocationVo location : mLocationList){
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

    public Long getStartTime(){
        if(mStartTime == null){
            mStartTime = MyScheduleUtils.convertEahApiTimeToUtc(mGermanStartDateTime);
        }
        return mStartTime;
    }

    public Long getEndTime(){
        if(mEndTime == null) {
            mEndTime = MyScheduleUtils.convertEahApiTimeToUtc(mGermanEndDateTime);
        }
        return mEndTime;
    }

    public Date getStartDate(){
        return new Date(getStartTime());
    }

    public Date getEndDate(){
        return new Date(getEndTime());
    }

    /**
     * Get time of day the event is starting
     *
     * @return String containing time formatted as HH:mm
     */
    public String getStartTimeString(){
        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ROOT);
        return sdf.format(getStartDate());
    }

    /**
     * Get time of day the event is ending
     *
     * @return String containing time formatted as HH:mm
     */
    public String getEndTimeString() {
        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ROOT);
        return sdf.format(getEndDate());
    }

    public String getEventSetId() { return mEventSetId; }

    /**
     * Return list of locations, sorted by location id
     * @return
     */
    public List<TimetableLocationVo> getLocationList() {
        //sorting needed to enable proper comparison via "equals"
        Collections.sort(mLocationList, new TimetableLocationComparator());
        return mLocationList;
    }

    public String getLocationListAsString(){
        final StringBuilder stringBuilder = new StringBuilder();

        if(mLocationList.isEmpty()) return "";

        for(final TimetableLocationVo room : mLocationList){
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
        return TimetableUtils.getWeekDayName(getStartDate());
    }

    /**
     * Get abbreviation of week day, e.g. "Mo"
     * @return Short form tof the event's week day as String
     */
    public String getWeekDayShort(){
        return new SimpleDateFormat("E").format(getStartDate());
    }

    /**
     * Return lecturer list, sorted by id
     * @return List of {@link LecturerVo}s
     */
    public List<LecturerVo> getLecturerList() {
        //sorting needed to enable proper comparison via "equals"
        Collections.sort(mLecturerList, new LecturerComparator());
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

    public boolean changedSinceLastCalSync() {
        return mChangedSinceLastCalSync;
    }

    public Long getCalEventId() {
        return mCalEventId;
    }

    public void addChange(final TimetableChangeType type){
        typesOfChanges.add(type);
        mChangedSinceLastCalSync = true;
    }

    public void setStartDateTimeInSec(final long mStartDateTime) {
        this.mGermanStartDateTime = mStartDateTime;
    }

    public void setEndDateTimeInSec(final long mEndDateTime) {
        this.mGermanEndDateTime = mEndDateTime;
    }

    public void setLecturerList(final List<LecturerVo> lecturerList) {
        this.mLecturerList = lecturerList;
    }

    public void setLocationList(final List<TimetableLocationVo> locationList) {
        this.mLocationList = locationList;
    }

    public void setTitle(final String title) {
        this.mTitle = title;
    }

    public void setCalEventId(final Long newValue) {
        this.mCalEventId = newValue;
    }

    public void setChangedSinceLastCalSync(final boolean newValue) {
        this.mChangedSinceLastCalSync = newValue;
    }

    @SerializedName("title")
    private String mTitle;

    @SerializedName("activityId")
    private final String mEventSetId;

    @SerializedName("startDateTime")
    private long mGermanStartDateTime;

    @SerializedName("endDateTime")
    private long mGermanEndDateTime;

    @SerializedName("lecturer")
    private List<LecturerVo> mLecturerList = new ArrayList<>();

    @SerializedName("location")
    private List<TimetableLocationVo> mLocationList = new ArrayList<>();

    @SerializedName("typesOfChanges")
    private Set<TimetableChangeType> typesOfChanges = new HashSet<>();

    @SerializedName("mChangedSinceLastCalSync")
    private boolean mChangedSinceLastCalSync = true;

    @SerializedName("calendarEventId")
    private Long mCalEventId = null;


    private Long mStartTime;
    private Long mEndTime;
}
