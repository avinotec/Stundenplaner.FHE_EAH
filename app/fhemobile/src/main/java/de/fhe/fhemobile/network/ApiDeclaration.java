package de.fhe.fhemobile.network;

import java.util.ArrayList;

import de.fhe.fhemobile.vos.CafeAquaResponse;
import de.fhe.fhemobile.vos.WeatherResponse;
import de.fhe.fhemobile.vos.mensa.MensaChoiceItemVo;
import de.fhe.fhemobile.vos.mensa.MensaFoodItemVo;
import de.fhe.fhemobile.vos.news.NewsCategoryResponse;
import de.fhe.fhemobile.vos.news.NewsItemResponse;
import de.fhe.fhemobile.vos.phonebook.EmployeeVo;
import de.fhe.fhemobile.vos.semesterdata.SemesterDataVo;
import de.fhe.fhemobile.vos.timetable.TimeTableResponse;
import de.fhe.fhemobile.vos.timetable.TimeTableWeekVo;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by paul on 03.03.15.
 */
public interface ApiDeclaration {
    
    @GET("/" + Endpoints.PHONEBOOK)
    public void fetchEmployees(@Query(Endpoints.PARAM_FNAME) String _FirstName, @Query(Endpoints.PARAM_LNAME) String _LastName, Callback<ArrayList<EmployeeVo>> _Callback);
    
    @GET("/" + Endpoints.SEMESTER)
    public void fetchSemesterData(Callback<SemesterDataVo> _Callback);
    
    @GET("/" + Endpoints.MENSA + "/{mensaId}")
    public void fetchMensaData(@Path("mensaId") String _MensaId, Callback<MensaFoodItemVo[]> _Callback);
    
    @GET("/" + Endpoints.MENSA)
    public void fetchAvailableMensas(Callback<MensaChoiceItemVo[]> _Callback);

    @GET("/" + Endpoints.RSS + "/{newsListId}")
    public void fetchNewsData(@Path("newsListId") String _NewsListId, Callback<NewsItemResponse> _Callback);

    @GET("/" + Endpoints.RSS)
    public void fetchAvailableNewsLists(Callback<NewsCategoryResponse> _Callback);
    
    @GET("/" + Endpoints.WEATHER)
    public void fetchWeather(Callback<WeatherResponse> _Callback);
    
    @GET("/" + Endpoints.AQUA)
    public void fetchCafeAquaStatus(Callback<CafeAquaResponse> _Callback);

    @GET("/" + Endpoints.TIMETABLE)
    public void fetchTimeTable(Callback<TimeTableResponse> _Callback);

    @GET("/" + Endpoints.TIMETABLE_EVENTS)
    public void fetchTimeTableEvents(@Query(Endpoints.PARAM_TIMETABLE_ID) String _TimeTableId, Callback<ArrayList<TimeTableWeekVo>> _Callback);
}
