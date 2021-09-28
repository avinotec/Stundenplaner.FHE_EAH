package de.fhe.fhemobile.models.navigation;

import static de.fhe.fhemobile.utils.Define.Navigation.*;

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
    private String typeOfFloorConnection; //stairs, elevator, crossing (Brücke)
    private ArrayList<Cell> connectedCells;

    //Constructor
    public FloorConnection() {
    }

    //Getter
    public String getTypeOfFloorConnection() {
        return typeOfFloorConnection;
    }

    public ArrayList<Cell> getConnectedCells() {
        return connectedCells;
    }

    public Cell getSingleCell(String building, String floor) {

        Cell cell = new Cell();

        for (int index = 0; index < connectedCells.size(); index++) {

            if (connectedCells.get(index).getBuilding().equals(BUILDING_05)
                    && (building.equals(BUILDING_05)) && connectedCells.get(index).getFloor().equals(floor)) {

                cell = connectedCells.get(index);
            }

            if (connectedCells.get(index).getBuilding().equals(BUILDING_04)
                    && (building.equals(BUILDING_04)) && connectedCells.get(index).getFloor().equals(floor)) {

                cell = connectedCells.get(index);
            }

            if ((connectedCells.get(index).getBuilding().equals(BUILDING_03)
                    || connectedCells.get(index).getBuilding().equals(BUILDING_02)
                    || connectedCells.get(index).getBuilding().equals(BUILDING_01)) &&
                    (building.equals(BUILDING_03) || building.equals(BUILDING_02) || building.equals(BUILDING_01))
                    && connectedCells.get(index).getFloor().equals(floor)) {

                cell = connectedCells.get(index);
            }
        }
        return cell;
    }

    //Setter
    public void setTypeOfFloorConnection(String typeOfFloorConnection) {
        this.typeOfFloorConnection = typeOfFloorConnection;
    }

    public void setConnectedCells(ArrayList<Cell> connectedCells) {
        this.connectedCells = connectedCells;
    }
}
