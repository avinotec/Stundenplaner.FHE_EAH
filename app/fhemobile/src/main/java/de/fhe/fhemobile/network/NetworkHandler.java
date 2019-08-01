package de.fhe.fhemobile.network;

import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
import de.fhe.fhemobile.vos.timetable.TimeTableResponse;
import de.fhe.fhemobile.vos.timetable.TimeTableWeekVo;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by paul on 22.01.14.
 */
public class NetworkHandler {

	public static NetworkHandler getInstance() {
		return ourInstance;
	}

	/**
	 * *
	 * @param _FirstName
	 * @param _LastName
	 */
	public void fetchEmployees(String _FirstName, String _LastName) {

		mApi.fetchEmployees(_FirstName, _LastName, new Callback<ArrayList<EmployeeVo>>() {
			@Override
			public void success(ArrayList<EmployeeVo> t, Response response) {
				// MS: Bei den News sind die news/0 kaputt
				if ( t != null ) {
					PhonebookModel.getInstance().setFoundEmployees(t);
				}
			}

			@Override
			public void failure(RetrofitError error) {
				showErrorToast();
			}
		});
	}

	/**
	 * *
	 */
	public void fetchSemesterData() {

		mApi.fetchSemesterData(new Callback<SemesterDataVo>() {
			@Override
			public void success(SemesterDataVo t, Response response) {
				// MS: Bei den News sind die news/0 kaputt
				if ( t != null ) {
					SemesterDataModel.getInstance().setData(t.getSemester());
				}
			}

			@Override
			public void failure(RetrofitError error) {
				showErrorToast();
			}
		});
	}

	/**
	 * *
	 */
	public void fetchMensaData() {

		mApi.fetchMensaData(UserSettings.getInstance().getChosenMensa(), new Callback<MensaFoodItemVo[]>() {
			@Override
			public void success(MensaFoodItemVo[] _mensaItems, Response response) {
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
			public void failure(RetrofitError error) {
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
			public void success(NewsItemResponse t, Response response) {

				// MS: Bei den News sind die news/0 kaputt
				if ( t != null ) {
					final NewsItemVo[] newsItemVos = t.getChannel().getNewsItems();
					NewsModel.getInstance().setNewsItems(newsItemVos);
				}
			}

			@Override
			public void failure(RetrofitError error) {
				showErrorToast();
			}
		});
	}

	/**
	 * *
	 */
	public void fetchNewsData(String _NewsCategory, Callback<NewsItemResponse> _Callback) {

		mApi.fetchNewsData(_NewsCategory, _Callback);
	}

	/**
	 * *
	 */
	public void fetchAvailableMensas() {

		mApi.fetchAvailableMensas(new Callback<MensaChoiceItemVo[]>() {
			@Override
			public void success(MensaChoiceItemVo[] t, Response response) {
				// MS: Bei den News sind die news/0 kaputt
				if ( t != null ) {
					MensaFoodModel.getInstance().setChoiceItems(t);
				}
			}

			@Override
			public void failure(RetrofitError error) {
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
			public void success(NewsCategoryResponse t, Response response) {
				// MS: Bei den News sind die news/0 kaputt
				if ( t != null ) {
					NewsModel.getInstance().setCategoryItems(t.getNewsCategories());
				}
			}

			@Override
			public void failure(RetrofitError error) {
				showErrorToast();
			}
		});
	}

	/**
	 * *
	 */
	public void fetchAvailableNewsLists(Callback<NewsCategoryResponse> _Callback) {

		mApi.fetchAvailableNewsLists(_Callback);
	}

	/**
	 * *
	 */
	public void fetchWeather(Callback<WeatherResponse> _Callback) {

		mApi.fetchWeather(_Callback);
	}

	/**
	 *
	 * @param _Callback
	 */
	public void fetchTimeTable(Callback<TimeTableResponse> _Callback) {

		mApi.fetchTimeTable(_Callback);
	}

	/**
	 *
	 * @param _TimeTableId
	 * @param _Callback
	 */
	public void fetchTimeTableEvents(String _TimeTableId, Callback<ArrayList<TimeTableWeekVo>> _Callback) {

		mApi.fetchTimeTableEvents(_TimeTableId, _Callback);
	}

	/**
	 *
	 * @param _Callback
	 */
	public void fetchCafeAquaStatus(Callback<CafeAquaResponse> _Callback) {

		mApi.fetchCafeAquaStatus(_Callback);
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


		mRestAdapter = new RestAdapter.Builder()
				.setEndpoint(Endpoints.BASE_URL + Endpoints.APP_NAME)
				.setConverter(new GsonConverter(gson))
//                .setLogLevel(RestAdapter.LogLevel.FULL)
				.build();

		mApi = mRestAdapter.create(ApiDeclaration.class);
	}


	private static final String LOG_TAG = NetworkHandler.class.getSimpleName();

	private static NetworkHandler ourInstance = new NetworkHandler();

	private RestAdapter     mRestAdapter;
	private ApiDeclaration  mApi;

}
