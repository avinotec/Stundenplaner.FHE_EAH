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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by paul on 12.03.15
 * Edited by Nadja - 04/2022
 */
public class TimeTableStudyProgramVo implements Parcelable {

    private static final String TAG = "TimeTableStudyProgramVo";

    public TimeTableStudyProgramVo() {
    }

    protected TimeTableStudyProgramVo(final Parcel in) {
        mId = in.readString();
        mShortTitle = in.readString();
        mLongTitle = in.readString();
        mDegree = in.readString();
        //note: readHashMap reads data from a Parcel into an EXISTING map
        mSemesters = new HashMap<>();
        mSemesters = in.readHashMap(TimeTableSemesterVo.class.getClassLoader());
    }

    public static final Creator<TimeTableStudyProgramVo> CREATOR = new Creator<TimeTableStudyProgramVo>() {
        @Override
        public TimeTableStudyProgramVo createFromParcel(final Parcel in) {
            return new TimeTableStudyProgramVo(in);
        }

        @Override
        public TimeTableStudyProgramVo[] newArray(final int size) {
            return new TimeTableStudyProgramVo[size];
        }
    };

    public String getId() {
        return mId;
    }

    public void setId(final String _id) {
        mId = _id;
    }

    public String getShortTitle() {
        return mShortTitle;
    }

    public String getLongTitle() {
        return mLongTitle;
    }

    public ArrayList<TimeTableSemesterVo> getSemesters() {
        return new ArrayList(mSemesters.values());
    }


    static final String BACHELOR_BEFORE="Bachelor: ";
    static final String BACHELOR_AFTER=": B";
    static final String MASTER_BEFORE ="Master: ";
    static final String MASTER_AFTER =": M";

//    public static void alterTitle(final List<TimeTableStudyProgramVo> list){
//        for (final TimeTableStudyProgramVo studyProgram : list){
//            Log.d(TAG, "alterTitle: "+ studyProgram.getTitle());
//            if(studyProgram.getTitle().contains(BACHELOR_BEFORE)){
//                studyProgram.setTitle(studyProgram.getTitle().replace(BACHELOR_BEFORE,"")+BACHELOR_AFTER);
//            }
//            else if(studyProgram.getTitle().contains(MASTER_BEFORE)){
//                studyProgram.setTitle(studyProgram.getTitle().replace(MASTER_BEFORE,"")+MASTER_AFTER);
//            }
////            else {
////                //nothing
////            }
//        }
//
//    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(mId);
        dest.writeString(mShortTitle);
        dest.writeString(mLongTitle);
        dest.writeString(mDegree);
        dest.writeMap(mSemesters);
    }

    @SerializedName("posId")
    private String              mId;

    @SerializedName("stgNameshort")
    private String              mShortTitle;

    @SerializedName("stgNamelong")
    private String              mLongTitle;

    @SerializedName("stgDegree")
    private String              mDegree;

    @SerializedName("semesterData")
    private Map<String, TimeTableSemesterVo> mSemesters;
}
