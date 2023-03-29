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
 * Helper value object to hold either a period or a date in the semester
 *
 * Created by paul on 27.01.14.
 */
public class SemesterPeriodOrDateVo implements Parcelable {

    public SemesterPeriodOrDateVo() {
    }

    public SemesterPeriodOrDateVo(final SemesterDateVo mDate, final SemesterPeriodVo mPeriod) {
        this.mDate = mDate;
        this.mPeriod = mPeriod;
    }

    public SemesterPeriodOrDateVo(final Parcel _In) {
        mDate = _In.readParcelable(SemesterDateVo.class.getClassLoader());
        mPeriod = _In.readParcelable(SemesterPeriodVo.class.getClassLoader());
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
        dest.writeParcelable(mDate, 0);
        dest.writeParcelable(mPeriod, 0);
    }

    public static final Parcelable.Creator<SemesterPeriodOrDateVo> CREATOR
            = new Parcelable.Creator<SemesterPeriodOrDateVo>() {
        public SemesterPeriodOrDateVo createFromParcel(final Parcel in) {
            return new SemesterPeriodOrDateVo(in);
        }

        public SemesterPeriodOrDateVo[] newArray(final int size) {
            return new SemesterPeriodOrDateVo[size];
        }
    };


    public SemesterDateVo getDate() {
        return mDate;
    }

    public void setDate(final SemesterDateVo mDate) {
        this.mDate = mDate;
    }

    public SemesterPeriodVo getPeriod() {
        return mPeriod;
    }

    public void setPeriod(final SemesterPeriodVo mPeriod) {
        this.mPeriod = mPeriod;
    }


    @SerializedName("date")
    private SemesterDateVo mDate;

    @SerializedName("period")
    private SemesterPeriodVo mPeriod;


}
