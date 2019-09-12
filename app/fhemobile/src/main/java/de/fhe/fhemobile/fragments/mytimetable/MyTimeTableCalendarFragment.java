package de.fhe.fhemobile.fragments.mytimetable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.views.timetable.MyTimeTableShowView;

public class MyTimeTableShowFragment extends FeatureFragment {

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment MyTimeTableShowFragment.
	 */
	public static MyTimeTableShowFragment newInstance() {
		MyTimeTableShowFragment fragment = new MyTimeTableShowFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public MyTimeTableShowFragment() {
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
		mView = (MyTimeTableShowView) inflater.inflate(R.layout.fragment_my_time_table, container, false);
		mView.initializeView(getChildFragmentManager());

		return mView;
	}


	private MyTimeTableShowView mView;

}
