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
public class TimetableStudyGroupVo implements Parcelable {

    public TimetableStudyGroupVo() {
    }

    protected TimetableStudyGroupVo(final Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
        mNumber = in.readString();
        groupId = in.readInt();
    }

    public String getTitle() {
        return mTitle;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getNumber() {
        //mNumber not available when fetched for MySchedule
        if (mNumber == null) {
            final String[] splitString = mTitle.split("\\.");
            mNumber = splitString[splitString.length - 1].replaceAll("\\D", "");
        }
        return mNumber;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setmNumber(String mNumber) {
        this.mNumber = mNumber;
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

    public static final Creator<TimetableStudyGroupVo> CREATOR = new Creator<TimetableStudyGroupVo>() {
        @Override
        public TimetableStudyGroupVo createFromParcel(final Parcel in) {
            return new TimetableStudyGroupVo(in);
        }

        @Override
        public TimetableStudyGroupVo[] newArray(final int size) {
            return new TimetableStudyGroupVo[size];
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

    @SerializedName("groupId")
    private Integer groupId;
}
