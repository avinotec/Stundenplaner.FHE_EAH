package de.fhe.fhemobile.api;

import org.openapitools.client.ApiClient;
import org.openapitools.client.api.BuchungApi;
import org.openapitools.client.model.BuchungByIdResponse;

import retrofit2.Callback;

public class MosesBuchungApi {
    private final BuchungApi buchungApiService;

    public MosesBuchungApi(ApiClient apiClient) {
        this.buchungApiService = apiClient.createService(BuchungApi.class);
    }

    public void buchungById(
            Integer id,
            Callback<BuchungByIdResponse> _Callback
    ) {
        buchungApiService
                .v1BuchungidGetById(id)
                .enqueue(_Callback);
    }
}