/*
 *  Copyright (c) 2020-2021 Ernst-Abbe-Hochschule Jena
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

package de.fhe.fhemobile.utils.navigation;


import android.content.Context;
import android.util.Log;

import androidx.core.util.Pair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import de.fhe.fhemobile.models.navigation.Cell;
import de.fhe.fhemobile.models.navigation.FloorConnection;
import de.fhe.fhemobile.models.navigation.Room;

public class JSONHandler {

    //Constants
    private static final String TAG = "JSONHandler"; //$NON-NLS
    public static final String BUILDING = "building";
    public static final String FLOOR = "floor";
    public static final String X_COORDINATE = "xCoordinate";
    public static final String Y_COORDINATE = "yCoordinate";
    public static final String WALKABLE = "walkable";
    public static final String TYPE = "type";
    public static final String ROOM_NUMBER = "roomNumber";
    public static final String QR_CODE = "qrCode";
    public static final String PERSONS = "persons";
    public static final String CONNECTED_CELLS = "connectedCells";

    //Constructor
    public JSONHandler() {
    }

    /**
     * Read JSON from assets
     * @param context
     * @param jsonFile in den Assets
     * @return
     */
    /*
    public String readJsonFromAssets(final Context context, final String jsonFile) {

        final AssetManager assetManager = context.getAssets();
        String json = "";

        try {
            final InputStream inputStream = assetManager.open(jsonFile);
            final int size = inputStream.available();
            final byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, StandardCharsets.UTF_8);

        } catch (Exception e) {
            Log.e(TAG, "error reading JSON file", e);
        }
        return json;
    } */
    public static String readJsonFromAssets(Context context, String jsonFile) throws IOException {

        final InputStream input = context.getResources().getAssets().open(jsonFile);
        final InputStreamReader reader = new InputStreamReader(input, "UTF-8");

        final StringBuffer text = new StringBuffer();
        BufferedReader br = new BufferedReader( reader );

        for ( String line; (line = br.readLine()) != null; ) {
            text.append(line + "\n");
        }
        return text.toString();
    }



    /**
     * Parse rooms from JSON
     * @param json
     * @return ArrayList fo rooms
     */
    public ArrayList<Room> parseJsonRooms(final String json) {

        final ArrayList<Room> rooms = new ArrayList<>();

        try {
            final JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {

                Room entry = new Room();

                final JSONObject jEntry = jsonArray.getJSONObject(i);
                entry.setRoomNumber(jEntry.optString(ROOM_NUMBER));
                entry.setBuilding(jEntry.optString(BUILDING));
                entry.setFloor(jEntry.optString(FLOOR));
                entry.setQRCode(jEntry.optString(QR_CODE));
                entry.setXCoordinate(jEntry.optInt(X_COORDINATE));
                entry.setYCoordinate(jEntry.optInt(Y_COORDINATE));
                entry.setWalkability(jEntry.optBoolean(WALKABLE));

                final JSONArray personsJSON = jEntry.getJSONArray(PERSONS);
                final ArrayList<String> persons = new ArrayList<>();
                for (int j = 0; j < personsJSON.length(); j++) {
                    persons.add(personsJSON.optString(j));
                }

                entry.setPersons(persons);
                rooms.add(entry);
            }
        } catch (final Exception e) {
            Log.e(TAG, "error parsing JSON rooms", e);
        }
        return rooms;
    }

    /**
     * Parse floor connections from JSON
     * @param json
     * @return ArrayList of floor connections
     */
    public ArrayList<FloorConnection> parseJsonFloorConnection(final String json) {

        final ArrayList<FloorConnection> transitions = new ArrayList<>();

        try {
            final JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {

                final FloorConnection entry = new FloorConnection();
                final JSONObject jEntry = jsonArray.getJSONObject(i);

                entry.setTypeOfFloorConnection(jEntry.optString(TYPE));

                final ArrayList<Cell> connectedCells = new ArrayList<>();
                final JSONArray connectedCellsJSON = jEntry.getJSONArray(CONNECTED_CELLS);

                for (int j = 0; j < connectedCellsJSON.length(); j++) {

                    final JSONObject cellJSON = connectedCellsJSON.getJSONObject(j);
                    final Cell cell = new Cell();

                    cell.setBuilding(cellJSON.optString(BUILDING));
                    cell.setFloor(cellJSON.optString(FLOOR));
                    cell.setXCoordinate(cellJSON.optInt(X_COORDINATE));
                    cell.setYCoordinate(cellJSON.optInt(Y_COORDINATE));
                    cell.setWalkability(cellJSON.optBoolean(WALKABLE));

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

    /**
     * Parse walkable cells from JSON
     * @param json
     * @return ArrayList of walkable cells
     */
    public HashMap<Pair<Integer,Integer>, Cell> parseJsonWalkableCells(final String json) {

        final HashMap<Pair<Integer,Integer>, Cell> walkableCells = new HashMap<>();

        try {
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                final Cell entry = new Cell();

                final JSONObject jEntry = jsonArray.getJSONObject(i);
                entry.setXCoordinate(jEntry.optInt(X_COORDINATE));
                entry.setYCoordinate(jEntry.optInt(Y_COORDINATE));
                entry.setWalkability(jEntry.optBoolean(WALKABLE));

                walkableCells.put(new Pair(X_COORDINATE, Y_COORDINATE), entry);

            }
        } catch (Exception e) {
            Log.e(TAG, "error parsing JSON walkableCells", e);
        }
        return walkableCells;
    }
}
