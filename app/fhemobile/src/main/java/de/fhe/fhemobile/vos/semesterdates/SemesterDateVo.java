/*
 *  Copyright (c) 2014-2023 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
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
 * Value object for an event in the semester e.g. "Semesterbeginn" or "Hochschulinformationstag"
 *
 * Created by paul on 24.01.14.
 */
public class SemesterDateVo implements Parcelable {

    public SemesterDateVo() {
    }

    public SemesterDateVo(final String mDate, final String mSemesterType) {
        this.mDate = mDate;
        this.mName = mSemesterType;
    }

    public SemesterDateVo(final Parcel _In) {
        mDate = _In.readString();
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
        dest.writeString(mDate);
        dest.writeString(mName);
    }

    public static final Parcelable.Creator<SemesterDateVo> CREATOR = new Parcelable.Creator<SemesterDateVo>() {
        public SemesterDateVo createFromParcel(final Parcel in) {
            return new SemesterDateVo(in);
        }

        public SemesterDateVo[] newArray(final int size) {
            return new SemesterDateVo[size];
        }
    };

    public String getDate() {
        return mDate;
    }


    public String getName() {
        return mName;
    }

    @SerializedName("date")
    private String mDate;

    @SerializedName("name")
    private String mName;


}
