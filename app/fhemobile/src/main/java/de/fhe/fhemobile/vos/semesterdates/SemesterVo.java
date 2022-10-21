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
package de.fhe.fhemobile.vos.semesterdates;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paul on 27.01.14.
 *
 * Vo used when displaying semester dates
 */
public class SemesterVo implements Parcelable {

    public SemesterVo() {
    }

    public SemesterVo(final String mName, final String mLongName, final List<SemesterTimesVo> mCourseTimes, final List<SemesterTimesVo> mHolidays, final List<SemesterTimesVo> mImportantDates) {
        this.mName              = mName;
        this.mLongName          = mLongName;
        this.mCourseTimes       = mCourseTimes;
        this.mHolidays          = mHolidays;
        this.mImportantDates    = mImportantDates;
    }

    public SemesterVo(final Parcel _In) {
        mName       = _In.readString();
        mLongName   = _In.readString();
        _In.readTypedList(mCourseTimes, SemesterTimesVo.CREATOR);
        _In.readTypedList(mHolidays, SemesterTimesVo.CREATOR);
        _In.readTypedList(mImportantDates, SemesterTimesVo.CREATOR);
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(mName);
        dest.writeString(mLongName);
        dest.writeTypedList(mCourseTimes);
        dest.writeTypedList(mHolidays);
        dest.writeTypedList(mImportantDates);
    }

    public static final Parcelable.Creator<SemesterVo> CREATOR
            = new Parcelable.Creator<SemesterVo>() {
        public SemesterVo createFromParcel(final Parcel in) {
            return new SemesterVo(in);
        }

        public SemesterVo[] newArray(final int size) {
            return new SemesterVo[size];
        }
    };

    public String getName() {
        return mName;
    }

    public void setName(final String mName) {
        this.mName = mName;
    }

    public String getLongName() {
        return mLongName;
    }

    public void setLongName(final String mLongName) {
        this.mLongName = mLongName;
    }

    public List<SemesterTimesVo> getCourseTimes() {
        return mCourseTimes;
    }

    public void setCourseTimes(final List<SemesterTimesVo> mCourseTimes) {
        this.mCourseTimes = mCourseTimes;
    }

    public List<SemesterTimesVo> getHolidays() {
        return mHolidays;
    }

    public void setHolidays(final List<SemesterTimesVo> mHolidays) {
        this.mHolidays = mHolidays;
    }

    public List<SemesterTimesVo> getImportantDates() {
        return mImportantDates;
    }

    public void setImportantDates(final List<SemesterTimesVo> mImportantDates) {
        this.mImportantDates = mImportantDates;
    }


    @SerializedName("name")
    private String mName;

    @SerializedName("longName")
    private String mLongName;

    @SerializedName("courseTimes")
    private List<SemesterTimesVo> mCourseTimes = new ArrayList<SemesterTimesVo>();

    @SerializedName("holidays")
    private List<SemesterTimesVo> mHolidays = new ArrayList<SemesterTimesVo>();

    @SerializedName("importantDates")
    private List<SemesterTimesVo> mImportantDates = new ArrayList<SemesterTimesVo>();

}
