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

package de.fhe.fhemobile.fragments.timetable;


import static de.fhe.fhemobile.utils.Define.TimeTable.SP_TIMETABLE;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.utils.Define;
import de.fhe.fhemobile.utils.timetable.TimeTableSettings;
import de.fhe.fhemobile.views.timetable.TimeTableView;
import de.fhe.fhemobile.vos.timetable.TimeTableWeekVo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimeTableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeTableFragment extends FeatureFragment {

	public static final String TAG = TimeTableFragment.class.getSimpleName();

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment TimeTableFragment.
	 */
	public static TimeTableFragment newInstance(final String _TimeTableId) {
		final TimeTableFragment fragment = new TimeTableFragment();
		final Bundle args = new Bundle();
		args.putString(Define.PARAM_TIMETABLE_ID, _TimeTableId);
		fragment.setArguments(args);
		return fragment;
	}

	public TimeTableFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mChosenTimeTableId = getArguments().getString(Define.PARAM_TIMETABLE_ID);
		}

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
	                         final Bundle savedInstanceState) {
		mView = (TimeTableView) inflater.inflate(R.layout.fragment_timetable, container, false);

		mView.initializeView(getChildFragmentManager(), getLifecycle());

		return mView;
	}

	@Override
	public void onResume() {
		super.onResume();
		NetworkHandler.getInstance().fetchTimeTableEvents(mChosenTimeTableId, mCallback);
	}


	@Override
	public void onCreateOptionsMenu(final Menu menu, @NonNull final MenuInflater inflater) {
		// If the drawer is open, show the global app actions in the action bar. See also
		// showGlobalContextActionBar, which controls the top-left area of the action bar.
		menu.clear();
		if (TimeTableSettings.getTimeTableSelection() != null) {
			inflater.inflate(R.menu.menu_timetable_events, menu);
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	//onOptionsItemSelected-------------------------------------------------------------------------
	@Override
	public boolean onOptionsItemSelected(final MenuItem _item) {
        /* Checks use of resource IDs in places requiring constants
            Avoid the usage of resource IDs where constant expressions are required.
            A future version of the Android Gradle Plugin will generate R classes with
            non-constant IDs in order to improve the performance of incremental compilation.
            Issue id: NonConstantResourceId
         */
		if (_item.getItemId() == R.id.action_reset_selection) {
			TimeTableSettings.saveTimeTableSelection(null);

			((MainActivity) getActivity()).changeFragment(TimeTableDialogFragment.newInstance(),
					false, TimeTableDialogFragment.TAG);

			return true;

			//other item
		}
		return super.onOptionsItemSelected(_item);
	}

	private final Callback<Map<String, TimeTableWeekVo>> mCallback = new Callback<Map<String, TimeTableWeekVo>>() {
		@Override
		public void onResponse(final Call<Map<String, TimeTableWeekVo>> call, final Response<Map<String, TimeTableWeekVo>> response) {
			if(response.body() != null){
				ArrayList<TimeTableWeekVo> weekVos = new ArrayList(response.body().values());
				mView.setPagerItems(weekVos);

				//save timetable for offline usage
				final SharedPreferences sharedPreferences = getContext().getSharedPreferences(SP_TIMETABLE, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString(SP_TIMETABLE, new Gson().toJson(weekVos));
				editor.apply();

			} else {
				showInternalProblemToast();
			}
		}

		@Override
		public void onFailure(final Call<Map<String, TimeTableWeekVo>> call, final Throwable t) {
			showConnectionErrorToast();
			Log.d(TAG, "failure: request " + call.request().url() + " - "+ t.getMessage());

			//load timetable from shared preferences
			final SharedPreferences sharedPreferences = getContext().getSharedPreferences(SP_TIMETABLE, Context.MODE_PRIVATE);
			String json = sharedPreferences.getString(SP_TIMETABLE, "");
			if(!json.isEmpty()){
				ArrayList<TimeTableWeekVo> loadedTimeTableWeeks = new Gson()
						.fromJson(json, new TypeToken<List<TimeTableWeekVo>>(){}.getType());
				mView.setPagerItems(loadedTimeTableWeeks);
			}
		}
	};

	static void showConnectionErrorToast() {
		Toast.makeText(Main.getAppContext(), R.string.timetable_connection_failed,
				Toast.LENGTH_LONG).show();
	}

	void showInternalProblemToast(){
		Toast.makeText(Main.getAppContext(),
				Main.getAppContext().getString(R.string.internal_problems),
				Toast.LENGTH_LONG).show();
	}


	TimeTableView mView;


	//note: the timetable is determined by the study group
	// that is why the timetable id is a study group id
	private String mChosenTimeTableId;

}
