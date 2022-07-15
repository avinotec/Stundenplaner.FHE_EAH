/*
 *  Copyright (c) 2014-2019 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
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

import java.util.ArrayList;
import java.util.Map;

import de.fhe.fhemobile.vos.myschedule.ModuleVo;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventSetVo;
import de.fhe.fhemobile.vos.timetablechanges.TimetableChangesResponse;
import de.fhe.fhemobile.vos.CafeAquaResponse;
import de.fhe.fhemobile.vos.WeatherResponse;
import de.fhe.fhemobile.vos.canteen.CanteenDishVo;
import de.fhe.fhemobile.vos.canteen.CanteenVo;
import de.fhe.fhemobile.vos.news.NewsCategoryResponse;
import de.fhe.fhemobile.vos.news.NewsItemResponse;
import de.fhe.fhemobile.vos.phonebook.EmployeeVo;
import de.fhe.fhemobile.vos.semesterdata.SemesterDataVo;
import de.fhe.fhemobile.vos.timetable.TimeTableDialogResponse;
import de.fhe.fhemobile.vos.timetable.TimeTableWeekVo;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * Created by paul on 03.03.15.
 */
public interface ApiDeclaration {

    @GET(Endpoints.PHONEBOOK)
    Call<ArrayList<EmployeeVo>> fetchEmployees(@Query(Endpoints.PARAM_FNAME) String _FirstName, @Query(Endpoints.PARAM_LNAME) String _LastName);
    
    @GET(Endpoints.SEMESTER)
    Call<SemesterDataVo> fetchSemesterData();
    
    @GET(Endpoints.CANTEEN + "/{canteenId}")
    Call<CanteenDishVo[]> fetchCanteenData(@Path("canteenId") String _CanteenId);
    
    @GET(Endpoints.CANTEEN)
    Call<CanteenVo[]> fetchAvailableCanteens();

    @GET(Endpoints.RSS + "/{newsListId}")
    Call<NewsItemResponse> fetchNewsData(@Path("newsListId") String _NewsListId);

    @GET(Endpoints.RSS)
    Call<NewsCategoryResponse> fetchAvailableNewsLists();
    
    @GET(Endpoints.WEATHER)
    Call<WeatherResponse> fetchWeather();
    
    @GET(Endpoints.AQUA)
    Call<CafeAquaResponse> fetchCafeAquaStatus();
    
    @GET(Endpoints.TIMETABLE)
    Call<TimeTableDialogResponse> fetchStudyProgramData();

    @GET(Endpoints.TIMETABLE_EVENTS + "{" + Endpoints.PARAM_STUDYGROUP_ID + "}/detail")
    Call<Map<String,TimeTableWeekVo>> fetchTimeTableEvents(@Path(Endpoints.PARAM_STUDYGROUP_ID) String _StudyGroupId);

    @GET(Endpoints.MY_SCHEDULE + "{" + Endpoints.PARAM_SEMESTER_ID + "}")
    Call<Map<String, MyScheduleEventSetVo>> fetchSemesterTimeTable(@Path(Endpoints.PARAM_SEMESTER_ID) String _SemesterId);

    @GET(Endpoints.MODULE + "{" + Endpoints.PARAM_MODULE_ID + "}")
    Call<ModuleVo> fetchModule(@Path(Endpoints.PARAM_MODULE_ID) String _ModuleId);

    @Headers({
            "Content-Type:application/json"
    })
    @POST(Endpoints.SCHEDULE_CHANGE_SERVER)
    Call<TimetableChangesResponse> registerTimeTableChanges(@Body RequestBody _json);

    @HTTP(method="GET", path = Endpoints.SCHEDULE_CHANGE_SERVER, hasBody = true)
    Call<TimetableChangesResponse> getTimeTableChanges(@Body RequestBody _json);
}
