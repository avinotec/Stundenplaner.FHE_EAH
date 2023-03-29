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

/**
 * Value object for a period in a semester e.g. "Vorlesungszeitraum" or "Ostern + Anreistag"
 *
 * Created by paul on 24.01.14.
 */
public class SemesterPeriodVo implements Parcelable {

    public SemesterPeriodVo() {
    }

    public SemesterPeriodVo(final String mBegin, final String mEnd, final String mName) {
        this.mStartDate = mBegin;
        this.mEndDate = mEnd;
        this.mName = mName;
    }

    public SemesterPeriodVo(final Parcel _In) {
        mStartDate = _In.readString();
        mEndDate = _In.readString();
        mName = _In.readString();
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
        dest.writeString(mStartDate);
        dest.writeString(mEndDate);
        dest.writeString(mName);
    }

    public static final Parcelable.Creator<SemesterPeriodVo> CREATOR
            = new Parcelable.Creator<SemesterPeriodVo>() {
        public SemesterPeriodVo createFromParcel(final Parcel in) {
            return new SemesterPeriodVo(in);
        }

        public SemesterPeriodVo[] newArray(final int size) {
            return new SemesterPeriodVo[size];
        }
    };


    public String getStartDateString() {
        return mStartDate;
    }

    public String getEndDateString() {
        return mEndDate;
    }

    public String getName() {
        return mName;
    }

    //**********************************************************************
    @SerializedName("begin")
    private String mStartDate;

    @SerializedName("end")
    private String mEndDate;

    @SerializedName("name")
    private String mName;   //e.g. "Vorlesungszeitraumn"


}
