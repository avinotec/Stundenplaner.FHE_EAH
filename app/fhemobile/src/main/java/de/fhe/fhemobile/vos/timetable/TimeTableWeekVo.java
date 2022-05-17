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

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by paul on 16.03.15
 * Edited by Nadja - 04/2022
 */
public class TimeTableWeekVo implements Parcelable {

    public TimeTableWeekVo() {
    }

    private TimeTableWeekVo(final Parcel in) {
        mWeekStart = in.readLong();
        mWeekEnd = in.readLong();
        mSemesterWeek = in.readInt();
        in.readMap(mDays, TimeTableDayVo.class.getClassLoader());
    }

    public Date getWeekStart() {
        //multiply by 1000 to convert from seconds to milliseconds
        Date date = new Date(mWeekStart * 1000);
        return date;
    }

    public Date getWeekEnd() {
        //multiply by 1000 to convert from seconds to milliseconds
        Date date = new Date(mWeekEnd * 1000);
        return date;
    }

    /**
     * The semester week is stored as 0-based, but officially it is counted 1-based.
     * That's why one need to be added when the semester week number is displayed
     * @return
     */
    public int getGuiSemesterWeek(){ return mSemesterWeek+1;}

    public Collection<TimeTableDayVo> getDays() {
        return mDays.values();
    }


    // PARCELABLE --------------------------------------------------------------------------------
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeLong(mWeekStart);
        dest.writeLong(mWeekEnd);
        dest.writeInt(mSemesterWeek);
        dest.writeMap(mDays);
    }

    public static final Parcelable.Creator<TimeTableWeekVo> CREATOR = new Parcelable.Creator<TimeTableWeekVo>() {
        public TimeTableWeekVo createFromParcel(final Parcel source) {
            return new TimeTableWeekVo(source);
        }

        public TimeTableWeekVo[] newArray(final int size) {
            return new TimeTableWeekVo[size];
        }
    };

    // End PARCELABLE ---------------------------------------------------------------------------------

    @SerializedName("weekStart")
    private long mWeekStart;

    @SerializedName("weekEnd")
    private long mWeekEnd;

    @SerializedName("weekNumber")
    private int mSemesterWeek;

    @SerializedName("dataDay")
    private final Map<String, TimeTableDayVo> mDays = new HashMap<>();
}
