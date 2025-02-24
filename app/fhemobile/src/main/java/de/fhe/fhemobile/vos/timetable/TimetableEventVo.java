/*
 *  Copyright (c) 2014-2022 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
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

import static de.fhe.fhemobile.utils.timetable.TimetableUtils.cutStudyProgramPrefix;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by paul on 16.03.15
 * Edited by Nadja - 04/2022
 */
public final class TimetableEventVo implements Parcelable {

    public TimetableEventVo() {
    }

    public String getGuiTitle() {
        return cutStudyProgramPrefix(mTitle);
    }

    public String getId() {
        return mId;
    }

    /**
     * Get time of day the event is starting
     *
     * @return String containing time formatted as HH:mm
     */
    public String getStartTimeString(){
        // convert to milliseconds
        final long lStartOrEndDateTime = mStartDateTime * 1000 ;
        final Date dateGetStartTime = new Date(lStartOrEndDateTime);

        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ROOT);
        // this is the magic thing to advise the SimpleDateFormat to do nothing with the Timezones
        sdf.setTimeZone( TimeZone.getTimeZone("UTC") );

        final String strGetStartTime = sdf.format( dateGetStartTime );
        return strGetStartTime;
    }

    /**
     * Get time of day the event is ending
     *
     * @return String containing time formatted as HH:mm
     */
    public String getEndTimeString() {
        // convert to milliseconds
        final long lStartOrEndDateTime = mEndDateTime * 1000 ;
        final Date dateGetStartTime = new Date(lStartOrEndDateTime);

        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ROOT);
        // this is the magic thing to advise the SimpleDateFormat to do nothing with the Timezones
        sdf.setTimeZone( TimeZone.getTimeZone("UTC") );

        final String strGetStartTime = sdf.format( dateGetStartTime );
        return strGetStartTime;
    }

    public String getLocationListAsString(){
        final StringBuilder stringBuilder = new StringBuilder();

        if(mLocationList.isEmpty()) return "";

        for(final TimetableLocationVo room : mLocationList.values()){
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

    public String getLecturerListAsString(){
        final StringBuilder stringBuilder = new StringBuilder();

        if(mLecturerList.isEmpty()) return "";

        for(final LecturerVo lecturer : mLecturerList.values()){
            if(lecturer.getName() != null){
                // append ", " in any case, for lists of lecturers
                stringBuilder.append(lecturer.getName()).append(", ");
            }
        }

        if(stringBuilder.toString().isEmpty() || stringBuilder.length() <= 2){
            // no lecturer, interesting....
            return "";
        } else {
            final int length = stringBuilder.length();
            // delete the ", " at the end, if only one lecturer
            stringBuilder.delete(length - 2, length);
            // add a little space at the end, looks better because of italic font
            stringBuilder.append("  ");
            return stringBuilder.toString();
        }
    }

    // PARCELABLE --------------------------------------------------------------------------------

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        // VERSION 1
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeLong(mStartDateTime);
        dest.writeLong(mEndDateTime);
        dest.writeMap(mLecturerList);
        dest.writeMap(mLocationList);
    }

    TimetableEventVo(final Parcel in) {
        // VERSION 1
        mId = in.readString();
        this.mTitle = in.readString();
        this.mStartDateTime = in.readLong();
        this.mEndDateTime = in.readLong();
        in.readMap(mLecturerList , LecturerVo.class.getClassLoader());
        in.readMap(mLocationList, TimetableLocationVo.class.getClassLoader());
    }


    public static final Parcelable.Creator<TimetableEventVo> CREATOR = new Parcelable.Creator<TimetableEventVo>() {
        public TimetableEventVo createFromParcel(final Parcel source) {
            return new TimetableEventVo(source);
        }

        public TimetableEventVo[] newArray(final int size) {
            return new TimetableEventVo[size];
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
    private final Map<String, LecturerVo> mLecturerList = new HashMap<>();

    @SerializedName("dataLocation")
    private final Map<String, TimetableLocationVo> mLocationList = new HashMap<>();
}
