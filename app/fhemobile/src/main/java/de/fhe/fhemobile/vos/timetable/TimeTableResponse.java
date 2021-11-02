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

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by paul on 12.03.15.
 */
public class TimeTableResponse {

    public TimeTableResponse() {
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String _title) {
        mTitle = correctUmlauts(_title);
    }

    public String getModified() {
        return mModified;
    }

    public void setModified(final String _modified) {
        mModified = _modified;
    }

    public ArrayList<StudyCourseVo> getStudyCourses() {
        return mStudyCourses;
    }

    public void setStudyCourses(final ArrayList<StudyCourseVo> _studyCourses) {
        mStudyCourses = _studyCourses;
    }

    @SerializedName("title")
    private String                   mTitle;

    @SerializedName("modified")
    private String                   mModified;

    @SerializedName("courseOfStudies")
    private ArrayList<StudyCourseVo> mStudyCourses;
}
