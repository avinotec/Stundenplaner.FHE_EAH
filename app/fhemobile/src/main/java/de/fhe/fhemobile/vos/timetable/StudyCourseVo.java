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
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paul on 12.03.15.
 */
public class StudyCourseVo implements Parcelable {
    private static final String TAG = "StudyCourseVo";

    public StudyCourseVo() {
    }

    protected StudyCourseVo(final Parcel in) {
        mId = in.readString();
        mTitle = correctUmlauts(in.readString());
        mSemesters = in.createTypedArrayList(SemesterVo.CREATOR);
    }

    public static final Creator<StudyCourseVo> CREATOR = new Creator<StudyCourseVo>() {
        @Override
        public StudyCourseVo createFromParcel(final Parcel in) {
            return new StudyCourseVo(in);
        }

        @Override
        public StudyCourseVo[] newArray(final int size) {
            return new StudyCourseVo[size];
        }
    };

    public String getId() {
        return mId;
    }

    public void setId(final String _id) {
        mId = _id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(final String _title) {
        mTitle = _title;
    }

    public ArrayList<SemesterVo> getSemesters() {
        return mSemesters;
    }

    public void setTerms(final ArrayList<SemesterVo> _semesters) {
        mSemesters = _semesters;
    }
    static final String BACHELOR_BEFORE="Bachelor: ";
    static final String BACHELOR_AFTER=": B";
    static final String MASTER_BEFORE ="Master: ";
    static final String MASTER_AFTER =": M";

    public static void alterTitle(final List<StudyCourseVo> list){
        for (final StudyCourseVo semester:list){
            Log.d(TAG, "alterTitle: "+ semester.getTitle());
            if(semester.getTitle().contains(BACHELOR_BEFORE)){
                semester.setTitle(semester.getTitle().replace(BACHELOR_BEFORE,"")+BACHELOR_AFTER);
            }
            else if(semester.getTitle().contains(MASTER_BEFORE)){
                semester.setTitle(semester.getTitle().replace(MASTER_BEFORE,"")+MASTER_AFTER);
            }
//            else {
//                //nothing
//            }
        }

    }

    @SerializedName("id")
    private String              mId;

    @SerializedName("title")
    private String              mTitle;

    @SerializedName("terms")
    private ArrayList<SemesterVo> mSemesters;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeTypedList(mSemesters);
    }
}
