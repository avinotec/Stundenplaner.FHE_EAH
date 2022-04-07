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

import java.util.List;

/**
 * Created by Nadja - 04/2022
 */
public class TimeTableStudyProgramVo2 implements Parcelable {

    private static final String TAG = "TT-StudyProgramVo2";

    public TimeTableStudyProgramVo2() {
    }

    protected TimeTableStudyProgramVo2(final Parcel in) {
        mDegree = in.readString();
        mShortTitle = correctUmlauts(in.readString());
        mTitle = correctUmlauts(in.readString());
    }

    public static final Creator<TimeTableStudyProgramVo2> CREATOR = new Creator<TimeTableStudyProgramVo2>() {
        @Override
        public TimeTableStudyProgramVo2 createFromParcel(final Parcel in) {
            return new TimeTableStudyProgramVo2(in);
        }

        @Override
        public TimeTableStudyProgramVo2[] newArray(int i) {
            return new TimeTableStudyProgramVo2[0];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(mDegree);
        dest.writeString(mShortTitle);
        dest.writeString(mTitle);
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(final String _title) {
        mTitle = _title;
    }

    public String getShortTitle() {
        return mShortTitle;
    }

    public String getDegree() {
        return mDegree;
    }

    public String getTitleAndDegree(){
        return mTitle + " (" + mDegree + ")";
    }



    @SerializedName("Degree")
    private String  mDegree;

    @SerializedName("StgShort")
    private String  mShortTitle;

    @SerializedName("StgLong")
    private String  mTitle;
}
