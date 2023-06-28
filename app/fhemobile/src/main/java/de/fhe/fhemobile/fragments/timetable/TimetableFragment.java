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

package de.fhe.fhemobile.fragments.timetable;


import static de.fhe.fhemobile.utils.Define.Timetable.SP_TIMETABLE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.utils.ApiErrorUtils;
import de.fhe.fhemobile.utils.Define;
import de.fhe.fhemobile.utils.Utils;
import de.fhe.fhemobile.utils.timetable.TimetableSettings;
import de.fhe.fhemobile.views.timetable.TimetableView;
import de.fhe.fhemobile.vos.ApiErrorResponse;
import de.fhe.fhemobile.vos.timetable.TimetableWeekVo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimetableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimetableFragment extends FeatureFragment {

	public static final String TAG = TimetableFragment.class.getSimpleName();

	public static TimetableFragment newInstance(String timeTableId){
		final TimetableFragment fragment = new TimetableFragment();
		final Bundle args = new Bundle();
		args.putString(Define.Timetable.KEY_TIMETABLE_ID, timeTableId);
		fragment.setArguments(args);
		return fragment;
	}


	public TimetableFragment() {
		super(TAG);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mChosenTimetableId = getArguments().getString(Define.Timetable.KEY_TIMETABLE_ID);
		}
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
	                         final Bundle savedInstanceState) {
		mView = (TimetableView) inflater.inflate(R.layout.fragment_timetable, container, false);

		mView.initializeView(getChildFragmentManager(), getLifecycle());

		return mView;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		//replacement of deprecated setHasOptionsMenu(), onCreateOptionsMenu() and onOptionsItemSelected()
		// see https://developer.android.com/jetpack/androidx/releases/activity#1.4.0-alpha01
		final MenuHost menuHost = requireActivity();
		final Activity activity = getActivity();
		menuHost.addMenuProvider(new MenuProvider() {
			@Override
			public void onCreateMenu(@NonNull final Menu menu, @NonNull final MenuInflater menuInflater) {
				// Add menu items here
				menu.clear();
				if (TimetableSettings.getTimetableSelection() != null) {
					menuInflater.inflate(R.menu.menu_timetable, menu);
				}
			}

			@Override
			public boolean onMenuItemSelected(@NonNull final MenuItem menuItem) {
				// Handle the menu selection
				if (menuItem.getItemId() == R.id.action_reset_selection) {

					TimetableSettings.saveTimetableSelection(null);
					((MainActivity) activity).changeFragment(TimetableDialogFragment.newInstance(), false);
					return true;
				}

				return false;
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		NetworkHandler.getInstance().fetchTimetable(mChosenTimetableId, mCallback);
//		todo: for debugging response = null
//		NetworkHandler.getInstance().fetchTimetableEvents("53B3DB05F11C6EA417AA82B3DA33991B", mCallback);

	}

	private final Callback<Map<String, TimetableWeekVo>> mCallback = new Callback<Map<String, TimetableWeekVo>>() {
		@Override
		public void onResponse(@NonNull final Call<Map<String, TimetableWeekVo>> call, final Response<Map<String, TimetableWeekVo>> response) {
			if(response.isSuccessful()){
				final ArrayList<TimetableWeekVo> weekVos = new ArrayList(response.body().values());
				mView.setPagerItems(weekVos);

				if(TimetableSettings.getTimetableSelection() != null){
					//save timetable for offline usage
					final SharedPreferences sharedPreferences = Main.getAppContext().getSharedPreferences(SP_TIMETABLE, Context.MODE_PRIVATE);
					final SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString(SP_TIMETABLE, new Gson().toJson(weekVos));
					editor.apply();
				}


			} else {
				final ApiErrorResponse error = ApiErrorUtils.getApiErrorResponse(response);
				ApiErrorUtils.showErrorToast(error, ApiErrorUtils.ApiErrorCode.TIMETABLE_FRAGMENT_CODE1);

				//load timetable from shared preferences
				restoreTimetableFromSharedPreferences();
			}
		}

		/**
		 *
		 * Is called on network failures and RetroFit conversion errors
		 * @param call
		 * @param t
		 */
		@Override
		public void onFailure(@NonNull final Call<Map<String, TimetableWeekVo>> call, @NonNull final Throwable t) {
			if(t instanceof IOException){
				ApiErrorUtils.showConnectionErrorToast(ApiErrorUtils.ApiErrorCode.TIMETABLE_FRAGMENT_CODE3);
			} else {
				ApiErrorUtils.showErrorToast(ApiErrorUtils.ApiErrorCode.TIMETABLE_FRAGMENT_CODE2);
			}
			Log.d(TAG, "failure: request " + call.request().url() + " - "+ t.getMessage());

			//load timetable from shared preferences
			restoreTimetableFromSharedPreferences();
		}
	};

	void restoreTimetableFromSharedPreferences(){
		//load timetable from shared preferences
		final SharedPreferences sharedPreferences = Main.getAppContext().getSharedPreferences(SP_TIMETABLE, Context.MODE_PRIVATE);
		final String json = sharedPreferences.getString(SP_TIMETABLE, "");
		if(!json.isEmpty()){
			final ArrayList<TimetableWeekVo> loadedTimetableWeeks = new Gson()
					.fromJson(json, new TypeToken<List<TimetableWeekVo>>(){}.getType());
			mView.setPagerItems(loadedTimetableWeeks);
		}
		Utils.showToast(R.string.timetable_restored);
	}


	TimetableView mView;


	//note: the timetable is determined by the study group
	// that is why the timetable id is a study group id
	private String mChosenTimetableId;

}
