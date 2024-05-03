package de.fhe.fhemobile.api;

import android.os.Build;
import android.util.Log;

import org.openapitools.client.ApiClient;
import org.openapitools.client.api.StudiengangApi;
import org.openapitools.client.model.Studiengang;
import org.openapitools.client.model.StudiengangByIdReponse;
import org.openapitools.client.model.StudiengangReponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MosesStudiengangApi {
    private final StudiengangApi studiengangApiService;

    public MosesStudiengangApi(ApiClient apiClient) {
        this.studiengangApiService = apiClient.createService(StudiengangApi.class);
    }

    public void studiengangGetAll() {
        studiengangApiService
                .v1StudiengangGetAll(null, 1000)
                .enqueue(studiengangReponseCallback);
    }

    public void studiengangGetById(Integer id) {
        studiengangApiService
                .v1StudiengangidGetById(id)
                .enqueue(studiengangByIdReponseCallback);
    }

    Callback<StudiengangReponse> studiengangReponseCallback =
            new Callback<StudiengangReponse>() {
                @Override
                public void onResponse(
                        Call<StudiengangReponse> call,
                        Response<StudiengangReponse> response
                ) {
                    if (response.isSuccessful()) {
                        StudiengangReponse studiengangReponse = response.body();

                        List<Studiengang> studiengangList = null;

                        if (studiengangReponse != null) {
                            studiengangList = studiengangReponse.getData();

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                studiengangList.forEach((studiengang -> {
                                    Log.d("calz", studiengang.getName());
                                }));
                            }
                        }
                    }
                }

                @Override
                public void onFailure(
                        Call<StudiengangReponse> call,
                        Throwable throwable
                ) {
                }
            };

    public Callback<StudiengangByIdReponse> studiengangByIdReponseCallback =
            new Callback<StudiengangByIdReponse>() {
                @Override
                public void onResponse(
                        Call<StudiengangByIdReponse> call,
                        Response<StudiengangByIdReponse> response
                ) {
                    if (response.isSuccessful()) {
                        StudiengangByIdReponse studiengangReponse = response.body();

                        List<Studiengang> studiengangList = null;
                        Studiengang studiengang = null;

                        if (studiengangReponse != null) {
                            studiengangList = studiengangReponse.getData();
                            if (studiengangList != null) {
                                studiengang = studiengangList.get(0);
                                Log.e("calz", studiengang.getName());
                            }
                        }
                    }
                }

                @Override
                public void onFailure(
                        Call<StudiengangByIdReponse> call,
                        Throwable throwable
                ) {

                }
            };
}
