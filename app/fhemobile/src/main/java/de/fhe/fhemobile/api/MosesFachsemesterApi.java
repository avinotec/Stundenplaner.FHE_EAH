package de.fhe.fhemobile.api;

import org.openapitools.client.ApiClient;
import org.openapitools.client.api.FachsemesterzuordnungApi;
import org.openapitools.client.model.FachsemesterResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MosesFachsemesterApi {
    private final FachsemesterzuordnungApi fachsemesterApi;

    public MosesFachsemesterApi(ApiClient apiClient) {
        fachsemesterApi = apiClient.createService(FachsemesterzuordnungApi.class);
    }

    public void fachsemesterGetAll(
            Integer stupoId,
            Integer lvvId,
            List<String> idlist,
            String eidlist,
            Callback<FachsemesterResponse> _Callback
    ) {
        fachsemesterApi
                .v1FachsemesterzuordnungGetAll(
                        stupoId,
                        lvvId,
                        idlist,
                        eidlist
                ).enqueue(_Callback);
    }

    Callback<FachsemesterResponse> fachsemesterResponseCallback =
            new Callback<FachsemesterResponse>() {
                @Override
                public void onResponse(
                        Call<FachsemesterResponse> call,
                        Response<FachsemesterResponse> response
                ) {
                }

                @Override
                public void onFailure(
                        Call<FachsemesterResponse> call,
                        Throwable throwable
                ) {
                }
            };
}
