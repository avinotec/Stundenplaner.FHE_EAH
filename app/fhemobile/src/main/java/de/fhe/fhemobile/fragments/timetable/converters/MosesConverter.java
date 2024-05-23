package de.fhe.fhemobile.fragments.timetable.converters;

import org.openapitools.client.model.Studiengang;
import org.openapitools.client.model.VplGruppe;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.fhe.fhemobile.vos.timetable.TimetableSemesterVo;
import de.fhe.fhemobile.vos.timetable.TimetableStudyGroupVo;
import de.fhe.fhemobile.vos.timetable.TimetableStudyProgramVo;

public class MosesConverter {
    /**
     * This method is needed to convert a VplGruppe to a TimetableSemesterVo in order to
     * keep existing code working with Moses.
     * We only need the semester and don't care too much about its other values, but still have
     * to set something.
     *
     * @param vplGruppe
     * @return
     */
    public TimetableSemesterVo semesterFromVplGruppe(VplGruppe vplGruppe) {
        String kurzName = vplGruppe.getKurzname();
        String name = vplGruppe.getName();
        String semester = vplGruppe.getFachsemester().toString(); // <-- we need this
        TimetableSemesterVo semesterVoFromVplGruppe = new TimetableSemesterVo();

        semesterVoFromVplGruppe.setmId(kurzName);
        semesterVoFromVplGruppe.setmTitle(name);
        semesterVoFromVplGruppe.setmNumber(semester);

        return semesterVoFromVplGruppe;
    }

    public TimetableStudyGroupVo studyGroupFromVplGruppe(VplGruppe vplGruppe) {
        TimetableStudyGroupVo convert = new TimetableStudyGroupVo();
        convert.setmTitle(vplGruppe.getName());
        convert.setmNumber(vplGruppe.getName());
        convert.setmTitle(vplGruppe.getKurzname());
        convert.setGroupId(vplGruppe.getId());

        return convert;
    }

    public TimetableStudyProgramVo studyProgramFromStudiengang(Studiengang studiengang) {
        TimetableStudyProgramVo convert = new TimetableStudyProgramVo();

        convert.setmLongTitle(studiengang.getName());
        convert.setmShortTitle(studiengang.getKurzname());
        convert.setStudyProgramId(studiengang.getId());
        /*
         * Refactor consider using Optional Class
         * or displaying degree in a different way.
         */
        if (studiengang.getStudiengangart() != null) {
            convert.setmDegree(studiengang.getStudiengangart().getName());
        } else {
            convert.setmDegree(" ");
        }

        return convert;
    }

    /**
     * Convert one list of VplGruppe to a list of TimetableSemesterVo
     * @param vplGruppeResponse
     * @return
     */
    public List<TimetableSemesterVo> semesterVosListFromVplGruppe(
            List<VplGruppe> vplGruppeResponse
    ) {
        List<TimetableSemesterVo> semesterVoList = new ArrayList<>();

        for (VplGruppe vplGruppe : vplGruppeResponse) {
            semesterVoList.add(semesterFromVplGruppe(vplGruppe));
        }

        return semesterVoList;
    }

    /**
     * The following method seems like an odd hack, and it is,
     * but we need to construct a unique, sorted set of TimetableSemesterVo objects as there
     * isn't an easy way to just get all available semesters assigned
     * to a specific study program.
     * What does this mean? Moses provides the /vplgruppe/{studiengangId} endpoint which returns
     * all groups assigned to a specific study program. Besides other fields in the response, it
     * also contains an assigned semester for each group but it's not a unique list.
     * We get something like this (simplified):
     * [
     *  { name: '', (...), fachsemester: 6 },
     *  { name: '', (...), fachsemester: 6 },
     *  { name: '', (...), fachsemester: 2 },
     *  { name: '', (...), fachsemester: 1 },
     *  { name: '', (...), fachsemester: 1 }
     * ]
     * This then gets converted into a set which means we loose some values which are merged
     * in this process - which is "okay" for our purposes as we just care about the semester.
     * Ideally this would be replaced by a yet to developed endpoint from the Moses vendor.
     *
     * @param semesterVoList
     * @return
     */
    public Set<TimetableSemesterVo> semesterVosSetFromList(
            List<TimetableSemesterVo> semesterVoList
    ) {
        TreeSet<TimetableSemesterVo> semesterVoSet = new TreeSet<>((semester1, semester2) -> {
            int number1 = Integer.parseInt(semester1.getNumber());
            int number2 = Integer.parseInt(semester2.getNumber());

            return Integer.compare(number1, number2);
        });

        semesterVoSet.addAll(semesterVoList);

        return semesterVoSet;
    }
}
