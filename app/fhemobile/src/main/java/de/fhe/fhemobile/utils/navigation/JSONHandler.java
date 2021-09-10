package de.fhe.fhemobile.utils.navigation;


import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import de.fhe.fhemobile.models.navigation.*;

public class JSONHandler {

    //Constants
    private static final String TAG = "JSONHandler"; //$NON-NLS

    //Constructor
    public JSONHandler() {
    }

    //Read JSON from assets
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String readJsonFromAssets(Context context, String jsonFile) {

        AssetManager assetManager = context.getAssets();
        String json = "";

        try {
            InputStream inputStream = assetManager.open(jsonFile);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, StandardCharsets.UTF_8);

        } catch (Exception e) {
            Log.e(TAG, "error reading JSON file", e);
        }
        return json;
    }

    //Parse JSON to rooms ArrayList<Cell>

    public ArrayList<Room> parseJsonRooms(String json) {

        ArrayList<Room> rooms = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {

                Room entry = new Room();

                JSONObject jEntry = jsonArray.getJSONObject(i);
                entry.setRoomNumber(jEntry.optString("roomNumber"));
                entry.setBuilding(jEntry.optString("building"));
                entry.setFloor(jEntry.optString("floor"));
                entry.setQRCode(jEntry.optString("qrCode"));
                entry.setXCoordinate(jEntry.optInt("xCoordinate"));
                entry.setYCoordinate(jEntry.optInt("yCoordinate"));
                entry.setWalkability(jEntry.optBoolean("walkable"));

                JSONArray personsJSON = jEntry.getJSONArray("persons");
                ArrayList<String> persons = new ArrayList<>();
                for (int j = 0; j < personsJSON.length(); j++) {
                    persons.add(personsJSON.optString(j));
                }

                entry.setPersons(persons);
                rooms.add(entry);
            }
        } catch (Exception e) {
            Log.e(TAG, "error parsing JSON rooms", e);
        }
        return rooms;
    }

    //Parse JSON to transitions ArrayList<Cell>
    public ArrayList<Transition> parseJsonTransitions(String json) {

        ArrayList<Transition> transitions = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {

                Transition entry = new Transition();
                JSONObject jEntry = jsonArray.getJSONObject(i);

                entry.setTypeOfTransition(jEntry.optString("type"));

                ArrayList<Cell> connectedCells = new ArrayList<>();
                JSONArray connectedCellsJSON = jEntry.getJSONArray("connectedCells");

                for (int j = 0; j < connectedCellsJSON.length(); j++) {

                    JSONObject cellJSON = connectedCellsJSON.getJSONObject(j);
                    Cell cell = new Cell();

                    cell.setBuilding(cellJSON.optString("building"));
                    cell.setFloor(cellJSON.optString("floor"));
                    cell.setXCoordinate(cellJSON.optInt("xCoordinate"));
                    cell.setYCoordinate(cellJSON.optInt("yCoordinate"));
                    cell.setWalkability(cellJSON.optBoolean("walkable"));

                    connectedCells.add(cell);
                }
                entry.setConnectedCells(connectedCells);
                transitions.add(entry);
            }
        } catch (Exception e) {
            Log.e(TAG, "error parsing JSON transitions array", e);
        }
        return transitions;
    }

    //Parse JSON to walkableCells ArrayList<Cell>
    public ArrayList<Cell> parseJsonWalkableCells(String json) {

        ArrayList<Cell> walkableCells = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                Cell entry = new Cell();

                JSONObject jEntry = jsonArray.getJSONObject(i);
                entry.setXCoordinate(jEntry.optInt("xCoordinate"));
                entry.setYCoordinate(jEntry.optInt("yCoordinate"));
                entry.setWalkability(jEntry.optBoolean("walkable"));

                walkableCells.add(entry);
            }
        } catch (Exception e) {
            Log.e(TAG, "error parsing JSON walkableCells", e);
        }
        return walkableCells;
    }
}
