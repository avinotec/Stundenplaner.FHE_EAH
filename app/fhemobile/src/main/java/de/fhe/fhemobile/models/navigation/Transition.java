package de.fhe.fhemobile.models.navigation;

import java.util.ArrayList;

public class Transition extends Cell{

    //Constants
    private static final String TAG = "Transition"; //$NON-NLS

    private static final String BUILDING_01 = "01";
    private static final String BUILDING_02 = "02";
    private static final String BUILDING_03 = "03";
    private static final String BUILDING_04 = "04";
    private static final String BUILDING_05 = "05";

    //Variables
    private String typeOfTransition; //stair, elevator, crossing
    private ArrayList<Cell> connectedCells;

    //Constructor
    public Transition() {
    }

    //Getter
    public String getTypeOfTransition() {
        return typeOfTransition;
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
    public void setTypeOfTransition(String typeOfTransition) {
        this.typeOfTransition = typeOfTransition;
    }

    public void setConnectedCells(ArrayList<Cell> connectedCells) {
        this.connectedCells = connectedCells;
    }
}
