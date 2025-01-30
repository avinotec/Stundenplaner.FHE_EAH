package de.fhe.fhemobile.api;

import org.openapitools.client.ApiClient;
import org.openapitools.client.api.SemesterApi;
import org.openapitools.client.model.SemesterResponse;
import org.openapitools.client.model.Semester;
import org.openapitools.client.model.SemesterVorlesungszeit;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MosesSemesterApi {
    private final SemesterApi semesterApi;
    private CurrentSemesterCallback currentSemesterCallback;

    public interface CurrentSemesterCallback {
        void onCurrentSemesterReceived(Semester semester);

        void onError(String errorMessage);
    }

    public MosesSemesterApi(ApiClient apiClient) {
        semesterApi = apiClient.createService(SemesterApi.class);
    }

    public void getCurrentSemester(CurrentSemesterCallback callback) {
        this.currentSemesterCallback = callback;
        semesterGetAll(semesterResponseCallback);
    }

    private void semesterGetAll(Callback<SemesterResponse> _Callback) {
        semesterApi.semesterGetAll().enqueue(_Callback);
    }

    private final Callback<SemesterResponse> semesterResponseCallback = new Callback<SemesterResponse>() {
        @Override
        public void onResponse(Call<SemesterResponse> call, Response<SemesterResponse> response) {
            if (response.isSuccessful() && response.body() != null) {
                SemesterResponse semesterResponse = response.body();
                try {
                    Semester currentSemester = findCurrentSemester(semesterResponse);
                    if (currentSemester != null) {
                        currentSemesterCallback.onCurrentSemesterReceived(currentSemester);
                    } else {
                        currentSemesterCallback.onError("Kein aktuelles Semester gefunden");
                    }
                } catch (ParseException e) {
                    currentSemesterCallback.onError("Fehler beim Parsen der Daten: " + e.getMessage());
                }
            } else {
                currentSemesterCallback.onError("Fehler beim Laden der Semester: " + response.message());
            }
        }

        @Override
        public void onFailure(Call<SemesterResponse> call, Throwable throwable) {
            currentSemesterCallback.onError("Netzwerkfehler: " + throwable.getMessage());
        }
    };

    public Semester findCurrentSemester(SemesterResponse response) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);
        Date today = new Date();

        for (Semester semester : response.getData()) {
            SemesterVorlesungszeit vorlesungszeit = semester.getVorlesungszeit();
            if (vorlesungszeit == null) {
                continue;
            }

            Object startDateObj = vorlesungszeit.getStartDate();
            Object endDateObj = vorlesungszeit.getEndDate();

            // Konvertiere die Objekte zu Strings
            String startDateStr = startDateObj.toString();
            String endDateStr = endDateObj.toString();

            Date vorlesungStart = dateFormat.parse(startDateStr);
            Date vorlesungEnd = dateFormat.parse(endDateStr);

            if (!today.before(vorlesungStart) && !today.after(vorlesungEnd)) {
                return semester;
            }
        }
        return null;
    }
}