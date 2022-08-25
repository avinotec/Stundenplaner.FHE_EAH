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

package de.fhe.fhemobile.fragments.myschedule;


import static de.fhe.fhemobile.Main.getSubscribedEventSeries;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import java.util.Collections;
import java.util.Date;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.comparator.EventSeriesTitleComparator;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.models.timetablechanges.TimeTableChangesRequestModel;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventSeriesVo;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.services.PushNotificationService;
import de.fhe.fhemobile.views.myschedule.MyScheduleSettingsView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyScheduleSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * Fragment of view showing overview of all subscribed courses
 */
public class MyScheduleSettingsFragment extends FeatureFragment {

	public static final String TAG = MyScheduleSettingsFragment.class.getSimpleName();


	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment TimeTableDialogFragment.
	 */
	public static MyScheduleSettingsFragment newInstance() {
		final MyScheduleSettingsFragment fragment = new MyScheduleSettingsFragment();
		final Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public MyScheduleSettingsFragment() {
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
		final MyScheduleSettingsView mView = (MyScheduleSettingsView) inflater.inflate(
				R.layout.fragment_myschedule_settings, container, false);
		mView.initializeView(getChildFragmentManager());

		mView.setCourseListEmptyView();
		return mView;
	}


	@Override
	public void onAttach(@NonNull final Context context) {
		super.onAttach(context);
		if(!getSubscribedEventSeries().isEmpty()){
			Collections.sort(getSubscribedEventSeries(), new EventSeriesTitleComparator());
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();

		PushNotificationService.registerSubscribedEventSeries();
	}

	static void showConnectionErrorToast() {
		Toast.makeText(Main.getAppContext(), Main.getAppContext().getString(R.string.connection_failed),
				Toast.LENGTH_LONG).show();
	}

}

