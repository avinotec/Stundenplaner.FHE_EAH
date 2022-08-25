/*
 *  Copyright (c) 2020-2022 Ernst-Abbe-Hochschule Jena
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

import org.jetbrains.annotations.NonNls;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.fragments.navigation.NavigationFragment;
import de.fhe.fhemobile.models.navigation.Cell;
import de.fhe.fhemobile.models.navigation.FloorConnectionCell;
import de.fhe.fhemobile.vos.navigation.BuildingExitVo;
import de.fhe.fhemobile.vos.navigation.FloorConnectionVo;
import de.fhe.fhemobile.vos.navigation.PersonVo;
import de.fhe.fhemobile.vos.navigation.RoomVo;

public final class JSONHandler {

    //Constants
    private static final String TAG = JSONHandler.class.getSimpleName();    //$NON-NLS
    public static final String BUILDING = "building";           //$NON-NLS
    public static final String FLOOR = "floor";                 //$NON-NLS
    public static final String X_COORDINATE = "xCoordinate";    //$NON-NLS
    public static final String Y_COORDINATE = "yCoordinate";    //$NON-NLS
    public static final String WALKABLE = "walkable";           //$NON-NLS
    public static final String TYPE = "type";                   //$NON-NLS
    public static final String ROOM_NUMBER = "roomNumber";      //$NON-NLS
    public static final String QR_CODE = "qrCode";              //$NON-NLS
    public static final String ROOM = "room";                   //$NON-NLS
    public static final String PERSON_NAME = "name";            //$NON-NLS
    public static final String EXITTO = "exitTo";               //$NON-NLS
    public static final String ENTRYFROM = "entryFrom";         //$NON-NLS
    public static final String CONNECTED_CELLS = "connectedCells";  //$NON-NLS
    @NonNls
    static final String JSON_SECTION_ROOMS = "rooms";
    @NonNls
    static final String JSON_SECTION_PERSONS = "persons";

    /* Utility classes have all fields and methods declared as static.
    Creating private constructors in utility classes prevents them from being accidentally instantiated. */
	private JSONHandler() {
	}


	//reading --------------------------------------------------------------------------------------
    /**
     * Read the floor plan as json from assets
     * @param jsonFile the file name in assets
     * @return read floor plan as json string
     */
    public static String readFloorGridFromAssets(final String jsonFile){
        final StringBuilder text = new StringBuilder();

        try {
            final InputStream input = Main.getAppContext().getAssets().open(jsonFile);
//            final InputStreamReader reader; //$NON-NLS
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//                reader = new InputStreamReader(input, StandardCharsets.UTF_8);
//            }
//            //if android < 19
//            else {
//                reader = new InputStreamReader(input, Charset.forName("UTF-8"));
//            }
            final InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);

            final BufferedReader br = new BufferedReader(reader);

            for (String line; (line = br.readLine()) != null; ) {
                text.append(line).append("\n");
            }
        } catch (final IOException e){
            Log.e(TAG, "reading floorplan from json failed", e);
        }

        //Ist das nötig? - Antwort Nadja: ja weil ein String und kein StringBuffer returned werden soll
        return text.toString();
    }


    /**
     * Read in certain json file from assets
     * @param filenameToRead name of json file that should be read in ("rooms", "exits", "floorconnections", "persons)
     * @return json string
     */
    public static String readFromAssets(final String filenameToRead){
        final String filename = filenameToRead + ".json";

        final StringBuilder text = new StringBuilder();

        try {
            /* final InputStream input = context.getResources().getAssets().open(filename); */
            // Wir holen uns hier den Applikationskontext, dann brauchen wir den nicht mehr durchzuschleifen
            final Context application = Main.getAppContext();
            final InputStream input = application.getAssets().open(filename);

            final InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8); //$NON-NLS

            final BufferedReader br = new BufferedReader(reader);

            for (String line; (line = br.readLine()) != null; ) {
                text.append(line).append("\n");
            }
        } catch (final IOException e){
            Log.e(TAG, "reading" +filenameToRead+ "from json failed", e);
        }

        return text.toString();
    }


    //parsing --------------------------------------------------------------------------------------

    /**
     * Parse persons from json
     * @param json the json string
     * @return HashMap of {@link PersonVo} objects, person's name used as key
     */
    public static HashMap<String, PersonVo> parseJsonPersons(final String json){
        final HashMap<String, PersonVo> persons = new HashMap<>();

        try{
            final JSONArray jsonArray = new JSONArray(json);

            for(int i = 0; i < jsonArray.length(); i++){

                final JSONObject jEntry = jsonArray.getJSONObject(i);

                final PersonVo newPerson = new PersonVo(
                        jEntry.optString(PERSON_NAME),
                        jEntry.getString(ROOM));

                persons.put(newPerson.getName(), newPerson);

            }
        } catch (final JSONException e) {
            Log.e(TAG, "error parsing JSON persons", e);
        }

        return persons;
    }

    /**
     * Parse exits from json
     * @param json the json string
     * @return ArrayList of {@link BuildingExitVo} objects
     */
    public static ArrayList<BuildingExitVo> parseJsonExits(final String json){
        final ArrayList<BuildingExitVo> buildingExits = new ArrayList<>();

        try{
            final JSONArray jsonArray = new JSONArray(json);

            for(int i = 0; i < jsonArray.length(); i++){

                final JSONObject jEntry = jsonArray.getJSONObject(i);

                final JSONArray exitToJSON = jEntry.getJSONArray(EXITTO);
                final ArrayList<String> exitTo = new ArrayList<>();
                for(int j = 0; j < exitToJSON.length(); j++){
                    exitTo.add(exitToJSON.optString(j));
                }

                final JSONArray entryFromJSON = jEntry.getJSONArray(ENTRYFROM);
                final ArrayList<String> entryFrom = new ArrayList<>();
                for(int j = 0; j < entryFromJSON.length(); j++){
                    entryFrom.add(entryFromJSON.optString(j));
                }

                final BuildingExitVo newBuildingExit = new BuildingExitVo(jEntry.optInt(X_COORDINATE),
                        jEntry.getInt(Y_COORDINATE),
                        jEntry.getString(BUILDING),
                        jEntry.getString(FLOOR),
                        exitTo,
                        entryFrom);

                buildingExits.add(newBuildingExit);

            }
        } catch (final JSONException e) {
            Log.e(TAG, "error parsing JSON exits", e);
        }

        return buildingExits;
    }


    /**
     * Parse rooms from JSON
     * @param json the json string
     * @return ArrayList of {@link RoomVo} objects
     */
    public static ArrayList<RoomVo> parseJsonRooms(final String json) {

        final ArrayList<RoomVo> rooms = new ArrayList<>();

        try {
            final JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {

                final JSONObject jEntry = jsonArray.getJSONObject(i);
                final RoomVo newRoom = new RoomVo(jEntry.optString(ROOM_NUMBER),
                        jEntry.optString(BUILDING),
                        jEntry.optString(FLOOR),
                        jEntry.optString(QR_CODE),
                        jEntry.optInt(X_COORDINATE),
                        jEntry.optInt(Y_COORDINATE),
                        jEntry.optBoolean(WALKABLE));

                rooms.add(newRoom);
            }
        } catch (final JSONException e) {
            Log.e(TAG, "error parsing JSON rooms", e);
        }
        return rooms;
    }

    /**
     * Parse floor connections from JSON
     * @param json the json string
     * @return ArrayList of {@link FloorConnectionVo} objects
     */
    public static ArrayList<FloorConnectionVo> parseJsonFloorConnection(final String json) {

        final ArrayList<FloorConnectionVo> floorConnections = new ArrayList<>();

        try {
            final JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {

                final JSONObject jEntry = jsonArray.getJSONObject(i);
                final String floorConnectionType = jEntry.optString(TYPE);

                // floorconnection is a staircase or elevator
                final ArrayList<FloorConnectionCell> connectedCells = new ArrayList<>();
                final JSONArray connectedCellsJSON = jEntry.getJSONArray(CONNECTED_CELLS);

                for (int j = 0; j < connectedCellsJSON.length(); j++) {

                    final JSONObject cellJSON = connectedCellsJSON.getJSONObject(j);
                    final FloorConnectionCell cell = new FloorConnectionCell(cellJSON.optInt(X_COORDINATE),
                            cellJSON.optInt(Y_COORDINATE),
                            cellJSON.optString(BUILDING),
                            cellJSON.optString(FLOOR),
                            cellJSON.optBoolean(WALKABLE),
                            floorConnectionType);

                    connectedCells.add(cell);
                }

                final FloorConnectionVo newConnection = new FloorConnectionVo(floorConnectionType, connectedCells);
                floorConnections.add(newConnection);
            }
        } catch (final JSONException e) {
            Log.e(TAG, "error parsing JSON floorConnections array", e);
        }
        return floorConnections;
    }

    /**
     * Parse walkable cells from JSON
     * @param json the json string
     * @return ArrayList of walkable cells
     */
    public static HashMap<String, Cell> parseJsonWalkableCells(final String json) {

        final HashMap<String, Cell> walkableCells = new HashMap<>();

        try {
            final JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                final Cell entry = new Cell();

                final JSONObject jEntry = jsonArray.getJSONObject(i);
                final int x = jEntry.optInt(X_COORDINATE);
                final int y = jEntry.optInt(Y_COORDINATE);
                entry.setXCoordinate(x);
                entry.setYCoordinate(y);
                entry.setWalkability(jEntry.optBoolean(WALKABLE));

                /* walkableCells.put(Integer.toString(x) + '_' + Integer.toString(y), entry); */
                walkableCells.put("" + x + '_' + y, entry);

            }
        } catch (final JSONException e) {
            Log.e(TAG, "error parsing JSON walkableCells", e); //$NON-NLS
        }
        return walkableCells;
    }

    //read, parse, save -------------------------------------------------------------------------------
    /**
     * Read rooms from assets and save list to MainActivity.rooms
     */
    public static void loadRooms(){
        if (NavigationFragment.rooms.isEmpty()) {
            try {
                final String roomsJson = JSONHandler.readFromAssets(JSON_SECTION_ROOMS);
                NavigationFragment.rooms = JSONHandler.parseJsonRooms(roomsJson);
            } catch (final RuntimeException e) {
                Log.e(TAG, "error reading or parsing rooms from JSON files:", e);
            }
        }
    }

    /**
     * Read rooms from assets
     * @return ArrayList of {@link RoomVo}s
     */
    public static ArrayList<RoomVo> getRooms(){
        try {
            final String roomsJson = JSONHandler.readFromAssets(JSON_SECTION_ROOMS);
            return JSONHandler.parseJsonRooms(roomsJson);
        } catch (final RuntimeException e) {
            Log.e(TAG, "error reading or parsing rooms from JSON files:", e);
        }

        return null;
    }

    /**
     * Read persons from assets and returns them in a HashMap
     * @return HashMap of {@link PersonVo} objects, person's names used as key
     */
    public static HashMap<String, PersonVo> loadPersons(){
        HashMap<String, PersonVo> persons = null;
        try{
            final String personsJson = JSONHandler.readFromAssets(JSON_SECTION_PERSONS);
            persons = JSONHandler.parseJsonPersons(personsJson);
        } catch (final RuntimeException e){
            Log.e(TAG, "error reading or parsing persons from JSON files:", e);
        }
        return persons;
    }
}
