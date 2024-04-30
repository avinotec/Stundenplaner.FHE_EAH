package de.fhe.fhemobile.api;

import static de.fhe.fhemobile.Main.getAppContext;

import org.openapitools.client.ApiClient;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import de.fhe.fhemobile.BuildConfig;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;

/**
 * Creates a Moses ApiClient which serves as the basis for the individual services of the endpoints.
 * An interceptor is used to add the "Authorization" and its API-Key for every request.
 * This API-Key has to bet set in the top level local.properties file as a key-value pair:
 * MOSES_API_KEY="API_KEY_VALUE"
 * Usage example:
 * final MosesApi mosesApi = new MosesApi();
 * calVeranstaltungsApi = new CalVeranstaltungsApi(mosesApi.getApiClient());
 */
public class MosesApi {
    private final ApiClient apiClient;

    public MosesApi() {
        long httpCacheMaxSize = 50L * 1024L * 1024L;
        File httpCacheFile = new File(getAppContext().getCacheDir(), "http_cache");
        String apiKey = BuildConfig.MOSES_API_KEY;

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .cache(new Cache(httpCacheFile, httpCacheMaxSize))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("Authorization", apiKey)
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                })
                .build();

        apiClient = new ApiClient(httpClient);
    }

    public ApiClient getApiClient() {
        return apiClient;
    }
}
