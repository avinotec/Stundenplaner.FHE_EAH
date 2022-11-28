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

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by paul on 12.03.15
 * Edited by Nadja - 04/2022
 */
public class TimetableDialogResponse {

    public TimetableDialogResponse(){

    }

    public ArrayList<TimetableStudyProgramVo> getStudyProgramsAsList() {
        return new ArrayList(mStudyPrograms.values());
    }

    public Map<String, TimetableStudyProgramVo> getStudyPrograms() {
        return mStudyPrograms;
    }

    @SerializedName("studentset")
    private Map<String, TimetableStudyProgramVo> mStudyPrograms;
}
