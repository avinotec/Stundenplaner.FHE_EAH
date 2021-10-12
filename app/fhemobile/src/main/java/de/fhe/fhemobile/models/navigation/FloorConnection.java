package de.fhe.fhemobile.models.navigation;

import static de.fhe.fhemobile.utils.Define.Navigation.BUILDING_01;
import static de.fhe.fhemobile.utils.Define.Navigation.BUILDING_02;
import static de.fhe.fhemobile.utils.Define.Navigation.BUILDING_03;

import android.util.Log;

import java.util.ArrayList;

/**
 * Modellklasse für Stockwerk- und Gebäudeverbindungen wie Treppenhäuser, Aufzüge und die Brücke
 * created: Bachelor Thesis from Tim Münziger from SS2020
 * edited by Nadja 15.09.21
 */
public class FloorConnection{

    //Constants
    private static final String TAG = "FloorConnection"; //$NON-NLS

    //Variables
    private String typeOfFloorConnection; //stairs, elevator, way outside building, bridge
    private ArrayList<FloorConnectionCell> connectedCells;


    public FloorConnection(String type, ArrayList<FloorConnectionCell> connectedCells){
        this.connectedCells = connectedCells;
        this.typeOfFloorConnection = type;
    }

    //Getter
    public String getTypeOfFloorConnection() {
        return typeOfFloorConnection;
    }

    public ArrayList<FloorConnectionCell> getConnectedCells() {
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

        for (int i = 0; i < connectedCells.size(); i++) {

            //case building = 04 or 05
            if (connectedCells.get(i).getBuilding().equals(building)
                    && connectedCells.get(i).getFloorString().equals(floor)) {

                cell = connectedCells.get(i);
            }
            //case building = 03-02-01
            else if ((connectedCells.get(i).getBuilding().equals(BUILDING_03)
                    || connectedCells.get(i).getBuilding().equals(BUILDING_02)
                    || connectedCells.get(i).getBuilding().equals(BUILDING_01)) &&
                    (building.equals(BUILDING_03) || building.equals(BUILDING_02) || building.equals(BUILDING_01))
                    && connectedCells.get(i).getFloorString().equals(floor)) {

                cell = connectedCells.get(i);
            } else {
                Log.e(TAG, "FloorConnection does not connected any cells at the requested building and floor");
            }
        }

        return cell;
    }

}
