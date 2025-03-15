package de.fhe.fhemobile.fragments.timetable;

import org.openapitools.client.model.Buchung;

import java.util.ArrayList;

public class MosesCalVeranstaltungWithBuchungsGruppe {
    Integer calVeranstaltungId;
    String calVeranstaltungName;
    Integer buchungsGruppeId;
    ArrayList<Buchung> einzeltermine;

    public MosesCalVeranstaltungWithBuchungsGruppe(
            Integer calVeranstaltungId,
            String calVeranstaltungName,
            Integer buchungsGruppeId
    ) {
        this.calVeranstaltungId = calVeranstaltungId;
        this.calVeranstaltungName = calVeranstaltungName;
        this.buchungsGruppeId = buchungsGruppeId;
        this.einzeltermine = new ArrayList<>();
    }
}