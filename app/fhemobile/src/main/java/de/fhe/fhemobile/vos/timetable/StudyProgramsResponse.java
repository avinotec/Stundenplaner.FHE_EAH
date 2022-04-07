package de.fhe.fhemobile.vos.timetable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import de.fhe.fhemobile.vos.timetable.TimeTableStudyProgramVo2;

/**
 * Created by Nadja - 04/2022
 */
public class StudyProgramsResponse {

    public StudyProgramsResponse(){}

    /**
     * Return List of all bachelor and master study programs
     * @return
     */
    public ArrayList<TimeTableStudyProgramVo2> getFilteredStudyPrograms() {
        ArrayList filteredStudyPrograms = new ArrayList<TimeTableStudyProgramVo2>();

        for(TimeTableStudyProgramVo2 studyProgram : mStudyPrograms){
            if(studyProgram.getDegree().equals("Bachelor")
                    || studyProgram.getDegree().equals("Master")){
                filteredStudyPrograms.add(studyProgram);
            }
        }
        return filteredStudyPrograms;
    }

    @SerializedName("StgList")
    private ArrayList<TimeTableStudyProgramVo2> mStudyPrograms;
}
