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

import static de.fhe.fhemobile.utils.Utils.correctUmlauts;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Value Object for a semester of a study program
 *
 * Created by paul on 12.03.15.
 * Edited by nadja - 04/2022
 */
public class TimeTableSemesterVo implements Parcelable {

    private static final String TAG = "TimeTableSemesterVo";

    public TimeTableSemesterVo() {
    }

    protected TimeTableSemesterVo(final Parcel in) {
        mNumber = in.readInt();
        mTitle = correctUmlauts(in.readString());
        mStudyGroups = in.createTypedArrayList(TimeTableStudyGroupVo.CREATOR);
    }

    public static final Creator<TimeTableSemesterVo> CREATOR = new Creator<TimeTableSemesterVo>() {
        @Override
        public TimeTableSemesterVo createFromParcel(final Parcel in) {
            return new TimeTableSemesterVo(in);
        }

        @Override
        public TimeTableSemesterVo[] newArray(final int size) {
            return new TimeTableSemesterVo[size];
        }
    };

    public int getNumber() {
        return mNumber;
    }

    public void setId(final int _id) {
        mNumber = _id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(final String _title) {
        mTitle = _title;
    }

    public ArrayList<TimeTableStudyGroupVo> getStudyGroups() {
        return mStudyGroups;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(mNumber);
        dest.writeString(mTitle);
        dest.writeTypedList(mStudyGroups);
    }


    @SerializedName("posNumber")
    private int mNumber;

    @SerializedName("posName")
    private String mTitle;

    @SerializedName("studentsetData")
    private ArrayList<TimeTableStudyGroupVo> mStudyGroups;
}
