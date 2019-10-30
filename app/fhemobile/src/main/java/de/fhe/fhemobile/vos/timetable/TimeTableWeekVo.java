/*
 * Copyright (c) 2014-2019 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fhe.fhemobile.vos.timetable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by paul on 16.03.15.
 */
public class TimeTableWeekVo implements Parcelable {

    public TimeTableWeekVo() {
    }

    public int getWeekInYear() {
        return mWeekInYear;
    }

    public void setWeekInYear(int _weekInYear) {
        mWeekInYear = _weekInYear;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(int _year) {
        mYear = _year;
    }

    public ArrayList<TimeTableDayVo> getDays() {
        return mDays;
    }

    public void setDays(ArrayList<TimeTableDayVo> _days) {
        mDays = _days;
    }

    @SerializedName("weekInYear")
    private int mWeekInYear;

    @SerializedName("year")
    private int mYear;

    @SerializedName("weekdays")
    private ArrayList<TimeTableDayVo> mDays = new ArrayList<>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mWeekInYear);
        dest.writeInt(mYear);
        dest.writeTypedList(mDays);
    }

    private TimeTableWeekVo(Parcel in) {
        mWeekInYear = in.readInt();
        mYear = in.readInt();
        in.readTypedList(mDays, TimeTableDayVo.CREATOR);
    }

    public static final Parcelable.Creator<TimeTableWeekVo> CREATOR = new Parcelable.Creator<TimeTableWeekVo>() {
        public TimeTableWeekVo createFromParcel(Parcel source) {
            return new TimeTableWeekVo(source);
        }

        public TimeTableWeekVo[] newArray(int size) {
            return new TimeTableWeekVo[size];
        }
    };
}
