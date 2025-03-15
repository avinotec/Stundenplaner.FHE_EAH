package de.fhe.fhemobile.api;

import android.util.Log;

import org.openapitools.client.ApiClient;
import org.openapitools.client.api.SemesterApi;
import org.openapitools.client.model.Semester;
import org.openapitools.client.model.SemesterResponse;

import java.time.LocalDate;
import java.util.List;

import de.fhe.fhemobile.BuildConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MosesSemesterApi {

    private static final String TAG = MosesSemesterApi.class.getSimpleName();

    private final SemesterApi semesterApi;

    public interface CurrentSemesterCallback {
        void onCurrentSemesterReceived(Semester semester);
        void onError(String errorMessage);
    }

    public MosesSemesterApi(ApiClient apiClient) {
        this.semesterApi = apiClient.createService(SemesterApi.class);
    }

    public void getCurrentSemester(CurrentSemesterCallback callback) {
        semesterApi.semesterGetAll().enqueue(new Callback<SemesterResponse>() {
            @Override
            public void onResponse(Call<SemesterResponse> call, Response<SemesterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Semester currentSemester = findCurrentSemester(response.body().getData());
                    if (currentSemester != null) {
                        callback.onCurrentSemesterReceived(currentSemester);
                    } else {
                        callback.onError("Kein aktuelles Semester gefunden.");
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "Kein aktuelles Semester gefunden.");
                        }
                    }
                } else {
                    String errorMessage = "Fehler beim Laden der Semester: " + response.message();
                    callback.onError(errorMessage);
                    Log.e(TAG, errorMessage);
                }
            }

            @Override
            public void onFailure(Call<SemesterResponse> call, Throwable t) {
                String errorMessage = "Netzwerkfehler: " + t.getMessage();
                callback.onError(errorMessage);
                Log.e(TAG, errorMessage, t);
            }
        });
    }

    private Semester findCurrentSemester(List<Semester> semesters) {
        LocalDate today = LocalDate.now();
        for (Semester semester : semesters) {
            if (!today.isBefore(semester.getStartDate()) && !today.isAfter(semester.getEndDate())) {
                return semester;
            }
        }
        return null;
    }
}