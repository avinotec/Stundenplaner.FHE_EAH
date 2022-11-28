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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Value Object for a semester of a study program
 *
 * Created by paul on 12.03.15
 * Edited by Nadja - 04/2022
 */
public class TimetableSemesterVo implements Parcelable {

    private static final String TAG = TimetableSemesterVo.class.getSimpleName();

    public TimetableSemesterVo() {
    }

    protected TimetableSemesterVo(final Parcel in) {
        mId = in.readString();
        mNumber = in.readString();
        mTitle = in.readString();
        in.readMap(mStudyGroups, TimetableStudyGroupVo.class.getClassLoader());
    }

    public String getId() { return mId;  }

    public String getNumber() {
        return mNumber;
    }

    public String getTitle() {
        return mTitle;
    }

    public ArrayList<TimetableStudyGroupVo> getStudyGroupList() {
        return new ArrayList<>(mStudyGroups.values());
    }

    // PARCELABLE --------------------------------------------------------------------------------
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(mId);
        dest.writeString(mNumber);
        dest.writeString(mTitle);
        dest.writeMap(mStudyGroups);
    }

    public static final Creator<TimetableSemesterVo> CREATOR = new Creator<TimetableSemesterVo>() {
        @Override
        public TimetableSemesterVo createFromParcel(final Parcel in) {
            return new TimetableSemesterVo(in);
        }

        @Override
        public TimetableSemesterVo[] newArray(final int size) {
            return new TimetableSemesterVo[size];
        }
    };

    // End PARCELABLE --------------------------------------------------------------------------------

    @SerializedName("posId")
    private String mId;

    @SerializedName("posNumber")
    private String mNumber;

    @SerializedName("posName")
    private String mTitle;

    @SerializedName("studentsetData")
    private final Map<String, TimetableStudyGroupVo> mStudyGroups = new HashMap<>();
}
