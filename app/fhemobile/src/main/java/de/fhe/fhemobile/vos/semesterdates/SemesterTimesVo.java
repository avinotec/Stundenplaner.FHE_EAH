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
 * Created by paul on 27.01.14.
 */
public class SemesterTimesVo implements Parcelable {

    public SemesterTimesVo() {
    }

    public SemesterTimesVo(final DateVo mDate, final PeriodVo mPeriod) {
        this.mDate = mDate;
        this.mPeriod = mPeriod;
    }

    public SemesterTimesVo(final Parcel _In) {
        mDate = _In.readParcelable(DateVo.class.getClassLoader());
        mPeriod = _In.readParcelable(PeriodVo.class.getClassLoader());
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

    public static final Parcelable.Creator<SemesterTimesVo> CREATOR
            = new Parcelable.Creator<SemesterTimesVo>() {
        public SemesterTimesVo createFromParcel(final Parcel in) {
            return new SemesterTimesVo(in);
        }

        public SemesterTimesVo[] newArray(final int size) {
            return new SemesterTimesVo[size];
        }
    };


    public DateVo getDate() {
        return mDate;
    }

    public void setDate(final DateVo mDate) {
        this.mDate = mDate;
    }

    public PeriodVo getPeriod() {
        return mPeriod;
    }

    public void setPeriod(final PeriodVo mPeriod) {
        this.mPeriod = mPeriod;
    }


    @SerializedName("date")
    private DateVo mDate;

    @SerializedName("period")
    private PeriodVo mPeriod;


}
