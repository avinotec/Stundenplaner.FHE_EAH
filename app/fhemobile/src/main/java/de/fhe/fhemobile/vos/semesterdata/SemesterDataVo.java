package de.fhe.fhemobile.vos.semesterdata;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 03.03.15.
 */
public class SemesterDataVo {

    public SemesterDataVo() {
    }

    public SemesterVo[] getSemester() {
        return mSemester;
    }

    public void setSemester(SemesterVo[] _semester) {
        mSemester = _semester;
    }

    @SerializedName("Semester")
    private SemesterVo[] mSemester;
}
