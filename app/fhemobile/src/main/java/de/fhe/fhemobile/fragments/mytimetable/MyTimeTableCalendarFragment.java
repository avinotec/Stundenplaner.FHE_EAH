package de.fhe.fhemobile.fragments.mytimetable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.views.timetable.MyTimeTableCalendarView;

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

		return mView;
	}


	private MyTimeTableCalendarView mView;

}
