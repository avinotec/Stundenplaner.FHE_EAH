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

import java.util.ArrayList;

/**
 * Created by paul on 12.03.15.
 */
public class StudyCourseVo implements Parcelable {

    public StudyCourseVo() {
    }

    protected StudyCourseVo(Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
        mTerms = in.createTypedArrayList(TermsVo.CREATOR);
    }

    public static final Creator<StudyCourseVo> CREATOR = new Creator<StudyCourseVo>() {
        @Override
        public StudyCourseVo createFromParcel(Parcel in) {
            return new StudyCourseVo(in);
        }

        @Override
        public StudyCourseVo[] newArray(int size) {
            return new StudyCourseVo[size];
        }
    };

    public String getId() {
        return mId;
    }

    public void setId(String _id) {
        mId = _id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String _title) {
        mTitle = _title;
    }

    public ArrayList<TermsVo> getTerms() {
        return mTerms;
    }

    public void setTerms(ArrayList<TermsVo> _terms) {
        mTerms = _terms;
    }

    @SerializedName("id")
    private String              mId;

    @SerializedName("title")
    private String              mTitle;

    @SerializedName("terms")
    private ArrayList<TermsVo>  mTerms;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeTypedList(mTerms);
    }
}
