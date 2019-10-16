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
import java.util.Date;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.models.TimeTableChanges.RequestModel;
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

		return mView;
	}


	private MyTimeTableCalendarView mView;

}
