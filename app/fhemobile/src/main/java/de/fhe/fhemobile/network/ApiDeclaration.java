package de.fhe.fhemobile.network;

import java.util.ArrayList;

import de.fhe.fhemobile.models.TimeTableChanges.ResponseModel;
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
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * Created by paul on 03.03.15.
 */
public interface ApiDeclaration {
    
    @GET(Endpoints.PHONEBOOK)
    public Call<ArrayList<EmployeeVo>> fetchEmployees(@Query(Endpoints.PARAM_FNAME) String _FirstName, @Query(Endpoints.PARAM_LNAME) String _LastName);
    
    @GET(Endpoints.SEMESTER)
    public Call<SemesterDataVo> fetchSemesterData();
    
    @GET(Endpoints.MENSA + "/{mensaId}")
    public Call<MensaFoodItemVo[]> fetchMensaData(@Path("mensaId") String _MensaId);
    
    @GET(Endpoints.MENSA)
    public Call<MensaChoiceItemVo[]> fetchAvailableMensas();

    @GET(Endpoints.RSS + "/{newsListId}")
    public Call<NewsItemResponse> fetchNewsData(@Path("newsListId") String _NewsListId);

    @GET(Endpoints.RSS)
    public Call<NewsCategoryResponse> fetchAvailableNewsLists();
    
    @GET(Endpoints.WEATHER)
    public Call<WeatherResponse> fetchWeather();
    
    @GET( Endpoints.AQUA)
    public Call<CafeAquaResponse> fetchCafeAquaStatus();
    
    @GET( Endpoints.TIMETABLE)
    public Call<TimeTableResponse> fetchTimeTable();

    @GET(Endpoints.TIMETABLE_EVENTS)
    public Call<ArrayList<TimeTableWeekVo>> fetchTimeTableEvents(@Query(Endpoints.PARAM_TIMETABLE_ID) String _TimeTableId);

    @Headers({
            "Content-Type:application/json"
    })
    @POST("https://lustigtestt.de/fhjena/rest_api/public/changes")
    public Call<ResponseModel> registerTimeTableChanges(@Body RequestBody _json);
}
