/*
 *  Copyright (c) 2014-2022 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package de.fhe.fhemobile.network;

import static de.fhe.fhemobile.activities.MainActivity.setSubscribedEventSeriesAndUpdateAdapters;
import static de.fhe.fhemobile.utils.myschedule.MyScheduleUtils.getUpdatedEventSeries;
import static de.fhe.fhemobile.utils.myschedule.MyScheduleUtils.groupByModuleId;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.models.canteen.CanteenModel;
import de.fhe.fhemobile.models.news.NewsModel;
import de.fhe.fhemobile.models.phonebook.PhonebookModel;
import de.fhe.fhemobile.models.semesterdates.SemesterDatesModel;
import de.fhe.fhemobile.utils.ApiErrorUtils;
import de.fhe.fhemobile.utils.UserSettings;
import de.fhe.fhemobile.utils.Utils;
import de.fhe.fhemobile.utils.canteen.CanteenUtils;
import de.fhe.fhemobile.vos.ApiErrorResponse;
import de.fhe.fhemobile.vos.WeatherResponse;
import de.fhe.fhemobile.vos.canteen.CanteenDishVo;
import de.fhe.fhemobile.vos.canteen.CanteenMenuDayVo;
import de.fhe.fhemobile.vos.canteen.CanteenVo;
import de.fhe.fhemobile.vos.myschedule.ModuleVo;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventSeriesVo;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventSetVo;
import de.fhe.fhemobile.vos.news.NewsCategoryResponse;
import de.fhe.fhemobile.vos.news.NewsItemResponse;
import de.fhe.fhemobile.vos.news.NewsItemVo;
import de.fhe.fhemobile.vos.phonebook.EmployeeVo;
import de.fhe.fhemobile.vos.semesterdates.SemesterDatesVo;
import de.fhe.fhemobile.vos.timetable.TimetableDialogResponse;
import de.fhe.fhemobile.vos.timetable.TimetableWeekVo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by paul on 22.01.14
 */
//RetroFitClient
public final class NetworkHandler {

    static final String TAG = NetworkHandler.class.getSimpleName();

    private static final NetworkHandler ourInstance = new NetworkHandler();

    private ApiDeclaration mApiErfurt = null;
    private ApiDeclaration mApiEah = null;

    /**
     * Private constructor
     */
    private NetworkHandler() {
        Assert.assertTrue(mApiErfurt == null);
        Assert.assertTrue(mApiEah == null);

        final Gson gson = new GsonBuilder()
                .setDateFormat("HH:mm:ss'T'yyyy-MM-dd")
                .create();

        final Retrofit mRestAdapter = new Retrofit.Builder()
                .baseUrl(Endpoints.BASE_URL + Endpoints.APP_NAME)
                .addConverterFactory(GsonConverterFactory.create(gson))
                //.setConverter(new GsonConverter(gson))
//                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        final Retrofit mRestAdapterEah = new Retrofit.Builder()
                .baseUrl(Endpoints.BASE_URL_EAH)
                .addConverterFactory(GsonConverterFactory.create(gson))
                //.setConverter(new GsonConverter(gson))
//                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        mApiErfurt = mRestAdapter.create(ApiDeclaration.class);
        mApiEah = mRestAdapterEah.create(ApiDeclaration.class);
        Assert.assertTrue(mApiErfurt != null);
        Assert.assertTrue(mRestAdapter != null);
        Assert.assertTrue(mApiEah != null);
        Assert.assertTrue(mRestAdapterEah != null);

    }

    /**
     * Singelton
     * @return the Singleton
     */
    public static NetworkHandler getInstance() {
        Assert.assertTrue(ourInstance != null);
        return ourInstance;
    }

    // -------------------------------- PHONEBOOK SEARCH -------------------------------------------

    /**
     * *
     *
     * @param _FirstName
     * @param _LastName
     */
    public void fetchEmployees(final String _FirstName, final String _LastName) {
        Assert.assertTrue(mApiErfurt != null);

        mApiErfurt.fetchEmployees(_FirstName, _LastName).enqueue(new Callback<ArrayList<EmployeeVo>>() {
            /**
             *
             * @param call
             * @param response
             */
            @Override
            public void onResponse(@NonNull final Call<ArrayList<EmployeeVo>> call, @NonNull final Response<ArrayList<EmployeeVo>> response) {
                if (response.isSuccessful()) {
                    PhonebookModel.getInstance().setFoundEmployees(response.body());
                } else {
                    final ApiErrorResponse error = ApiErrorUtils.getApiErrorResponse(response);
                    ApiErrorUtils.showErrorToast(error, ApiErrorUtils.ApiErrorCode.NETWORK_HANDLER_CODE1);
                }
            }

            /**
             *
             * @param call
             * @param t
             */
            @Override
            public void onFailure(@NonNull final Call<ArrayList<EmployeeVo>> call, @NonNull final Throwable t) {
                ApiErrorUtils.showConnectionErrorToast(ApiErrorUtils.ApiErrorCode.NETWORK_HANDLER_CODE7);
                Log.d(TAG, "failure: request " + call.request().url() + " - " + t.getMessage());
            }
        });
    }

    // ---------------------------------- SEMESTER DATES -------------------------------------------

    /**
     * *
     */
    public void fetchSemesterDates() {
        Assert.assertTrue(mApiErfurt != null);

        mApiErfurt.fetchSemesterDates().enqueue(new Callback<SemesterDatesVo>() {

            /**
             *
             * @param call
             * @param response
             */
            @Override
            public void onResponse(@NonNull final Call<SemesterDatesVo> call, @NonNull final Response<SemesterDatesVo> response) {
                if (response.isSuccessful()) {
                    SemesterDatesModel.getInstance().setData(response.body().getSemester());
                } else {
                    final ApiErrorResponse error = ApiErrorUtils.getApiErrorResponse(response);
                    ApiErrorUtils.showErrorToast(error, ApiErrorUtils.ApiErrorCode.NETWORK_HANDLER_CODE2);
                }
            }

            /**
             *
             * @param call
             * @param t
             */
            @Override
            public void onFailure(@NonNull final Call<SemesterDatesVo> call, @NonNull final Throwable t) {
                ApiErrorUtils.showConnectionErrorToast(ApiErrorUtils.ApiErrorCode.NETWORK_HANDLER_CODE8);
                Log.d(TAG, "failure: request " + call.request().url() + " - " + t.getMessage());
            }
        });
    }


    // ---------------------------------- NEWS -----------------------------------------------------
    /**
     *
     * @param _NewsCategory
     * @param _Callback
     */
    public void fetchNewsData(final String _NewsCategory, final Callback<NewsItemResponse> _Callback) {
        Assert.assertTrue(mApiErfurt != null);

        mApiErfurt.fetchNewsData(_NewsCategory).enqueue(_Callback);
    }

    /**
     *
     */
    public void fetchNewsData() {
        fetchNewsData(UserSettings.getInstance().getChosenNewsCategory(),
                new Callback<NewsItemResponse>() {
            /**
             *
             * @param call
             * @param response
             */
            @Override
            public void onResponse(@NonNull final Call<NewsItemResponse> call, @NonNull final Response<NewsItemResponse> response) {
                // MS: Bei den News sind die news/0 kaputt
                if (response.code() == 200) {
                    if (response.body() != null) {
                        final NewsItemVo[] newsItemVos = response.body().getChannel().getNewsItems();
                        NewsModel.getInstance().setNewsItems(newsItemVos);
                    }
                } else /* if ( response.code() == 204 ) */ {
                    // no content, bspw. wegen einer Weiterleitung
                    // TODO: Leere Meldung erzeugen
                    final NewsItemVo aNoNewsItemErrorObj = new NewsItemVo();
                    aNoNewsItemErrorObj.setTitle("System Error");
                    aNoNewsItemErrorObj.setLink("");
                    aNoNewsItemErrorObj.setDescription("An internal error in this news system showed up. Please report.");
                    aNoNewsItemErrorObj.setAuthor("The News System");

                    final NewsItemVo[] mNewsItems = new NewsItemVo[1];
                    mNewsItems[0] = aNoNewsItemErrorObj;
                    final NewsItemVo[] newsItemVos = mNewsItems;
                    NewsModel.getInstance().setNewsItems(newsItemVos);

                }
            }

            /**
             *
             * @param call
             * @param t
             */
            @Override
            public void onFailure(@NonNull final Call<NewsItemResponse> call, @NonNull final Throwable t) {
                ApiErrorUtils.showConnectionErrorToast(ApiErrorUtils.ApiErrorCode.NETWORK_HANDLER_CODE10);
                Log.d(TAG, "failure: request " + call.request().url() + " - " + t.getMessage());
            }
        });
    }

    /**
     *
     */
    public void fetchAvailableNewsLists() {
        Assert.assertTrue(mApiErfurt != null);

        mApiErfurt.fetchAvailableNewsLists().enqueue(new Callback<NewsCategoryResponse>() {

            /**
             *
             * @param call
             * @param response
             */
            @Override
            public void onResponse(@NonNull final Call<NewsCategoryResponse> call, @NonNull final Response<NewsCategoryResponse> response) {
                // MS: Bei den News sind die news/0 kaputt
                if (response.isSuccessful()) {
                    NewsModel.getInstance().setCategoryItems(response.body().getNewsCategories());
                } else {
                    final ApiErrorResponse error = ApiErrorUtils.getApiErrorResponse(response);
                    ApiErrorUtils.showErrorToast(error, ApiErrorUtils.ApiErrorCode.NETWORK_HANDLER_CODE4);
                }
            }

            /**
             *
             * @param call
             * @param t
             */
            @Override
            public void onFailure(@NonNull final Call<NewsCategoryResponse> call, @NonNull final Throwable t) {
                ApiErrorUtils.showConnectionErrorToast(ApiErrorUtils.ApiErrorCode.NETWORK_HANDLER_CODE12);
                Log.d(TAG, "failure: request " + call.request().url() + " - " + t.getMessage());
            }
        });
    }


    // ---------------------------------- CANTEEN -----------------------------------------------------
    /**
     *
     */
    public void fetchAvailableCanteens() {
        Assert.assertTrue(mApiErfurt != null);

        mApiErfurt.fetchAvailableCanteens().enqueue(new Callback<CanteenVo[]>() {

            @Override
            public void onResponse(@NonNull final Call<CanteenVo[]> call, @NonNull final Response<CanteenVo[]> response) {
                if (response.isSuccessful()) {
                    CanteenModel.getInstance().setCanteens(response.body());
                } else {
                    final ApiErrorResponse error = ApiErrorUtils.getApiErrorResponse(response);
                    ApiErrorUtils.showErrorToast(error, ApiErrorUtils.ApiErrorCode.NETWORK_HANDLER_CODE3);
                }
            }

            @Override
            public void onFailure(@NonNull final Call<CanteenVo[]> call, @NonNull final Throwable t) {
                ApiErrorUtils.showConnectionErrorToast(ApiErrorUtils.ApiErrorCode.NETWORK_HANDLER_CODE11);
                Log.d(TAG, "failure: request " + call.request().url() + " - " + t.getMessage());
            }
        });
    }

    /**
     * Fetch menu of the canteens specified in {@link UserSettings}
     */
    public void fetchCanteenMenus() {
        Assert.assertTrue(mApiErfurt != null);

        final ArrayList<String> selectedCanteenIds = UserSettings.getInstance().getSelectedCanteenIds();
        Log.d(TAG, "Selected Canteens: " + selectedCanteenIds);

        for (final String canteenId : selectedCanteenIds) {
            mApiErfurt.fetchCanteenData(canteenId).enqueue(new Callback<CanteenDishVo[]>() {

                @Override
                public void onResponse(@NonNull final Call<CanteenDishVo[]> call, @NonNull final Response<CanteenDishVo[]> response) {
                    Log.d(TAG, "Canteen: " + call.request().url());
                    final CanteenDishVo[] dishes = response.body();
                    List<CanteenMenuDayVo> sortedDishes = null;

                    if (dishes != null && dishes.length > 0)
                        sortedDishes = CanteenUtils.sortCanteenItems(dishes);

                    if (sortedDishes != null) {
                        CanteenModel.getInstance().setMenu(canteenId, sortedDishes);
                    } else {
                        //do nothing
                    }
                }

                @Override
                public void onFailure(@NonNull final Call<CanteenDishVo[]> call, @NonNull final Throwable t) {
                    ApiErrorUtils.showConnectionErrorToast(ApiErrorUtils.ApiErrorCode.NETWORK_HANDLER_CODE9);
                }
            });
        }
    }

    // ---------------------------------- WEATHER -----------------------------------------------------

    /**
     *
     * @param _Callback
     */
    public void fetchWeather(final Callback<WeatherResponse> _Callback) {
        Assert.assertTrue(mApiErfurt != null);

        mApiErfurt.fetchWeather().enqueue(_Callback);
    }

    // ---------------------------------- TIMETABLE -----------------------------------------------------
    /**
     * @param _Callback
     */
    public void fetchStudyPrograms(final Callback<TimetableDialogResponse> _Callback) {
        Assert.assertTrue(mApiEah != null);

        mApiEah.fetchStudyProgramData().enqueue(_Callback);
    }

    /**
     * @param _StudyGroupId
     * @param _Callback
     */
    public void fetchTimetableEvents(final String _StudyGroupId,
                                     final Callback<Map<String, TimetableWeekVo>> _Callback) {
        Assert.assertTrue(mApiEah != null);
        if(_StudyGroupId == null){
            Log.e(TAG, "StudyGroupId is null. Cannot fetch semester timetable.");
            return;
        }

        mApiEah.fetchTimetableEvents(_StudyGroupId).enqueue(_Callback);
    }

    // ---------------------------------- MY SCHEDULE ---------------------------------------------

    /**
     * Fetch timetable (a set of {@link MyScheduleEventSetVo}s) of a given semester
     *
     * @param _SemesterId The semester's ID
     * @param _Callback
     */
    public void fetchSemesterTimetable(final String _SemesterId,
                                       final Callback<Map<String, MyScheduleEventSetVo>> _Callback) {
        Assert.assertTrue(mApiEah != null);
        if(_SemesterId == null){
            Log.e(TAG, "SemesterId is null. Cannot fetch semester timetable.");
            return;
        }

        mApiEah.fetchSemesterTimetable(_SemesterId).enqueue(_Callback);
    }


    //wait until all updates are collected
    private volatile int requestCounterMySchedule;

    /**
     * Fetch all subscribed event series to detect changes and update
     */
    public void fetchMySchedule() {
        Assert.assertTrue(mApiEah != null);

        final Map<String, Map<String, MyScheduleEventSeriesVo>> modules =
                groupByModuleId(Main.getSubscribedEventSeries());

        /*
         *  From java 5 after a change in Java memory model reads and writes are atomic for all
         *  variables declared using the volatile keyword (including long and double variables) and
         *  simple atomic variable access is more efficient instead of accessing these variables
         *  via synchronized java code.
         *
         *  Wir feuern verschiedene Anfragen. Erst wenn alle Antworten zurückgekommen sind, dann
         *  verarbeiten wir diese.
         */
        requestCounterMySchedule = 0;
        /* Das ist unser Halter für die Antworten */
        final List<MyScheduleEventSeriesVo> updatedEventSeriesList = new ArrayList<>();

        //iterate over modules
        for (final Map.Entry<String, Map<String, MyScheduleEventSeriesVo>> module : modules.entrySet()) {
            //skip if module id is null -> caused by eventseries added before module IDs were introduced
            if (module.getKey() != null) {

                //update all subscribed event series of this module
                // we count the several requests
                requestCounterMySchedule++;
                mApiEah.fetchModule(module.getKey()).enqueue(new Callback<ModuleVo>() {

                    @Override
                    public void onResponse(@NonNull final Call<ModuleVo> call,
                                           @NonNull final Response<ModuleVo> response) {

                        if (response.isSuccessful()) {
                            updatedEventSeriesList.addAll(
									getUpdatedEventSeries(module.getValue(), response.body().getEventSets()));
                        } else {
                            final ApiErrorResponse error = ApiErrorUtils.getApiErrorResponse(response);
                            ApiErrorUtils.showErrorToast(error, ApiErrorUtils.ApiErrorCode.NETWORK_HANDLER_CODE5);
                            //add old event series' to prevent them from getting lost
                            updatedEventSeriesList.addAll(module.getValue().values());
                        }
                        // we received an answer, anyhow, so we decrease the outstanding requests counter
                        requestCounterMySchedule--;
                        if(requestCounterMySchedule <= 0){
                            // last request received, so proceed, finally
							setSubscribedEventSeriesAndUpdateAdapters(updatedEventSeriesList);
						}
                    }

                    @Override
                    public void onFailure(@NonNull final Call<ModuleVo> call, @NonNull final Throwable t) {
                        ApiErrorUtils.showConnectionErrorToast(ApiErrorUtils.ApiErrorCode.NETWORK_HANDLER_CODE6);
                        Utils.showToast(R.string.myschedule_connection_failed);
                        Log.d(TAG, "failure: request " + call.request().url() + " (internal: " + t.getCause() + ")");

                        //add old event series' to prevent them from getting lost
                        updatedEventSeriesList.addAll(module.getValue().values());
                        // we received an answer, anyhow, so we decrease the outstanding requests counter
                        requestCounterMySchedule--;
						if(requestCounterMySchedule <= 0){
                            // even when failed, this is the last request received, so proceed, finally
							setSubscribedEventSeriesAndUpdateAdapters(updatedEventSeriesList);
						}
                    }
                });
            }
            //module id is null thus updates cannot be fetched
            else {
                Utils.showToast(R.string.myschedule_connection_failed);
                //add old event series' with module id == null to prevent them from getting lost
                updatedEventSeriesList.addAll(module.getValue().values());
            }

        }
    }


}
