package de.fhe.fhemobile.api;

import android.os.Build;
import android.util.Log;

import org.openapitools.client.ApiClient;
import org.openapitools.client.api.VeranstaltungApi;
import org.openapitools.client.model.CalVeranstaltung;
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

    public void gV() {
        veranstaltungApiService
                .v1CalveranstaltungGetAll(null, null)
                .enqueue(calResponseCallback);
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


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            calList.forEach(calVeranstaltung -> Log.e("calz", calVeranstaltung.getKurzname()));
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
