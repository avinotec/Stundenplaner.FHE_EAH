package de.fhe.fhemobile.models.navigation;

import static de.fhe.fhemobile.utils.Define.Navigation.COSTS_EXIT;

import java.util.ArrayList;

import de.fhe.fhemobile.utils.navigation.NavigationUtils.Complex;

/*
    class created by Nadja, 23.11.2021
 */

/**
 * Class that models an exit of a building
 */
public class Exit extends Cell{

    // this exit should be use when changing to the complex "exitTo"
    private final ArrayList<Complex> exitTo = new ArrayList<Complex>();
    // this entry should be use when changing from complex "entryFrom"
    private final ArrayList<Complex> entryFrom = new ArrayList<Complex>();

    /**
     *
     * @param xCoordinate
     * @param yCoordinate
     * @param building
     * @param floor
     * @param exitTo list containing the buildings this exit should be used when changing to
     * @param entryFrom the building of the exit should be entered via this exit when coming from the buildings of the entryFrom list
     */
    public Exit(final int xCoordinate, final int yCoordinate, final String building, final String floor,
                final ArrayList<String> exitTo, final ArrayList<String> entryFrom) {
        super(xCoordinate, yCoordinate, building, floor, true, COSTS_EXIT);

        for(String destBuilding: exitTo){
            this.exitTo.add(new Building(destBuilding).getComplex());
        }

        for(String startBuilding: entryFrom){
            this.entryFrom.add(new Building(startBuilding).getComplex());
        }
    }

    public ArrayList<Complex> getExitTo(){ return exitTo;  }

    public ArrayList<Complex> getEntryFrom(){ return entryFrom; }
}
