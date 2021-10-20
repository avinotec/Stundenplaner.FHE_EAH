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

/**
 * Created by paul on 16.03.15.
 */
public class TimeTableDayVo implements Parcelable {

    public TimeTableDayVo() {
    }

    public Integer getDayInWeek() {
        return mDayInWeek;
    }

    public void setDayInWeek(Integer _dayInWeek) {
        mDayInWeek = _dayInWeek;
    }

    public String getName() {
        return mWeekDayName;
    }

    public void setName(String _name) {
        mWeekDayName = _name;
    }

    public ArrayList<TimeTableEventVo> getEvents() {
        return mEvents;
    }

    public void setEvents(ArrayList<TimeTableEventVo> _events) {
        mEvents = _events;
    }

    @SerializedName("dayInWeek")
    private int mDayInWeek;

    @SerializedName("name")
    private String mWeekDayName;

    @SerializedName("events")
    private ArrayList<TimeTableEventVo> mEvents = new ArrayList<>();


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mDayInWeek);
        dest.writeString(mWeekDayName);
        dest.writeTypedList(mEvents);
    }

    private TimeTableDayVo(Parcel in) {
        mDayInWeek = in.readInt();
        mWeekDayName = in.readString();
        in.readTypedList(mEvents, TimeTableEventVo.CREATOR);
    }

    public static final Parcelable.Creator<TimeTableDayVo> CREATOR = new Parcelable.Creator<TimeTableDayVo>() {
        public TimeTableDayVo createFromParcel(Parcel source) {
            return new TimeTableDayVo(source);
        }

        public TimeTableDayVo[] newArray(int size) {
            return new TimeTableDayVo[size];
        }
    };
}
