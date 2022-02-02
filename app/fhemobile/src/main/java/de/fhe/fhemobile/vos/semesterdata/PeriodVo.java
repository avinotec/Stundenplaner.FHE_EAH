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
package de.fhe.fhemobile.vos.semesterdata;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 24.01.14.
 */
public class PeriodVo implements Parcelable {

    public PeriodVo() {
    }

    public PeriodVo(final String mBegin, final String mEnd, final String mName) {
        this.mBegin = mBegin;
        this.mEnd = mEnd;
        this.mName = mName;
    }

    public PeriodVo(final Parcel _In) {
        mBegin = _In.readString();
        mEnd = _In.readString();
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
        dest.writeString(mBegin);
        dest.writeString(mEnd);
        dest.writeString(mName);
    }

    public static final Parcelable.Creator<PeriodVo> CREATOR
            = new Parcelable.Creator<PeriodVo>() {
        public PeriodVo createFromParcel(final Parcel in) {
            return new PeriodVo(in);
        }

        public PeriodVo[] newArray(final int size) {
            return new PeriodVo[size];
        }
    };

    public String getBegin() {
        return mBegin;
    }

// --Commented out by Inspection START (02.11.2021 17:27):
//    public void setBegin(String mBegin) {
//        this.mBegin = mBegin;
//    }
// --Commented out by Inspection STOP (02.11.2021 17:27)

    public String getEnd() {
        return mEnd;
    }

// --Commented out by Inspection START (02.11.2021 17:27):
//    public void setEnd(String mEnd) {
//        this.mEnd = mEnd;
//    }
// --Commented out by Inspection STOP (02.11.2021 17:27)

    public String getName() {
        return mName;
    }

// --Commented out by Inspection START (02.11.2021 17:27):
//    public void setName(String mName) {
//        this.mName = mName;
//    }
// --Commented out by Inspection STOP (02.11.2021 17:27)

    //**********************************************************************
    @SerializedName("begin")
    private String mBegin;

    @SerializedName("end")
    private String mEnd;

    @SerializedName("name")
    private String mName;


}
