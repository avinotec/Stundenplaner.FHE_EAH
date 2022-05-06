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

import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.models.canteen.CanteenModel;
import de.fhe.fhemobile.models.news.NewsModel;
import de.fhe.fhemobile.models.phonebook.PhonebookModel;
import de.fhe.fhemobile.models.semesterdata.SemesterDataModel;
import de.fhe.fhemobile.models.timeTableChanges.ResponseModel;
import de.fhe.fhemobile.utils.canteen.CanteenUtils;
import de.fhe.fhemobile.utils.UserSettings;
import de.fhe.fhemobile.vos.CafeAquaResponse;
import de.fhe.fhemobile.vos.WeatherResponse;
import de.fhe.fhemobile.vos.canteen.CanteenDishVo;
import de.fhe.fhemobile.vos.canteen.CanteenMenuDayVo;
import de.fhe.fhemobile.vos.canteen.CanteenVo;
import de.fhe.fhemobile.vos.mytimetable.MyTimeTableCourseComponent;
import de.fhe.fhemobile.vos.news.NewsCategoryResponse;
import de.fhe.fhemobile.vos.news.NewsItemResponse;
import de.fhe.fhemobile.vos.news.NewsItemVo;
import de.fhe.fhemobile.vos.phonebook.EmployeeVo;
import de.fhe.fhemobile.vos.semesterdata.SemesterDataVo;
import de.fhe.fhemobile.vos.timetable.TimeTableDialogResponse;
import de.fhe.fhemobile.vos.timetable.TimeTableWeekVo;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by paul on 22.01.14
 */
//RetroFitClient
public class NetworkHandler {

	private static final String TAG = "NetworkHandler";

	private static final NetworkHandler ourInstance = new NetworkHandler();

	private Retrofit mRestAdapter = null;
	private Retrofit mRestAdapterEah = null;
	private ApiDeclaration mApi = null;
	private ApiDeclaration mApiEah = null;

	/**
	 * Private constructor
	 */
	private NetworkHandler() {
		Assert.assertTrue( mApi == null );
		Assert.assertTrue( mRestAdapter == null );
		Assert.assertTrue( mApiEah == null );
		Assert.assertTrue( mRestAdapterEah == null );

		final Gson gson = new GsonBuilder()
				.setDateFormat("HH:mm:ss'T'yyyy-MM-dd")
				.create();

		mRestAdapter = new Retrofit.Builder()
				.baseUrl(Endpoints.BASE_URL + Endpoints.APP_NAME)
				.addConverterFactory(GsonConverterFactory.create(gson))
				//.setConverter(new GsonConverter(gson))
//                .setLogLevel(RestAdapter.LogLevel.FULL)
				.build();
		mRestAdapterEah = new Retrofit.Builder()
				.baseUrl(Endpoints.BASE_URL_EAH)
				.addConverterFactory(GsonConverterFactory.create(gson))
				//.setConverter(new GsonConverter(gson))
//                .setLogLevel(RestAdapter.LogLevel.FULL)
				.build();

		mApi = mRestAdapter.create(ApiDeclaration.class);
		mApiEah = mRestAdapterEah.create(ApiDeclaration.class);
		Assert.assertTrue( mApi != null );
		Assert.assertTrue( mRestAdapter != null );
		Assert.assertTrue( mApiEah != null );
		Assert.assertTrue( mRestAdapterEah != null );

	}

	/**
	 *
	 * @return
	 */
	public static NetworkHandler getInstance() {
		Assert.assertTrue( ourInstance != null);
		return ourInstance;
	}

	/**
	 * *
	 * @param _FirstName
	 * @param _LastName
	 */
	public void fetchEmployees(final String _FirstName, final String _LastName) {

		Assert.assertTrue( mApi != null );
		mApi.fetchEmployees(_FirstName, _LastName).enqueue(new Callback<ArrayList<EmployeeVo>>() {

			/**
			 *
			 * @param call
			 * @param response
			 */
			@Override
			public void onResponse(final Call<ArrayList<EmployeeVo>> call, final Response<ArrayList<EmployeeVo>> response) {
				if ( response.body() != null ) {
					PhonebookModel.getInstance().setFoundEmployees(response.body());
				}
			}

			/**
			 *
			 * @param call
			 * @param t
			 */
			@Override
			public void onFailure(final Call<ArrayList<EmployeeVo>> call, final Throwable t) {
				showErrorToast();
				Log.d(TAG, "failure: request " + call.request().url() + " - "+ t.getMessage());
			}

		});
	}

	/**
	 * *
	 */
	public void fetchSemesterData() {
		Assert.assertTrue( mApi != null );

		mApi.fetchSemesterData().enqueue( new Callback<SemesterDataVo>() {

			/**
			 *
			 * @param call
			 * @param response
			 */
			@Override
			public void onResponse( final Call<SemesterDataVo> call, final Response<SemesterDataVo> response) {
				if ( response.body() != null ) {
					SemesterDataModel.getInstance().setData(response.body().getSemester());
				}
			}

			/**
			 *
			 * @param call
			 * @param t
			 */
			@Override
			public void onFailure(final Call<SemesterDataVo> call, final Throwable t) {
				showErrorToast();
				Log.d(TAG, "failure: request " + call.request().url() + " - "+ t.getMessage());
			}
		});
	}

	/**
	 * Fetch menu of the canteens specified in {@link UserSettings}
	 */
	public void fetchCanteenMenus() {
		Assert.assertTrue( mApi != null );

		ArrayList<String> selectedCanteenIds = UserSettings.getInstance().getSelectedCanteenIds();
		Log.d(TAG, "Selected Canteens: " + selectedCanteenIds);

		for(String canteenId : selectedCanteenIds){
			mApi.fetchCanteenData(canteenId).enqueue(new Callback<CanteenDishVo[]>() {

				/**
				 *
				 * @param call
				 * @param response
				 */
				@Override
				public void onResponse(final Call<CanteenDishVo[]> call, final Response<CanteenDishVo[]> response) {
					Log.d(TAG, "Canteen: " + call.request().url());
					final CanteenDishVo[] dishes = response.body();
					List<CanteenMenuDayVo> sortedDishes = null;

					if (dishes != null && dishes.length > 0)
						sortedDishes = CanteenUtils.sortCanteenItems(dishes);

					if (sortedDishes != null) {
						CanteenModel.getInstance().addMenu(canteenId, sortedDishes);
					} else {
						CanteenModel.getInstance().addMenu(canteenId, new ArrayList<>());
					}
				}

				/**
				 *
				 * @param call
				 * @param t
				 */
				@Override
				public void onFailure(final Call<CanteenDishVo[]> call, final Throwable t) {
					showErrorToast();
				}
			});
		}
	}

	/**
	 * *
	 */
	public void fetchNewsData() {

		fetchNewsData(UserSettings.getInstance().getChosenNewsCategory(), new Callback<NewsItemResponse>() {

			/**
			 *
			 * @param call
			 * @param response
			 */
			@Override
			public void onResponse(final Call<NewsItemResponse> call, final Response<NewsItemResponse> response) {
				// MS: Bei den News sind die news/0 kaputt
				if ( response.code() == 200 ) {
					if (response.body() != null) {
						final NewsItemVo[] newsItemVos = response.body().getChannel().getNewsItems();
						NewsModel.getInstance().setNewsItems(newsItemVos);
					}
				}
				else /* if ( response.code() == 204 ) */
				{
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
			public void onFailure(final Call<NewsItemResponse> call, final Throwable t) {
				showErrorToast();
				Log.d(TAG, "failure: request " + call.request().url() + " - "+ t.getMessage());
			}
		});
	}

	/**
	 * *
	 */
	public void fetchNewsData(final String _NewsCategory, final Callback<NewsItemResponse> _Callback) {
		Assert.assertTrue( mApi != null );

		mApi.fetchNewsData(_NewsCategory ).enqueue(_Callback);
	}

	/**
	 * *
	 */
	public void fetchAvailableCanteens() {
		Assert.assertTrue( mApi != null );

		mApi.fetchAvailableCanteens().enqueue(new Callback<CanteenVo[]>() {

			@Override
			public void onResponse(final Call<CanteenVo[]> call, final Response<CanteenVo[]> response) {
				if ( response.body() != null ) {
					CanteenModel.getInstance().setCanteens(response.body());
				}
			}

			@Override
			public void onFailure(final Call<CanteenVo[]> call, final Throwable t) {
				showErrorToast();
				Log.d(TAG, "failure: request " + call.request().url() + " - "+ t.getMessage());
			}
		});
	}

	/**
	 * *
	 */
	public void fetchAvailableNewsLists() {

		fetchAvailableNewsLists(new Callback<NewsCategoryResponse>() {

			/**
			 *
			 * @param call
			 * @param response
			 */
			@Override
			public void onResponse(final Call<NewsCategoryResponse> call, final Response<NewsCategoryResponse> response) {
				// MS: Bei den News sind die news/0 kaputt
				if ( response.body() != null ) {
					NewsModel.getInstance().setCategoryItems(response.body().getNewsCategories());
				}
			}

			/**
			 *
			 * @param call
			 * @param t
			 */
			@Override
			public void onFailure(final Call<NewsCategoryResponse> call, final Throwable t) {
				showErrorToast();
				Log.d(TAG, "failure: request " + call.request().url() + " - "+ t.getMessage());
			}
		});
	}

	/**
	 * *
	 */
	public void fetchAvailableNewsLists(final Callback<NewsCategoryResponse> _Callback) {
		Assert.assertTrue( mApi != null );

		mApi.fetchAvailableNewsLists().enqueue(_Callback);
	}

	/**
	 * *
	 */
	public void fetchWeather(final Callback<WeatherResponse> _Callback) {
		Assert.assertTrue( mApi != null );

		mApi.fetchWeather().enqueue(_Callback);
	}

	/**
	 *
	 * @param _Callback
	 */
	public void fetchTimeTable(final Callback<TimeTableDialogResponse> _Callback) {
		Assert.assertTrue( mApiEah != null );

		mApiEah.fetchTimeTable().enqueue(_Callback);
	}

	/**
	 *
	 * @param _StudyGroupId
	 * @param _Callback
	 */
	public void fetchTimeTableEvents(final String _StudyGroupId,
									 final Callback<Map<String, TimeTableWeekVo>> _Callback) {
		Assert.assertTrue( mApiEah != null );

		mApiEah.fetchTimeTableEvents(_StudyGroupId).enqueue(_Callback);
	}

	public List<MyTimeTableCourseComponent> reloadEvents(final MyTimeTableCourseComponent event) {
		Assert.assertTrue( mApi != null );
		Assert.assertTrue( event != null );


		final List<MyTimeTableCourseComponent> eventList = new ArrayList<>();

		//todo: auskommentiert im Zuge von Umbauarbeiten
		/*try {

			final ArrayList<TimeTableWeekVo> timeTable = mApi.fetchTimeTableEvents(
					event.getStudyGroup().getTimeTableId()).execute().body();
			Assert.assertTrue( timeTable != null );

			for (final TimeTableWeekVo weekEntry : timeTable){
				for(final TimeTableDayVo dayEntry : weekEntry.getDays() ){
					for(final TimeTableEventVo eventEntry : dayEntry.getEvents() ){
						if(eventEntry.getTitle().equals(event.getEvent().getTitle())){
							final MyTimeTableCourseComponent item = event.copy();
							item.setEventWeek(weekEntry);
							item.setEventDay(dayEntry);
							item.setEvent(eventEntry);
							eventList.add(item);
						}
					}
				}
			}
		} catch (final IOException e) {
			Log.e(TAG, "fetchTimeTableEventsSynchron: ",e );
		}*/
		return eventList;
	}

	/**
	 *
	 * @param json
	 * @param _Callback
	 */
	public void registerTimeTableChanges(final String json, final Callback<ResponseModel> _Callback){
		Assert.assertTrue( mApi != null );
		//okhttp 4.x final RequestBody body =RequestBody.create( json, MediaType.parse("application/json"));
		//okhttp 3.12 flip parameters
		final RequestBody body =RequestBody.create(MediaType.parse("application/json"), json);
		mApi.registerTimeTableChanges(body).enqueue(_Callback);
	}

	/**
	 *
	 * @param json
	 * @param _Callback
	 */
	public void getTimeTableChanges(final String json, final Callback<ResponseModel>_Callback){
		Assert.assertTrue( mApi != null );
		//okhttp 4.x final RequestBody body =RequestBody.create( json, MediaType.parse("application/json"));
		//okhttp 3.12 flip parameters
		final RequestBody body =RequestBody.create(MediaType.parse("application/json"), json);
		mApi.getTimeTableChanges(body).enqueue(_Callback);
	}

	/**
	 *
	 * @param _Callback
	 */
	public void fetchCafeAquaStatus(final Callback<CafeAquaResponse> _Callback) {
		Assert.assertTrue( mApi != null );
		mApi.fetchCafeAquaStatus().enqueue(_Callback);
	}

	/**
	 *
	 */
	private void showErrorToast() {
		Toast.makeText(Main.getAppContext(), "Cannot establish connection!",
				Toast.LENGTH_LONG).show();
	}



}
