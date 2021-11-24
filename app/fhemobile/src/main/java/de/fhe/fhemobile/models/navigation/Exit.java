package de.fhe.fhemobile.models.navigation;

import java.util.ArrayList;

import de.fhe.fhemobile.utils.navigation.NavigationUtils.Complex;

/**
 * Class that models an exit of a building
 */
public class Exit extends Cell{

    // this exit should be use when changing to the complex "exitTo"
    private ArrayList<Complex> exitTo = new ArrayList<Complex>();


    public Exit(final int xCoordinate, final int yCoordinate, final String building, final String floor, final ArrayList<String> exitTo) {
        super(xCoordinate, yCoordinate, building, floor, true);

        for(String destBuilding: exitTo){
            this.exitTo.add(new Building(destBuilding).getComplex());
        }
    }

    public ArrayList<Complex> getExitTo(){ return exitTo;  }
}
