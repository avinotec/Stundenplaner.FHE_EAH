package de.fhe.fhemobile.api;

import android.os.Build;
import android.util.Log;

import org.openapitools.client.ApiClient;
import org.openapitools.client.api.StudienUndPrfungsordnungApi;
import org.openapitools.client.model.Stupo;
import org.openapitools.client.model.StupoReponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MosesStupoApi {
    private final StudienUndPrfungsordnungApi studienUndPruefungsordnungApiService;

    public MosesStupoApi(ApiClient apiClient) {
        studienUndPruefungsordnungApiService = apiClient
                .createService(StudienUndPrfungsordnungApi.class);
    }

    public void stupoGetAll(
            List<String> idlist,
            String eidlist,
            String name,
            Callback<StupoReponse> _Callback
    ) {
        studienUndPruefungsordnungApiService
                .v1StupoGetAll(idlist, eidlist, name)
                .enqueue(_Callback);
    }

    Callback<StupoReponse> stupoReponseCallback = new Callback<StupoReponse>() {
        @Override
        public void onResponse(
                Call<StupoReponse> call,
                Response<StupoReponse> response
        ) {
            if (response.isSuccessful()) {
                StupoReponse stupoReponseReponse = response.body();

                List<Stupo> stupoResponseList = null;

                if (stupoReponseReponse != null) {
                    stupoResponseList = stupoReponseReponse.getData();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        stupoResponseList.forEach((stupo -> {
                            Log.d("calz", stupo.getName());
                        }));
                    }
                }
            }
        }

        @Override
        public void onFailure(
                Call<StupoReponse> call,
                Throwable throwable
        ) {
        }
    };
}
