package de.fhe.fhemobile.api;

import org.openapitools.client.ApiClient;
import org.openapitools.client.api.BuchungsgruppeApi;
import org.openapitools.client.model.BuchungsGruppeByIdResponse;

import retrofit2.Callback;

public class MosesBuchungsGruppeApi {
    private final BuchungsgruppeApi buchungsgruppeApiService;

    public MosesBuchungsGruppeApi(ApiClient apiClient) {
        buchungsgruppeApiService = apiClient
                .createService(BuchungsgruppeApi.class);
    }

    public void buchungsGruppeById(
            Integer id,
            Callback<BuchungsGruppeByIdResponse> _Callback
    ) {
        buchungsgruppeApiService
                .v1BuchungsgruppeidGetById(id)
                .enqueue(_Callback);
    }
}
