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


import static de.fhe.fhemobile.utils.Define.Navigation.FLOORCONNECTION_TYPE_BRIDGE;
import static de.fhe.fhemobile.utils.Define.Navigation.FLOORCONNECTION_TYPE_ELEVATOR;
import static de.fhe.fhemobile.utils.Define.Navigation.FLOORCONNECTION_TYPE_EXIT;
import static de.fhe.fhemobile.utils.Define.Navigation.FLOORCONNECTION_TYPE_STAIR;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import de.fhe.fhemobile.models.navigation.Cell;
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
    public static final String CONNECTED_CELLS = "connectedCells";  //$NON-NLS
    public static final String EXITCELLS = "cells";             //$NON-NLS

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
     * Read json containing all rooms
     * @param context
     * @return json as string
     */
    public static String readRoomsFromAssets(final Context context){
        final String FILE_ROOMS = "rooms.json"; //$NON-NLS
        final StringBuffer text = new StringBuffer();

        try {
            final InputStream input = context.getResources().getAssets().open(FILE_ROOMS);
            final InputStreamReader reader = new InputStreamReader(input, "UTF-8"); //$NON-NLS

            final BufferedReader br = new BufferedReader(reader);

            for (String line; (line = br.readLine()) != null; ) {
                text.append(line).append("\n");
            }
        }catch (final Exception e){
            Log.e(TAG, "reading rooms from json failed", e);
        }

        return text.toString();
    }

    /**
     * Read json containing all floor connections of all buildings
     * @param context
     * @return json as string
     */
    public static String readFloorConnectionsFromAssets(final Context context){
        final String FILE_FLOORCONNECTIONS = "floorconnections.json"; //$NON-NLS
        final StringBuffer text = new StringBuffer();

        try {
            final InputStream input = context.getResources().getAssets().open(FILE_FLOORCONNECTIONS);
            final InputStreamReader reader = new InputStreamReader(input, "UTF-8"); //$NON-NLS

            BufferedReader br = new BufferedReader(reader);

            for (String line; (line = br.readLine()) != null; ) {
                text.append(line).append("\n");
            }
        } catch (final Exception e){
            Log.e(TAG, "reading floorconnections from json failed", e);
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
     * @return ArrayList of floor connections
     */
    public ArrayList<FloorConnection> parseJsonFloorConnection(final String json) {

        final ArrayList<FloorConnection> floorConnections = new ArrayList<>();

        try {
            final JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {

                final JSONObject jEntry = jsonArray.getJSONObject(i);
                final String floorConnectionType = jEntry.optString(TYPE);

                // floorconnection is a staircase or elevator or bridge
                if(floorConnectionType.equals(FLOORCONNECTION_TYPE_ELEVATOR)
                        || floorConnectionType.equals(FLOORCONNECTION_TYPE_STAIR)
                        || floorConnectionType.equals(FLOORCONNECTION_TYPE_BRIDGE)){

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

                }else if(floorConnectionType.equals(FLOORCONNECTION_TYPE_EXIT)){

                    //todo model data structure for exit
                }


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
