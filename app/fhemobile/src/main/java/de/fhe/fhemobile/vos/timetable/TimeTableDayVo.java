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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import de.fhe.fhemobile.utils.timetable.TimeTableUtils;

/**
 * Created by paul on 16.03.15
 * Edited by Nadja - 04/2022
 */
public final class TimeTableDayVo implements Parcelable {

    private TimeTableDayVo(final Parcel in) {
        mDayNumber = in.readInt();
        in.readTypedList(mEvents, TimeTableEventVo.CREATOR);
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(mDayNumber);
        dest.writeTypedList(mEvents);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<TimeTableDayVo> CREATOR = new Parcelable.Creator<TimeTableDayVo>() {
        public TimeTableDayVo createFromParcel(final Parcel source) {
            return new TimeTableDayVo(source);
        }

        public TimeTableDayVo[] newArray(final int size) {
            return new TimeTableDayVo[size];
        }
    };



    public String getDayName() {
        return TimeTableUtils.getWeekDayName(mDayNumber);
    }

    public ArrayList<TimeTableEventVo> getEvents() {
        return mEvents;
    }



    @SerializedName("dayNumber")
    private final int mDayNumber;

    @SerializedName("dataActivity")
    private final ArrayList<TimeTableEventVo> mEvents = new ArrayList<>();
}
