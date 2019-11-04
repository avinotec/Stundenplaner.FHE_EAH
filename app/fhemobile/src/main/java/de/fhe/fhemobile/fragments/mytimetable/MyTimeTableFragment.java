package de.fhe.fhemobile.fragments.mytimetable;


import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import org.junit.Assert;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.models.timeTableChanges.RequestModel;
import de.fhe.fhemobile.models.timeTableChanges.ResponseModel;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.views.timetable.MyTimeTableView;
import de.fhe.fhemobile.vos.timetable.FlatDataStructure;
import de.fhe.fhemobile.vos.timetable.TimeTableResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyTimeTableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyTimeTableFragment extends FeatureFragment {
	public static final String TAG = "MyTimeTableFragment";

	private MyTimeTableView     mView;

	private TimeTableResponse mResponse;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment TimeTableFragment.
	 */
	public static MyTimeTableFragment newInstance() {
		final MyTimeTableFragment fragment = new MyTimeTableFragment();
		final Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public MyTimeTableFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {

		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		mView = (MyTimeTableView) inflater.inflate(R.layout.fragment_my_time_table, container, false);
		mView.initializeView(getChildFragmentManager());

		return mView;
	}

/*
	@Override
	public void onResume() {
		super.onResume();
	}
*/


	private final int CHANGEREASON_EDIT = 1;
	private final int CHANGEREASON_NEW = 3;
	private final int CHANGEREASON_DELETE = 2;

	@Override
	public void onDetach() {
		super.onDetach();


		final RequestModel request = new RequestModel(RequestModel.ANDROID_DEVICE, MainActivity.getFirebaseToken(),new Date().getTime()-86400000);
		String title="";
		String setID="";

		for (FlatDataStructure event:MyTimeTableView.getLessons()){
			final String eventTitleShort=FlatDataStructure.cutEventTitle(event.getEvent().getTitle());
			final String sSetID = event.getStudyGroup().getTimeTableId() ;

			if( (title.equals(eventTitleShort) && setID.equals( sSetID ) ) == false ) {

				request.addLesson(event.getStudyGroup().getTimeTableId(),event.getEvent().getTitle());
				title=eventTitleShort;
				setID=sSetID;
			}
		}

		final Gson gson = new Gson();
		final String json = gson.toJson(request);
		Log.d(TAG, "onDetach: " + json);

		NetworkHandler.getInstance().registerTimeTableChanges( json, new Callback<ResponseModel>() {
			@Override
			public void onResponse(final Call<ResponseModel> call, final Response<ResponseModel> response) {

				Assert.assertTrue( response != null );
				Assert.assertTrue( response.body() != null );

				//DEBUG
				if ( response.body() == null )
				{
					// Da ist ein Fehler in der Kommunikation
					// 400: Bad request
					if ( response.code() == 400 )
					{
						String sErrorText = response.errorBody().toString();
						Log.d( TAG, "Error in Schedule Change Server: " + sErrorText );

						AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
						builder1.setMessage("Push Notifications: Error in Schedule Change Server: " + sErrorText);
						AlertDialog alert11 = builder1.create();
						alert11.show();
					}

					// nothing to do now....
					return;
				}


				final Gson gson = new Gson();
				final String json = gson.toJson(response.body());
				Assert.assertTrue( !json.isEmpty() );

				Log.d(TAG, "onResponse: "+response.raw().request().url());
				Log.d(TAG, "onResponse code: "+response.code()+" geparsed: "+ json );

				final List<ResponseModel.Change> changes = response.body().getChanges();

				final List<String[]> negativeList = MyTimeTableView.generateNegativeLessons();
				final Iterator<ResponseModel.Change> iterator= changes.iterator();

				while(iterator.hasNext()){

					final ResponseModel.Change change=iterator.next();
					//TODO: Negativ Liste von der gespeicherten Liste erstellen
					boolean isInNegativeList=false;
					for(String[] negativeEvent:negativeList){
						if(change.getNewEventJson().getTitle().contains(negativeEvent[0])
								&& change.getSetSplusKey().equals(negativeEvent[1])){
							isInNegativeList=true;
						}

					}

					if(isInNegativeList){
						iterator.remove();
					}
				}

//				String changesAsString="";
//				for(ResponseModel.Change change: changes){
//					changesAsString+=(change.getChangesReasonText()+"/n/n");
//				}

//                new AlertDialog.Builder(MyTimeTableFragment.this.getActivity())
//                        .setTitle("Änderungen")
//                       // .setMessage(changesAsString)
//                        .setMessage("test")
//
//                        // Specifying a listener allows you to take an action before dismissing the dialog.
//                        // The dialog is automatically dismissed when a dialog button is clicked.
//                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                // Continue with delete operation
//                            }
//                        })
//                        .create()
//
//                        // A null listener allows the button to dismiss the dialog and take no further action.
//                        .show();


				for(ResponseModel.Change change : changes){

					// Shortcut to the list
					final List<FlatDataStructure> myTimetableList = MyTimeTableView.getLessons();

					//Aenderung eines Events: suche den Event und ueberschreibe seine Daten
					if(change.getChangesReason()==CHANGEREASON_EDIT) {
						final FlatDataStructure event = FlatDataStructure.getEventByID( myTimetableList, change.getNewEventJson().getUid());
						if(event!=null){
							event.setEvent(change.getNewEventJson());
						}
					}
					//Hinzufuegen eines neuen Events: Erstelle ein neues Element vom Typ FlatDataStructure, schreibe alle Set-, Semester- und Studiengangdaten in diesen
					//und fuege dann die Eventdaten des neuen Events hinzu. Anschliessend in die Liste hinzufuegen.
					if(change.getChangesReason()==CHANGEREASON_NEW) {
						final FlatDataStructure event = FlatDataStructure.queryGetEventsByStudyGroupTitle( myTimetableList, change.getSetSplusKey()).get(0).copy();
						event.setEvent(change.getNewEventJson());
						MyTimeTableView.getLessons().add(event);

					}
					//Loeschen eines Events: Suche den Event mit der SplusID und lösche ihn aus der Liste.
					if(change.getChangesReason()==CHANGEREASON_DELETE){
						final FlatDataStructure event = FlatDataStructure.getEventByID( myTimetableList, change.getNewEventJson().getUid());
						MyTimeTableView.getLessons().remove(event);
					}
				}

			}

			@Override
			public void onFailure(Call<ResponseModel> call, Throwable t) {
				Log.d(TAG, "onFailure: "+t.toString());
			}
		});
	}

}
