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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.fhe.fhemobile.comparator.SemesterComparator;

/**
 * Created by paul on 12.03.15
 * Edited by Nadja - 04/2022
 */
public class TimetableStudyProgramVo implements Parcelable {

    private static final String TAG = TimetableStudyProgramVo.class.getSimpleName();

    public TimetableStudyProgramVo() {
    }

    protected TimetableStudyProgramVo(final Parcel in) {
        mShortTitle = in.readString();
        mLongTitle = in.readString();
        mDegree = in.readString();
        in.readMap(mSemesters, TimetableSemesterVo.class.getClassLoader());
    }

    public String getId() {
        return mShortTitle;
    }

    public String getShortTitle() {
        return mShortTitle;
    }

    public void setmShortTitle(String mShortTitle) {
        this.mShortTitle = mShortTitle;
    }

    public String getLongTitle() {
        return mLongTitle;
    }

    public void setmLongTitle(String mLongTitle) {
        this.mLongTitle = mLongTitle;
    }

    public void setStudyProgramId(Integer studyProgramId) {
        this.studyProgramId = studyProgramId;
    }

    public Integer getStudyProgramId() {
        return studyProgramId;
    }

    /**
     * Get semesters as Arraylist, sorted by semester number
     * @return
     */
    public ArrayList<TimetableSemesterVo> getSemestersAsSortedList() {
        final ArrayList<TimetableSemesterVo>  semesterList = new ArrayList<>(mSemesters.values());
        Collections.sort(semesterList, new SemesterComparator());
        return semesterList;
    }


    public Map<String, TimetableSemesterVo> getSemesters() {
        return mSemesters;
    }

    /**
     * Get title plus abbreviation ("B" or "M") in brackets behind.
     * @return The name of the study program for display in GUI
     */
    public String getGuiName(){
        return mLongTitle + " (" + mDegree.charAt(0) + ")";
    }

    public String getDegree() {
        return mDegree;
    }

    public void setmDegree(String mDegree) {
        this.mDegree = mDegree;
    }

    // PARCELABLE --------------------------------------------------------------------------------

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(mShortTitle);
        dest.writeString(mLongTitle);
        dest.writeString(mDegree);
        dest.writeMap(mSemesters);
    }

    public static final Creator<TimetableStudyProgramVo> CREATOR = new Creator<TimetableStudyProgramVo>() {
        @Override
        public TimetableStudyProgramVo createFromParcel(final Parcel in) {
            return new TimetableStudyProgramVo(in);
        }

        @Override
        public TimetableStudyProgramVo[] newArray(final int size) {
            return new TimetableStudyProgramVo[size];
        }
    };

    // End PARCELABLE --------------------------------------------------------------------------------


    @SerializedName("stgNameshort")
    private String              mShortTitle;

    @SerializedName("stgNamelong")
    private String              mLongTitle;

    @SerializedName("stgDegree")
    private String              mDegree;

    @SerializedName("semesterData")
    private final Map<String, TimetableSemesterVo> mSemesters = new HashMap<>();

    @SerializedName("studyProgramId")
    private Integer studyProgramId;
}
