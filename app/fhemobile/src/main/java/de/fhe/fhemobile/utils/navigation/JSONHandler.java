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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import de.fhe.fhemobile.models.navigation.Cell;
import de.fhe.fhemobile.models.navigation.Exit;
import de.fhe.fhemobile.models.navigation.FloorConnection;
import de.fhe.fhemobile.models.navigation.FloorConnectionCell;
import de.fhe.fhemobile.models.navigation.Room;

public class JSONHandler {

    //Constants
    private static final String TAG = "JSONHandler";            //$NON-NLS
    public static final String BUILDING = "building";           //$NON-NLS
    public static final String FLOOR = "floor";                 //$NON-NLS
    public static final String X_COORDINATE = "xCoordinate";    //$NON-NLS
    public static final String Y_COORDINATE = "yCoordinate";    //$NON-NLS
    public static final String WALKABLE = "walkable";           //$NON-NLS
    public static final String TYPE = "type";                   //$NON-NLS
    public static final String ROOM_NUMBER = "roomNumber";      //$NON-NLS
    public static final String QR_CODE = "qrCode";              //$NON-NLS
    public static final String PERSONS = "persons";             //$NON-NLS
    public static final String EXITTO = "exitTo";               //$NON-NLS
    public static final String ENTRYFROM = "entryFrom";          //$NON-NLS
    public static final String CONNECTED_CELLS = "connectedCells";  //$NON-NLS
    public static final String EXITCELLS = "cells";             //$NON-NLS

    //asset file names
    final static String FILE_FLOORCONNECTIONS = "floorconnections.json"; //$NON-NLS
    final static String FILE_ROOMS = "rooms.json"; //$NON-NLS
    final static String FILE_EXITS = "exits.json"; //$NON-NLS

    //Constructor
    public JSONHandler() {
    }

    /**
     * Read floorplan as json from assets
     * @param context
     * @param jsonFile file name in assets
     * @return read floor plan as json string
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
    public static String readFloorGridFromAssets(final Context context, final String jsonFile){
        final StringBuffer text = new StringBuffer();

        try {
            final InputStream input = context.getResources().getAssets().open(jsonFile);
            final InputStreamReader reader = new InputStreamReader(input, "UTF-8"); //$NON-NLS

            BufferedReader br = new BufferedReader(reader);

            for (String line; (line = br.readLine()) != null; ) {
                text.append(line).append("\n");
            }
        }catch (final Exception e){
            Log.e(TAG, "reading floorplan from json failed", e);
        }

        //TODO ist das nötig - Nadja: ja weil ein String und kein StringBuffer returned wird
        return text.toString();
    }


    /**
     * Read in certain json file from assets
     * @param context
     * @param toRead name of json file that should be read in ("rooms", "exits", "floorconnections")
     * @return json string
     */
    public static String readFromAssets(final Context context, final String toRead){
        String filename = "";
        switch(toRead){
            case "rooms":
                filename = FILE_ROOMS; break;
            case "exits":
                filename = FILE_EXITS; break;
            case "floorconnections":
                filename = FILE_FLOORCONNECTIONS; break;
        }

        final StringBuffer text = new StringBuffer();

        try {
            final InputStream input = context.getResources().getAssets().open(filename);
            final InputStreamReader reader = new InputStreamReader(input, "UTF-8"); //$NON-NLS

            BufferedReader br = new BufferedReader(reader);

            for (String line; (line = br.readLine()) != null; ) {
                text.append(line).append("\n");
            }
        } catch (final Exception e){
            Log.e(TAG, "reading" +toRead+ "from json failed", e);
        }

        return text.toString();
    }

    /**
     *
     * @param json
     * @return ArrayList of {@link Exit} objects
     */
    public ArrayList<Exit> parseJsonExits(final String json){
        final ArrayList<Exit> exits = new ArrayList<>();

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

                final Exit newExit = new Exit(jEntry.optInt(X_COORDINATE),
                        jEntry.getInt(Y_COORDINATE),
                        jEntry.getString(BUILDING),
                        jEntry.getString(FLOOR),
                        exitTo,
                        entryFrom);

                exits.add(newExit);

            }
        } catch (JSONException e) {
            Log.e(TAG, "error parsing JSON exits", e);
        }

        return exits;

    }


    /**
     * Parse rooms from JSON
     * @param json
     * @return ArrayList of {@link Room} objects
     */
    public ArrayList<Room> parseJsonRooms(final String json) {

        final ArrayList<Room> rooms = new ArrayList<>();

        try {
            final JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {

                final JSONObject jEntry = jsonArray.getJSONObject(i);
                final JSONArray personsJSON = jEntry.getJSONArray(PERSONS);
                final ArrayList<String> persons = new ArrayList<>();
                for (int j = 0; j < personsJSON.length(); j++) {
                    persons.add(personsJSON.optString(j));
                }
                final Room newRoom = new Room(jEntry.optString(ROOM_NUMBER),
                        jEntry.optString(BUILDING),
                        jEntry.optString(FLOOR),
                        jEntry.optString(QR_CODE),
                        persons,
                        jEntry.optInt(X_COORDINATE),
                        jEntry.optInt(Y_COORDINATE),
                        jEntry.optBoolean(WALKABLE));

                rooms.add(newRoom);
            }
        } catch (final Exception e) {
            Log.e(TAG, "error parsing JSON rooms", e);
        }
        return rooms;
    }

    /**
     * Parse floor connections from JSON
     * @param json
     * @return ArrayList of {@link FloorConnection} objects
     */
    public ArrayList<FloorConnection> parseJsonFloorConnection(final String json) {

        final ArrayList<FloorConnection> floorConnections = new ArrayList<>();

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

                final FloorConnection newConnection = new FloorConnection(floorConnectionType, connectedCells);
                floorConnections.add(newConnection);
            }
        } catch (final Exception e) {
            Log.e(TAG, "error parsing JSON floorConnections array", e);
        }
        return floorConnections;
    }

    /**
     * Parse walkable cells from JSON
     * @param json
     * @return ArrayList of walkable cells
     */
    public HashMap<String, Cell>  parseJsonWalkableCells(final String json) {

        final HashMap<String, Cell> walkableCells = new HashMap<>();

        try {
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                final Cell entry = new Cell();

                final JSONObject jEntry = jsonArray.getJSONObject(i);
                int x = jEntry.optInt(X_COORDINATE);
                int y = jEntry.optInt(Y_COORDINATE);
                entry.setXCoordinate(x);
                entry.setYCoordinate(y);
                entry.setWalkability(jEntry.optBoolean(WALKABLE));

                /* walkableCells.put(Integer.toString(x) + '_' + Integer.toString(y), entry); */
                walkableCells.put("" + x + '_' + y, entry);

            }
        } catch (Exception e) {
            Log.e(TAG, "error parsing JSON walkableCells", e);
        }
        return walkableCells;
    }
}
