package de.fhe.fhemobile.models.navigation;

import de.fhe.fhemobile.utils.navigation.NavigationUtils.Complex;


/**
 * Class for distinguishing between names of buildings (3, 2, 1) and buildings considered as one unit (3-2-1)
 * needed for route calculation
 */
public class Building {

    private Complex mComplex;
    private String mBuilding;

    Building(String building){
        this.mBuilding = building;
        this.mComplex = Complex.getEnum(building);
    }

    /**
     * Get the complex ({@link Complex}) the building belongs to (BUILDING_4, BUILDING_5, BUILDING_321)
     * @return complex
     */
    public Complex getComplex() {
        return mComplex;
    }

    /**
     * Returns the String of the building ("ug", "00", "01" ,...)
     * @return building as String
     */
    public String getBuilding() {
        return mBuilding;
    }
}
