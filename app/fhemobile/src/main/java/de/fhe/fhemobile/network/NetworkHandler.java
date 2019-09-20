package de.fhe.fhemobile.network;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.models.mensa.MensaFoodModel;
import de.fhe.fhemobile.models.news.NewsModel;
import de.fhe.fhemobile.models.phonebook.PhonebookModel;
import de.fhe.fhemobile.models.semesterdata.SemesterDataModel;
import de.fhe.fhemobile.utils.MensaUtils;
import de.fhe.fhemobile.utils.UserSettings;
import de.fhe.fhemobile.vos.CafeAquaResponse;
import de.fhe.fhemobile.vos.WeatherResponse;
import de.fhe.fhemobile.vos.mensa.MensaChoiceItemVo;
import de.fhe.fhemobile.vos.mensa.MensaFoodItemCollectionVo;
import de.fhe.fhemobile.vos.mensa.MensaFoodItemVo;
import de.fhe.fhemobile.vos.news.NewsCategoryResponse;
import de.fhe.fhemobile.vos.news.NewsItemResponse;
import de.fhe.fhemobile.vos.news.NewsItemVo;
import de.fhe.fhemobile.vos.phonebook.EmployeeVo;
import de.fhe.fhemobile.vos.semesterdata.SemesterDataVo;
import de.fhe.fhemobile.vos.timetable.FlatDataStructure;
import de.fhe.fhemobile.vos.timetable.TimeTableDayVo;
import de.fhe.fhemobile.vos.timetable.TimeTableEventVo;
import de.fhe.fhemobile.vos.timetable.TimeTableResponse;
import de.fhe.fhemobile.vos.timetable.TimeTableWeekVo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by paul on 22.01.14.
 */
public class NetworkHandler {
	private static final String TAG = "NetworkHandler";

	public static NetworkHandler getInstance() {
		return ourInstance;
	}

	/**
	 * *
	 * @param _FirstName
	 * @param _LastName
	 */
	public void fetchEmployees(String _FirstName, String _LastName) {

		mApi.fetchEmployees(_FirstName, _LastName).enqueue(new Callback<ArrayList<EmployeeVo>>() {
			@Override
			public void onResponse(Call<ArrayList<EmployeeVo>> call, Response<ArrayList<EmployeeVo>> response) {
				if ( response.body() != null ) {
					PhonebookModel.getInstance().setFoundEmployees(response.body());
				}
			}

			@Override
			public void onFailure(Call<ArrayList<EmployeeVo>> call, Throwable t) {
				showErrorToast();
			}

		});
	}

	/**
	 * *
	 */
	public void fetchSemesterData() {

		mApi.fetchSemesterData().enqueue(new Callback<SemesterDataVo>() {
			@Override
			public void onResponse(Call<SemesterDataVo> call, Response<SemesterDataVo> response) {
				if ( response.body() != null ) {
					SemesterDataModel.getInstance().setData(response.body().getSemester());
				}
			}

			@Override
			public void onFailure(Call<SemesterDataVo> call, Throwable t) {
				showErrorToast();
			}
		});
	}

	/**
	 * *
	 */
	public void fetchMensaData() {

		mApi.fetchMensaData(UserSettings.getInstance().getChosenMensa()).enqueue( new Callback<MensaFoodItemVo[]>() {
			@Override
			public void onResponse(Call<MensaFoodItemVo[]> call, Response<MensaFoodItemVo[]> response) {
				MensaFoodItemVo[] _mensaItems =response.body();
				List<MensaFoodItemCollectionVo> orderedItems = null;

				if (_mensaItems != null && _mensaItems.length > 0)
					orderedItems = MensaUtils.orderMensaItems(_mensaItems);

				if (orderedItems != null) {
					MensaFoodModel.getInstance().setFoodItems(orderedItems.toArray(new MensaFoodItemCollectionVo[orderedItems.size()]));
				} else {
					MensaFoodModel.getInstance().setFoodItems(null);
				}
			}

			@Override
			public void onFailure(Call<MensaFoodItemVo[]> call, Throwable t) {
				showErrorToast();
			}
		});
	}

	/**
	 * *
	 */
	public void fetchNewsData() {

		fetchNewsData(UserSettings.getInstance().getChosenNewsCategory(), new Callback<NewsItemResponse>() {
			@Override
			public void onResponse(Call<NewsItemResponse> call, Response<NewsItemResponse> response) {
				// MS: Bei den News sind die news/0 kaputt
				if ( response.body() != null ) {
					final NewsItemVo[] newsItemVos = response.body().getChannel().getNewsItems();
					NewsModel.getInstance().setNewsItems(newsItemVos);
				}
			}

			@Override
			public void onFailure(Call<NewsItemResponse> call, Throwable t) {
				showErrorToast();
			}
		});
	}

	/**
	 * *
	 */
	public void fetchNewsData(String _NewsCategory, Callback<NewsItemResponse> _Callback) {

		mApi.fetchNewsData(_NewsCategory ).enqueue(_Callback);
	}

	/**
	 * *
	 */
	public void fetchAvailableMensas() {

		mApi.fetchAvailableMensas().enqueue(new Callback<MensaChoiceItemVo[]>() {
			@Override
			public void onResponse(Call<MensaChoiceItemVo[]> call, Response<MensaChoiceItemVo[]> response) {
				// MS: Bei den News sind die news/0 kaputt
				if ( response.body() != null ) {
					MensaFoodModel.getInstance().setChoiceItems(response.body());
				}
			}

			@Override
			public void onFailure(Call<MensaChoiceItemVo[]> call, Throwable t) {
				showErrorToast();
			}
		});
	}

	/**
	 * *
	 */
	public void fetchAvailableNewsLists() {

		fetchAvailableNewsLists(new Callback<NewsCategoryResponse>() {
			@Override
			public void onResponse(Call<NewsCategoryResponse> call, Response<NewsCategoryResponse> response) {
				// MS: Bei den News sind die news/0 kaputt
				if ( response.body() != null ) {
					NewsModel.getInstance().setCategoryItems(response.body().getNewsCategories());
				}
			}

			@Override
			public void onFailure(Call<NewsCategoryResponse> call, Throwable t) {
				showErrorToast();
			}
		});
	}

	/**
	 * *
	 */
	public void fetchAvailableNewsLists(Callback<NewsCategoryResponse> _Callback) {

		mApi.fetchAvailableNewsLists().enqueue(_Callback);
	}

	/**
	 * *
	 */
	public void fetchWeather(Callback<WeatherResponse> _Callback) {

		mApi.fetchWeather().enqueue(_Callback);
	}

	/**
	 *
	 * @param _Callback
	 */
	public void fetchTimeTable(Callback<TimeTableResponse> _Callback) {

		mApi.fetchTimeTable().enqueue(_Callback);
	}

	/**
	 *
	 * @param _TimeTableId
	 * @param _Callback
	 */
	public void fetchTimeTableEvents(String _TimeTableId, Callback<ArrayList<TimeTableWeekVo>> _Callback) {

		mApi.fetchTimeTableEvents(_TimeTableId).enqueue(_Callback);
	}
	public List<FlatDataStructure> reloadEvents(FlatDataStructure event) {
			List<FlatDataStructure>eventList = new ArrayList<>();
		try {
			ArrayList<TimeTableWeekVo> timeTable=mApi.fetchTimeTableEvents(event.getStudyGroup().getTimeTableId()).execute().body();
			for (TimeTableWeekVo weekEntry:timeTable){
				for(TimeTableDayVo dayEntry:weekEntry.getDays() ){
					for(TimeTableEventVo eventEntry : dayEntry.getEvents() ){
						if(eventEntry.getTitle().equals(event.getEvent().getTitle())){
							FlatDataStructure item = event.copy();
							item
								.setEventWeek(weekEntry)
								.setEventDay(dayEntry)
								.setEvent(eventEntry);
							eventList.add(item);
						}
					}
				}
			}
		} catch (IOException e) {
			Log.e(TAG, "fetchTimeTableEventsSynchron: ",e );
		}
		return eventList;
	}
	public void registerTimeTableChanges(String json, Callback<ArrayList<TimeTableWeekVo>>_Callback){
		mApi.registerTimeTableChanges(json).enqueue(_Callback);

	}


	/**
	 *
	 * @param _Callback
	 */
	public void fetchCafeAquaStatus(Callback<CafeAquaResponse> _Callback) {

		mApi.fetchCafeAquaStatus().enqueue(_Callback);
	}

	/**
	 *
	 */
	private void showErrorToast() {
		Toast.makeText(Main.getAppContext(), "Cannot establish connection!",
				Toast.LENGTH_LONG).show();
	}

	/**
	 *
	 */
	private NetworkHandler() {
		Gson gson = new GsonBuilder()
				.setDateFormat("HH:mm:ss'T'yyyy-MM-dd")
				.create();

		mRestAdapter = new Retrofit.Builder()
				.baseUrl(Endpoints.BASE_URL + Endpoints.APP_NAME)
				.addConverterFactory(GsonConverterFactory.create(gson))
				//.setConverter(new GsonConverter(gson))
//                .setLogLevel(RestAdapter.LogLevel.FULL)
				.build();

		mApi = mRestAdapter.create(ApiDeclaration.class);

	}


	private static final String LOG_TAG = NetworkHandler.class.getSimpleName();

	private static NetworkHandler ourInstance = new NetworkHandler();

	private Retrofit mRestAdapter;
	private ApiDeclaration  mApi;

}
