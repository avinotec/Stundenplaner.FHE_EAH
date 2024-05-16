package de.fhe.fhemobile.api;

import org.openapitools.client.ApiClient;
import org.openapitools.client.api.VplGruppeApi;
import org.openapitools.client.model.VplGruppe;

import java.util.List;

import retrofit2.Callback;

public class MosesVplGruppeApi {
    private final VplGruppeApi vplGruppeApiService;

    public MosesVplGruppeApi(ApiClient apiClient) {
        this.vplGruppeApiService = apiClient.createService(VplGruppeApi.class);
    }

    /**
     * "Verlaufsplan Gruppen" for a specific studiengangId.
     * Example: E-Commerce (B.Sc.)- studiengangId: 7
     * This is currently used as a workaround(?) to get a list of available semesters
     * for a specific studiengangId. It returns something like this: 2, 2, 2, 6, 6, 8, 8, 8
     * which then gets converted into a sorted set of semesters (2, 6, 8). After doing this we can
     * query using vplgruppestudiengangIdsemesterFindAll(7, 2, _Callback) for example.
     *
     * @param studiengangId
     * @param _Callback
     */
    public void vplgruppestudiengangIdFindAll(
            int studiengangId,
            Callback<List<VplGruppe>> _Callback
    ) {
        vplGruppeApiService
                .v1VplgruppestudiengangIdFindAll(studiengangId)
                .enqueue(_Callback);
    }

    /**
     * "Verlaufsplan Gruppen" for a specific studiengangId and semester.
     * Example: E-Commerce (B.Sc.)- studiengangId: 7, semester: 1 (the literal "first" semester).
     * Should be something like: WIEC(BA)2.01, WIEC(BA)2.02, (...), WIEC(BA)2.09.
     * This groups then can(?) should be somehow used to query in /calveranstaltung if we are lucky.
     *
     * @param studiengangId
     * @param semester
     * @param _Callback
     */
    public void vplgruppestudiengangIdsemesterFindAll(
            int studiengangId,
            int semester,
            Callback<List<VplGruppe>> _Callback
    ) {
        vplGruppeApiService
                .v1VplgruppestudiengangIdsemesterFindAll(studiengangId, semester)
                .enqueue(_Callback);
    }
}
