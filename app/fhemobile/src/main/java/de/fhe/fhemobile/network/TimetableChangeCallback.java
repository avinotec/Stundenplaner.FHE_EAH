package de.fhe.fhemobile.network;

import android.app.AlertDialog;
import android.util.Log;

import com.google.gson.Gson;

import org.junit.Assert;
import java.util.Iterator;
import java.util.List;

import de.fhe.fhemobile.BuildConfig;
import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.views.mytimetable.MyTimeTableSettingsView;
import de.fhe.fhemobile.vos.timetablechanges.TimetableChange;
import de.fhe.fhemobile.vos.timetablechanges.TimetableChangesResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimetableChangeCallback implements Callback<TimetableChangesResponse> {

    private static final String TAG = TimetableChangeCallback.class.getSimpleName();

    @Override
    public void onResponse(final Call<TimetableChangesResponse> call,
                           final Response<TimetableChangesResponse> response) {

        //DEBUG
        if(response == null || response.body() == null) {
            // Da ist ein Fehler in der Kommunikation
            // 400: Bad request
            if(response.code() == 400) {
                final String sErrorText = response.errorBody().toString();
                Log.d(TAG, "Error in Schedule Change Server: " + sErrorText );

                final AlertDialog.Builder builder1 = new AlertDialog.Builder(Main.getAppContext());
                builder1.setMessage(
                        "Push Notifications: Error in Schedule Change Server: " + sErrorText);
                final AlertDialog alert11 = builder1.create();
                alert11.show();
            }

            // nothing to do now....
            return;
        }

        final Gson gson = new Gson();
        final String json = gson.toJson(response.body());
        if (BuildConfig.DEBUG) Assert.assertFalse(json.isEmpty());

        Log.d(TAG, "onResponse: " + response.raw().request().url());
        Log.d(TAG, "onResponse code: " + response.code() + " geparsed: " + json );

        final List<TimetableChange> changes = response.body().getChanges();

        final List<String[]> negativeList = MyTimeTableSettingsView.generateNegativeLessons();
        final Iterator<TimetableChange> iterator = changes.iterator();

//        while(iterator.hasNext()){
//
//            final TimetableChangesResponse.Change change = iterator.next();
//            boolean isInNegativeList = false;
//            for(final String[] negativeEvent : negativeList){
//                if (change.getNewEventJson().getGuiTitle().contains(negativeEvent[0])
//                        && change.getSetSplusKey().equals( negativeEvent[1] ) ) {
//                    isInNegativeList = true;
//                    break;
//                }
//            }
//
//            if(isInNegativeList){
//                iterator.remove();
//            }
//        }

//				String changesAsString="";
//				for(TimetableChangesResponse.Change change: changes){
//					changesAsString+=(change.getChangesReasonText()+"/n/n");
//				}

//                new AlertDialog.Builder(MyTimeTableSettingsFragment.this.getActivity())
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

        //folgender Code basiert auf generateNegativeList welche wie es Änderungen der Veranstaltungen in subscribedEventSeries berücksichtigt

        //todo: auskommentiert im Zuge von Umbauarbeiten
        //TODO PushNotifications
//                    for(final TimetableChangesResponse.Change change : changes){
//
//                        // Shortcut to the list
//                        final List<MyTimeTableEventSetVo> myTimetableList = subscribedEventSeries;
//
//                        //Aenderung einer Veranstaltung: suche das Event (= einzelne Veranstaltung) und ueberschreibe ihre Daten
//                        if(change.getChangesReason() == CHANGEREASON_EDIT) {
//                            final MyTimeTableEventSetVo event = MyTimeTableUtils.getEventByID(
//                                    myTimetableList, change.getNewEventJson().getId());
//                            if(event != null){
//
//                               event.setEvent(change.getNewEventJson());
//                            }
//                        }
//                        //Hinzufuegen einer neuen veranstaltung:
//                        // Erstelle ein neues Element vom Typ MyTimeTableEventSetVo, schreibe alle Set-, Semester- und Studiengangdaten in diesen
//                        //und fuege dann die Eventdaten des neuen Events hinzu. Anschliessend in die Liste hinzufuegen.
//                        if(change.getChangesReason() == CHANGEREASON_NEW) {
//                            final MyTimeTableEventSetVo event =
//                                    MyTimeTableUtils.getCoursesByStudyGroupTitle(
//                                            myTimetableList, change.getSetSplusKey()).get(0).copy();
//                            //todo: auskommentiert im Zuge von Umbauarbeiten
//                            //event.setEvent(change.getNewEventJson());
//
//                            //todo: bad static use to update MyTimeTableCalendar
//                            //MyTimeTableCalendarAdapter.addCourseAndUpdateSharedPreferences(event);
//
//                        }
//                        //Loeschen einer Veranstaltung: Suche die Veranstaltung mit der SplusID und lösche sie aus der Liste.
//                        if(change.getChangesReason() == CHANGEREASON_DELETE){
//                            final MyTimeTableEventSetVo event = MyTimeTableUtils.getEventByID(
//                                    myTimetableList, change.getNewEventJson().getId());
//
//                            //todo: bad static use to update MyTimeTableCalendar
//                            //MyTimeTableCalendarAdapter.removeCourseAndUpdateSharedPreferences(event);
//                        }
//                    }

    }

    @Override
    public void onFailure(final Call<TimetableChangesResponse> call, final Throwable t) {
        Log.d(TAG, "onFailure: " + t);
    }
}
