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
     * @param building
     * @param floor
     * @return Name as String
     */
    public static String getfloorName(final String building, final String floor) {

        String floorPlan;

        switch (building + "." + floor) {
            case "01.ug":
            case "02.ug":
            case "03.ug":
                floorPlan = BUILDING_03_02_01_FLOOR_UG;
                break;
            case "01.00":
            case "02.00":
            case "03.00":
                floorPlan = BUILDING_03_02_01_FLOOR_00;
                break;
            case "01.01":
            case "02.01":
            case "03.01":
                floorPlan = BUILDING_03_02_01_FLOOR_01;
                break;
            case "01.02":
            case "02.02":
            case "03.02":
                floorPlan = BUILDING_03_02_01_FLOOR_02;
                break;
            case "01.03":
            case "02.03":
            case "03.03":
                floorPlan = BUILDING_03_02_01_FLOOR_03;
                break;
            case "01.04":
            case "02.04":
            case "03.04":
                floorPlan = BUILDING_03_02_01_FLOOR_04;
                break;
            case "04.ug":
                floorPlan = BUILDING_04_FLOOR_UG;
                break;
            case "04.00":
                floorPlan = BUILDING_04_FLOOR_00;
                break;
            case "04.01":
                floorPlan = BUILDING_04_FLOOR_01;
                break;
            case "04.02":
                floorPlan = BUILDING_04_FLOOR_02;
                break;
            case "04.03":
                floorPlan = BUILDING_04_FLOOR_03;
                break;
            case "05.ug":
                floorPlan = BUILDING_05_FLOOR_UG;
                break;
            case "05.00":
                floorPlan = BUILDING_05_FLOOR_00;
                break;
            case "05.01":
                floorPlan = BUILDING_05_FLOOR_01;
                break;
            case "05.02":
                floorPlan = BUILDING_05_FLOOR_02;
                break;
            case "05.03":
                floorPlan = BUILDING_05_FLOOR_03;
                break;
            default:
                floorPlan = null;
        }
        return floorPlan;
    }

}
