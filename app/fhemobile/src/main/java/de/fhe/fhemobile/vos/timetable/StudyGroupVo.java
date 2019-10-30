/*
 * Copyright (c) 2014-2019 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fhe.fhemobile.vos.timetable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 12.03.15.
 */
public class StudyGroupVo implements Parcelable {

    public StudyGroupVo() {
    }

    protected StudyGroupVo(Parcel in) {
        mTitle = in.readString();
        mTimeTableId = in.readString();
    }

    public static final Creator<StudyGroupVo> CREATOR = new Creator<StudyGroupVo>() {
        @Override
        public StudyGroupVo createFromParcel(Parcel in) {
            return new StudyGroupVo(in);
        }

        @Override
        public StudyGroupVo[] newArray(int size) {
            return new StudyGroupVo[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String _title) {
        mTitle = _title;
    }

    public String getTimeTableId() {
        return mTimeTableId;
    }

    public void setTimeTableId(String _timeTableId) {
        mTimeTableId = _timeTableId;
    }

    @SerializedName("title")
    private String mTitle;

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
