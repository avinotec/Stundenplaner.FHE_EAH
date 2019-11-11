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

package de.fhe.fhemobile.fragments.mytimetable;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.views.timetable.MyTimeTableCalendarView;
import de.fhe.fhemobile.views.timetable.MyTimeTableView;
import de.fhe.fhemobile.vos.timetable.FlatDataStructure;

public class MyTimeTableCalendarFragment extends FeatureFragment {

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment MyTimeTableCalendarFragment.
	 */
	public static MyTimeTableCalendarFragment newInstance() {
		MyTimeTableCalendarFragment fragment = new MyTimeTableCalendarFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public MyTimeTableCalendarFragment() {
		// Required empty public constructor
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		mView = (MyTimeTableCalendarView) inflater.inflate(R.layout.fragment_calendar_my_time_table, container, false);
		mView.initializeView(getActivity().getSupportFragmentManager());
		SharedPreferences sharedPreferences =getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
		String json = sharedPreferences.getString("list","");
		Gson gson = new Gson();
		FlatDataStructure[] list = gson.fromJson(json, FlatDataStructure[].class);
		if(list!=null) {
			MyTimeTableView.setLessons(new ArrayList<FlatDataStructure>(Arrays.asList(list)));


		}
		String emptyText = getResources().getString(R.string.empty_text_calendar);
		mView.setEmptyText(emptyText);

		return mView;
	}


	private MyTimeTableCalendarView mView;

}
