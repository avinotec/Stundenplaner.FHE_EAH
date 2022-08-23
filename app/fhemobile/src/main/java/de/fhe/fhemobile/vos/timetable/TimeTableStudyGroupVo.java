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
package de.fhe.fhemobile.vos.timetable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 12.03.15
 * Edited by Nadja - 04/2022
 */
public class TimeTableStudyGroupVo implements Parcelable {

    public TimeTableStudyGroupVo() {
    }

    protected TimeTableStudyGroupVo(final Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
        mNumber = in.readString();
    }

    public String getTitle() {
        return mTitle;
    }

    public String getStudyGroupId() {
        return mId;
    }

    public String getNumber() {
        //mNumber not available when fetched for MySchedule
        if(mNumber == null){
            final String[] splitString = mTitle.split("\\.");
            mNumber = splitString[splitString.length-1].replaceAll("\\D", "");
        }
        return mNumber;
    }


    // PARCELABLE --------------------------------------------------------------------------------
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeString(mNumber);
    }

    public static final Creator<TimeTableStudyGroupVo> CREATOR = new Creator<TimeTableStudyGroupVo>() {
        @Override
        public TimeTableStudyGroupVo createFromParcel(final Parcel in) {
            return new TimeTableStudyGroupVo(in);
        }

        @Override
        public TimeTableStudyGroupVo[] newArray(final int size) {
            return new TimeTableStudyGroupVo[size];
        }
    };

    // End PARCELABLE --------------------------------------------------------------------------------

    //not SPLUS-Id
    @SerializedName("studentsetId")
    private String mId;

    @SerializedName("studentsetName")
    private String mTitle;

    @SerializedName("studentsetNumber")
    private String mNumber;
}
