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


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.utils.TimeTableSettings;
import de.fhe.fhemobile.views.timetable.TimeTableEventsView;
import de.fhe.fhemobile.vos.timetable.TimeTableWeekVo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimeTableEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeTableEventsFragment extends FeatureFragment {


	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment TimeTableEventsFragment.
	 */
	public static TimeTableEventsFragment newInstance(String _TimeTableId) {
		TimeTableEventsFragment fragment = new TimeTableEventsFragment();
		Bundle args = new Bundle();
		args.putString(PARAM_TIMETABLE_ID, _TimeTableId);
		fragment.setArguments(args);
		return fragment;
	}

	public TimeTableEventsFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mChosenTimeTableId = getArguments().getString(PARAM_TIMETABLE_ID);
		}

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		mView = (TimeTableEventsView) inflater.inflate(R.layout.fragment_time_table_events, container, false);
		mView.setViewListener(mViewListener);
		mView.initializeView(getChildFragmentManager());

		return mView;
	}

	@Override
	public void onResume() {
		super.onResume();
		NetworkHandler.getInstance().fetchTimeTableEvents(mChosenTimeTableId, mCallback);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater) {
		// If the drawer is open, show the global app actions in the action bar. See also
		// showGlobalContextActionBar, which controls the top-left area of the action bar.
		menu.clear();
		if (TimeTableSettings.fetchTimeTableSelection() != null) {
			inflater.inflate(R.menu.menu_time_table_events, menu);
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	//onOptionsItemSelected-------------------------------------------------------------------------
	@Override
	public boolean onOptionsItemSelected(MenuItem _item) {
		switch (_item.getItemId()) {
			case R.id.action_reset_selection:
				TimeTableSettings.saveTimeTableSelection(null);

				((MainActivity) getActivity()).changeFragment(TimeTableFragment.newInstance(), false);

				return true;

			//other item
			default:
				return super.onOptionsItemSelected(_item);
		}
	}

	private final Callback<ArrayList<TimeTableWeekVo>> mCallback = new Callback<ArrayList<TimeTableWeekVo>>() {
		@Override
		public void onResponse(Call<ArrayList<TimeTableWeekVo>> call, Response<ArrayList<TimeTableWeekVo>> response) {
			if (response.body() != null) {
				mView.setPagerItems(response.body());
			}
		}

		@Override
		public void onFailure(Call<ArrayList<TimeTableWeekVo>> call, Throwable t) {

		}

	};

	private final TimeTableEventsView.IViewListener mViewListener = new TimeTableEventsView.IViewListener() {
	};

	public static final String PARAM_TIMETABLE_ID = "paramTimeTableId";

	private TimeTableEventsView mView;

	private String mChosenTimeTableId;

}
