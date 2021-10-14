package de.fhe.fhemobile.utils.navigation;

import static de.fhe.fhemobile.utils.Define.Navigation.*;

public class NavigationUtils {

    /**
     * Complex for RouteCalculator
     * merges buildings 3, 2 and 1 to COMPLEX_321
     */
    public enum Complex {

        //order important, sorted from west to east
        COMPLEX_4,
        COMPLEX_321,
        COMPLEX_5;


        public static Complex getEnum(String building){
            switch(building){
                case "01":
                case "02":
                case "03":
                    return COMPLEX_321;
                case "04":
                    return COMPLEX_4;
                case "05":
                    return COMPLEX_5;
                default:
                    return null;
            }
        }


    }

    /**
     * Get name of file for the floor plan - corresponds to file name without ending (.json / .jpeg /.png)
     * @param complex
     * @param floor
     * @return Name as String
     */
    public static String getFloorPlanName(final Complex complex, final String floor) {
        String floorPlan = "";

        if(complex.equals(Complex.COMPLEX_321)){
            switch (floor) {
                case "ug":
                    floorPlan = BUILDING_03_02_01_FLOOR_UG;
                    break;
                case "00":
                    floorPlan = BUILDING_03_02_01_FLOOR_00;
                    break;
                case "01":
                    floorPlan = BUILDING_03_02_01_FLOOR_01;
                    break;
                case "02":
                    floorPlan = BUILDING_03_02_01_FLOOR_02;
                    break;
                case "03":
                    floorPlan = BUILDING_03_02_01_FLOOR_03;
                    break;
                case "04":
                    floorPlan = BUILDING_03_02_01_FLOOR_04;
                    break;
            }
        }
        else if(complex.equals(Complex.COMPLEX_4)){
            switch(floor){
                case "ug":
                    floorPlan = BUILDING_04_FLOOR_UG;
                    break;
                case "00":
                    floorPlan = BUILDING_04_FLOOR_00;
                    break;
                case "01":
                    floorPlan = BUILDING_04_FLOOR_01;
                    break;
                case "02":
                    floorPlan = BUILDING_04_FLOOR_02;
                    break;
                case "03":
                    floorPlan = BUILDING_04_FLOOR_03;
                    break;
            }
        } else if(complex.equals(Complex.COMPLEX_5)){
            switch (floor){
                case "ug":
                    floorPlan = BUILDING_05_FLOOR_UG;
                    break;
                case "00":
                    floorPlan = BUILDING_05_FLOOR_00;
                    break;
                case "01":
                    floorPlan = BUILDING_05_FLOOR_01;
                    break;
                case "02":
                    floorPlan = BUILDING_05_FLOOR_02;
                    break;
                case "03":
                    floorPlan = BUILDING_05_FLOOR_03;
                    break;
            }
        }
        return floorPlan;
    }

    /**
     * Get the string representation of the floor
     * @param index
     * @return floor name as string
     */
    public static String floorIntToString(final int index) {

        String currentFloor = "";

        switch (index) {
            case -1:
                currentFloor = "ug";
                break;
            case 0:
                currentFloor = "00";
                break;
            case 1:
                currentFloor = "01";
                break;
            case 2:
                currentFloor = "02";
                break;
            case 3:
                currentFloor = "03";
                break;
            case 4:
                currentFloor = "04";
                break;
            default:
                break;
        }
        return currentFloor;
    }

}
