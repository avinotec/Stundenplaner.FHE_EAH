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
            Callback<CalVeranstaltungReponse> _Callback
    ) {
        veranstaltungApiService
                .v1CalveranstaltungGetAll(pageNumber, pageSize, name)
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
