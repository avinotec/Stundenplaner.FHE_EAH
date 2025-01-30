package de.fhe.fhemobile.api;

import android.os.Build;
import android.util.Log;

import org.openapitools.client.ApiClient;
import org.openapitools.client.api.VeranstaltungApi;
import org.openapitools.client.model.CalVeranstaltung;
import org.openapitools.client.model.CalVeranstaltungByIdReponse;
import org.openapitools.client.model.CalVeranstaltungReponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MosesCalVeranstaltungApi {
    private final VeranstaltungApi veranstaltungApiService;

    public MosesCalVeranstaltungApi(ApiClient apiClient) {
        this.veranstaltungApiService = apiClient.createService(VeranstaltungApi.class);
    }

    public void calVeranstaltungGetAll(
            Integer pageNumber,
            Integer pageSize,
            String name,
            Integer planungsgruppeId,
            Integer semesterId,
            Callback<CalVeranstaltungReponse> _Callback
    ) {
        veranstaltungApiService
                .v1CalveranstaltungGetAll(pageNumber, pageSize, name, planungsgruppeId, semesterId)
                .enqueue(_Callback);
    }

    public void calVeranstaltungById(
            Integer id,
            Callback<CalVeranstaltungByIdReponse> _Callback
    ) {
        veranstaltungApiService
                .v1CalveranstaltungidGetById(id)
                .enqueue(_Callback);
    }

    public void calVeranstaltungByEid(
            String eidFromImport,
            Callback<CalVeranstaltungByIdReponse> _Callback
    ) {
        veranstaltungApiService
                .v1CalveranstaltungGetByEid(eidFromImport)
                .enqueue(_Callback);
    }

    /**
     * This is an important method as it returns a list of CalVeranstaltung for a specific
     * study program, semester and group.
     * This might not be the las method to call in the application as you could use one of its
     * 'buchungsgruppeList' ID values to query in buchungsgruppe/{buchungsgruppeListId}
     *
     * @param pageSize
     * @param planungsgruppeId
     * @param _Callback
     */
    public void calVeranstaltungByPlanungsgruppeId(
            Integer pageSize,
            Integer planungsgruppeId,
            Integer semesterId,
            Callback<CalVeranstaltungReponse> _Callback
    ) {
        veranstaltungApiService
                .v1CalveranstaltungGetAll(
                        null,
                        pageSize,
                        null,
                        planungsgruppeId,
                        semesterId
                ).enqueue(_Callback);
    }

    Callback<CalVeranstaltungReponse> calResponseCallback =
            new Callback<CalVeranstaltungReponse>() {
                @Override
                public void onResponse(
                        Call<CalVeranstaltungReponse> call,
                        Response<CalVeranstaltungReponse> response
                ) {
                    if (response.isSuccessful()) {
                        CalVeranstaltungReponse calVeranstaltungReponse = response.body();
                        List<CalVeranstaltung> calList = null;

                        if (calVeranstaltungReponse != null) {
                            calList = calVeranstaltungReponse.getData();
                        }


                        for (CalVeranstaltung calVeranstaltung : calList) {
                            Log.e("calz", calVeranstaltung.getKurzname());
                        }
                    }
                }

                @Override
                public void onFailure(
                        Call<CalVeranstaltungReponse> call,
                        Throwable throwable
                ) {
                    Log.e("calz", "damnson");
                }
            };
}
