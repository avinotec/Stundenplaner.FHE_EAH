package de.fhe.fhemobile.models.navigation;

import static de.fhe.fhemobile.utils.Define.Navigation.BUILDING_01;
import static de.fhe.fhemobile.utils.Define.Navigation.BUILDING_02;
import static de.fhe.fhemobile.utils.Define.Navigation.BUILDING_03;
import static de.fhe.fhemobile.utils.Define.Navigation.COSTS_FLOORCONNECTION;

import android.util.Log;

import java.util.ArrayList;

/**
 * Modellklasse für Stockwerk- und Gebäudeverbindungen wie Treppenhäuser, Aufzüge und die Brücke
 * created: Bachelor Thesis from Tim Münziger from SS2020
 * edited by Nadja 15.09.21
 */
public class FloorConnection extends Cell{

    //Constants
    private static final String TAG = "FloorConnection"; //$NON-NLS

    //Variables
    private String typeOfFloorConnection; //stairs, elevator, way outside building, bridge
    private ArrayList<Cell> connectedCells;

    //Constructor
    public FloorConnection() {
        super(COSTS_FLOORCONNECTION);
    }

    public FloorConnection(String type, ArrayList<Cell> connectedCells){
        super(COSTS_FLOORCONNECTION);
        this.connectedCells = connectedCells;
        this.typeOfFloorConnection = type;
    }

    //Getter
    public String getTypeOfFloorConnection() {
        return typeOfFloorConnection;
    }

    public ArrayList<Cell> getConnectedCells() {
        return connectedCells;
    }

    /**
     * Returns the connected cell at the particular building and floor
     * @param building
     * @param floor
     * @return connected cell at this building and floor
     */
    public Cell getConnectedCell(String building, String floor) {

        Cell cell = null;

        for (int index = 0; index < connectedCells.size(); index++) {

            //case building = 04 or 05
            if (connectedCells.get(index).getBuilding().equals(building)
                    && connectedCells.get(index).getFloorString().equals(floor)) {

                cell = connectedCells.get(index);
            }
            //case building = 03-02-01
            else if ((connectedCells.get(index).getBuilding().equals(BUILDING_03)
                    || connectedCells.get(index).getBuilding().equals(BUILDING_02)
                    || connectedCells.get(index).getBuilding().equals(BUILDING_01)) &&
                    (building.equals(BUILDING_03) || building.equals(BUILDING_02) || building.equals(BUILDING_01))
                    && connectedCells.get(index).getFloorString().equals(floor)) {

                cell = connectedCells.get(index);
            } else {
                Log.e(TAG, "FloorConnection does not connected any cells at the requested building and floor");
            }
        }

        return cell;
    }

}
