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


import static de.fhe.fhemobile.Main.getSubscribedCourseComponents;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import org.junit.Assert;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import de.fhe.fhemobile.BuildConfig;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.comparator.CourseTitleComparator;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.models.timeTableChanges.RequestModel;
import de.fhe.fhemobile.models.timeTableChanges.ResponseModel;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.services.PushNotificationService;
import de.fhe.fhemobile.utils.MyTimeTableUtils;
import de.fhe.fhemobile.views.mytimetable.MyTimeTableOverviewView;
import de.fhe.fhemobile.vos.mytimetable.MyTimeTableCourseComponent;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyTimeTableOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * Fragment of view showing overview of all subscribed courses
 */
public class MyTimeTableOverviewFragment extends FeatureFragment {

	public static final String TAG = "MyTimeTableOverviewFrag";


	private static final int CHANGEREASON_EDIT = 1;
	private static final int CHANGEREASON_NEW = 3;
	private static final int CHANGEREASON_DELETE = 2;


	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment TimeTableDialogFragment.
	 */
	public static MyTimeTableOverviewFragment newInstance() {
		final MyTimeTableOverviewFragment fragment = new MyTimeTableOverviewFragment();
		final Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public MyTimeTableOverviewFragment() {
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
		MyTimeTableOverviewView mView = (MyTimeTableOverviewView) inflater.inflate(
				R.layout.fragment_my_time_table_overview, container, false);
		mView.initializeView(getChildFragmentManager());

		mView.setCourseListEmptyView();
		return mView;
	}


	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		if(!getSubscribedCourseComponents().isEmpty()){
			Collections.sort(getSubscribedCourseComponents(),new CourseTitleComparator());
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();

		//TODO enable Push Notifications
		//PushNotificationsRegisterAndUpdateCourses();
	}

	private void PushNotificationsRegisterAndUpdateCourses() {
		if (!getSubscribedCourseComponents().isEmpty()) {
			final RequestModel request = new RequestModel(RequestModel.ANDROID_DEVICE, PushNotificationService.getFirebaseToken(), new Date().getTime() - 86400000);
			String title = "";
			String setID = "";

			for (MyTimeTableCourseComponent course : getSubscribedCourseComponents()) {
				final String eventTitleShort = course.getTitle();

				//todo: all setIds in the course's studyGroupList has to be searched
				/*final String sSetID = course.getStudyGroup().getTimeTableId();

				if ((title.equals(eventTitleShort) && setID.equals(sSetID))) {

					request.addCourse(course.getStudyGroup().getTimeTableId(), course.getEvent().getTitle());
					title = eventTitleShort;
					setID = sSetID;
				}*/
			}

			final String json = request.toJson();
			Log.d(TAG, "onDetach: " + json);
//TODO Pushnotification Das hier müsste zu den Push-Notifications gehören. Die aufgerufene URL müsste der Push-Server sein?
			NetworkHandler.getInstance().registerTimeTableChanges(json, new Callback<ResponseModel>() {
				@Override
				public void onResponse(final Call<ResponseModel> call, final Response<ResponseModel> response) {

					if (BuildConfig.DEBUG) Assert.assertTrue(response != null);
					//wieso assert und damit einen Absturz produzieren, wenn das einfach auftreten kann, wenn der Server nicht verfügbar ist?
					//vorallem wenn darunter eh ein if das gleiche abfragt.
					//	Assert.assertTrue( response.body() != null );

					//DEBUG
					if (response.body() == null) {
						// Da ist ein Fehler in der Kommunikation
						// 400: Bad request
						if (response.code() == 400) {
							String sErrorText = "";
							try {
								sErrorText = response.errorBody().string();
							} catch (IOException e) {
								Log.e(TAG,"Fehler im Netzwerk",e);
							}
							Log.d(TAG, "Error in Schedule Change Server: " + sErrorText);

//							AlertDialog.Builder builder1 = new AlertDialog.Builder(MyTimeTableOverviewFragment.this.getContext().getApplicationContext());
//							builder1.setMessage("Push Notifications: Error in Schedule Change Server: " + sErrorText);
//							AlertDialog alert11 = builder1.create();
//							alert11.show();
						}

						// nothing to do now....
						return;
					}


					final Gson gson = new Gson();
					final String json = gson.toJson(response.body());
					if (BuildConfig.DEBUG) Assert.assertTrue(!json.isEmpty());

					Log.d(TAG, "onResponse: " + response.raw().request().url());
					Log.d(TAG, "onResponse code: " + response.code() + " geparsed: " + json);

					final List<ResponseModel.Change> changes = response.body().getChanges();

					final List<String[]> negativeList = MyTimeTableOverviewView.generateNegativeLessons();
					final Iterator<ResponseModel.Change> iterator = changes.iterator();

					while (iterator.hasNext()) {

						final ResponseModel.Change change = iterator.next();
						//TODO: Negativ Liste von der gespeicherten Liste erstellen
						boolean isInNegativeList = false;
						for (String[] negativeEvent : negativeList) {
							if (change.getNewEventJson().getTitle().contains(negativeEvent[0])
									&& change.getSetSplusKey().equals(negativeEvent[1])) {
								isInNegativeList = true;
								break;
							}

						}

						if (isInNegativeList) {
							iterator.remove();
						}
					}

//				String changesAsString="";
//				for(ResponseModel.Change change: changes){
//					changesAsString+=(change.getChangesReasonText()+"/n/n");
//				}

//                new AlertDialog.Builder(MyTimeTableOverviewFragment.this.getActivity())
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


					for (ResponseModel.Change change : changes) {

						// Shortcut to the list
						final List<MyTimeTableCourseComponent> myTimetableList = getSubscribedCourseComponents();

						//Aenderung eines Events: suche das Event und ueberschreibe es
						if (change.getChangesReason() == CHANGEREASON_EDIT) {
							final MyTimeTableCourseComponent event = MyTimeTableUtils.getEventByID(myTimetableList, change.getNewEventJson().getUid());
							if (event != null) {
								//todo: auskommentiert im Zuge von Umbauarbeiten
								//event.setEvent(change.getNewEventJson());
							}
						}
						//Hinzufuegen eines neuen Events: Erstelle ein neues Element vom Typ MyTimeTableCourseComponent, schreibe alle Set-, Semester- und Studiengangdaten in dieses
						//und fuege dann die Eventdaten des neuen Events hinzu. Anschliessend in die Liste hinzufuegen.
						if (change.getChangesReason() == CHANGEREASON_NEW) {
							final MyTimeTableCourseComponent event = MyTimeTableUtils.getCoursesByStudyGroupTitle(myTimetableList, change.getSetSplusKey()).get(0).copy();
							//todo: auskommentiert im Zuge von Umbauarbeiten
							//event.setEvent(change.getNewEventJson());
							((MainActivity) getActivity()).addToSubscribedCourseComponentsAndUpdateAdapters(event);

						}
						//Loeschen eines Events: Suche den Event mit der SplusID und lösche ihn aus der Liste.
						if (change.getChangesReason() == CHANGEREASON_DELETE) {
							final MyTimeTableCourseComponent event = MyTimeTableUtils.getEventByID(myTimetableList, change.getNewEventJson().getUid());
							((MainActivity) getActivity()).removeFromSubscribedCourseComponentsAndUpdateAdapters(event);
						}
					}

				}

				@Override
				public void onFailure(Call<ResponseModel> call, Throwable t) {
					Log.d(TAG, "onFailure: " + t.toString());
				}
			});
		}
	}

	// static --------------------------------------------------------------------------------------

}
