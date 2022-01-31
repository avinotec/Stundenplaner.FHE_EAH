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

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 12.03.15.
 */
public class TimeTableStudyGroupVo implements Parcelable {

    public TimeTableStudyGroupVo() {
    }

    protected TimeTableStudyGroupVo(Parcel in) {
        mTitle = in.readString();
        mTimeTableId = in.readString();
    }

    public static final Creator<TimeTableStudyGroupVo> CREATOR = new Creator<TimeTableStudyGroupVo>() {
        @Override
        public TimeTableStudyGroupVo createFromParcel(Parcel in) {
            return new TimeTableStudyGroupVo(in);
        }

        @Override
        public TimeTableStudyGroupVo[] newArray(int size) {
            return new TimeTableStudyGroupVo[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(final String _title) {
        mTitle = _title;
    }

    public String getTimeTableId() {
        return mTimeTableId;
    }

    public String getShortTitle() {
        //e.g. mTitle = "ET(BA)5.01(ATTITi)
        return mTitle.replaceFirst("^\\w+\\(\\w+\\)\\d\\.", "");
    }

// --Commented out by Inspection START (02.11.2021 17:32):
//    public void setTimeTableId(final String _timeTableId) {
//        mTimeTableId = _timeTableId;
//    }
// --Commented out by Inspection STOP (02.11.2021 17:32)

    @SerializedName("title")
    private String mTitle;

    //SPLUS-Id
    @SerializedName("timetableId")
    private String mTimeTableId;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mTimeTableId);
    }
}
