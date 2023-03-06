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

package de.fhe.fhemobile.fragments.myschedule;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.services.PushNotificationService;
import de.fhe.fhemobile.views.myschedule.MyScheduleOverviewView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyScheduleOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * Fragment of view showing overview of all subscribed courses
 */
public class MyScheduleOverviewFragment extends FeatureFragment {

	public static final String TAG = MyScheduleOverviewFragment.class.getSimpleName();


	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment {@link MyScheduleOverviewFragment}.
	 */
	public static MyScheduleOverviewFragment newInstance() {
		final MyScheduleOverviewFragment fragment = new MyScheduleOverviewFragment();
		final Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public MyScheduleOverviewFragment() {
		// Required empty public constructor
	}


	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
							 final Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		final MyScheduleOverviewView mView = (MyScheduleOverviewView) inflater.inflate(
				R.layout.fragment_myschedule_setup, container, false);
		mView.initializeView(getChildFragmentManager());

		mView.setCourseListEmptyView();
		return mView;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		if(//if push notifications enabled
				PreferenceManager.getDefaultSharedPreferences(Main.getAppContext())
						.getBoolean(getResources().getString(R.string.sp_myschedule_enable_fcm), false)){
			PushNotificationService.registerSubscribedEventSeries();
		}
	}

}

