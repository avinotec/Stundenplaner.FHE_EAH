package de.fhe.fhemobile.utils.navigation;

import de.fhe.fhemobile.utils.Define;

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
     * Get path to floorplan png
     * @param complex
     * @param floor
     * @return filepath to png
     */
    public static String getPathToFloorPlanPNG(final Complex complex, final String floor) {
        String filename = "";
        String folder = "drawable/";

        if(complex.equals(Complex.COMPLEX_321)){
            switch (floor) {
                case "ug":
                    filename = Define.Maps.BUILDING_03_02_01_FLOOR_UG;
                    break;
                case "00":
                    filename = Define.Maps.BUILDING_03_02_01_FLOOR_00;
                    break;
                case "01":
                    filename = Define.Maps.BUILDING_03_02_01_FLOOR_01;
                    break;
                case "02":
                    filename = Define.Maps.BUILDING_03_02_01_FLOOR_02;
                    break;
                case "03":
                    filename = Define.Maps.BUILDING_03_02_01_FLOOR_03;
                    break;
                case "04":
                    filename = Define.Maps.BUILDING_03_02_01_FLOOR_04;
                    break;
            }
        }
        else if(complex.equals(Complex.COMPLEX_4)){
            switch(floor){
                case "ug":
                    filename = Define.Maps.BUILDING_04_FLOOR_UG;

                    break;
                case "00":
                    filename = Define.Maps.BUILDING_04_FLOOR_00;
                    break;
                case "01":
                    filename = Define.Maps.BUILDING_04_FLOOR_01;
                    break;
                case "02":
                    filename = Define.Maps.BUILDING_04_FLOOR_02;
                    break;
                case "03":
                    filename = Define.Maps.BUILDING_04_FLOOR_03;
                    break;
            }
        } else if(complex.equals(Complex.COMPLEX_5)){
            switch (floor){
                case "ug":
                    filename = Define.Maps.BUILDING_05_FLOOR_UG;
                    break;
                case "00":
                    filename = Define.Maps.BUILDING_05_FLOOR_00;
                    break;
                case "01":
                    filename = Define.Maps.BUILDING_05_FLOOR_01;
                    break;
                case "02":
                    filename = Define.Maps.BUILDING_05_FLOOR_02;
                    break;
                case "03":
                    filename = Define.Maps.BUILDING_05_FLOOR_03_1;
                    //todo: somehow check if level 1 or level 2 is needed
                    break;
            }
        }
        return folder + filename;
    }

    /**
     * Get path to floorplan grid as json
     * @param complex
     * @param floor
     * @return filepath to json
     */
    public static String getNameOfFloorPlanGrid(final Complex complex, final String floor) {
        String filename = "";
        String ending = ".json";

        if(complex.equals(Complex.COMPLEX_321)){
            switch (floor) {
                case "ug":
                    filename = Define.Maps.BUILDING_03_02_01_FLOOR_UG;
                    break;
                case "00":
                    filename = Define.Maps.BUILDING_03_02_01_FLOOR_00;
                    break;
                case "01":
                    filename = Define.Maps.BUILDING_03_02_01_FLOOR_01;
                    break;
                case "02":
                    filename = Define.Maps.BUILDING_03_02_01_FLOOR_02;
                    break;
                case "03":
                    filename = Define.Maps.BUILDING_03_02_01_FLOOR_03;
                    break;
                case "04":
                    filename = Define.Maps.BUILDING_03_02_01_FLOOR_04;
                    break;
            }
        }
        else if(complex.equals(Complex.COMPLEX_4)){
            switch(floor){
                case "ug":
                    filename = Define.Maps.BUILDING_04_FLOOR_UG;

                    break;
                case "00":
                    filename = Define.Maps.BUILDING_04_FLOOR_00;
                    break;
                case "01":
                    filename = Define.Maps.BUILDING_04_FLOOR_01;
                    break;
                case "02":
                    filename = Define.Maps.BUILDING_04_FLOOR_02;
                    break;
                case "03":
                    filename = Define.Maps.BUILDING_04_FLOOR_03;
                    break;
            }
        } else if(complex.equals(Complex.COMPLEX_5)){
            switch (floor){
                case "ug":
                    filename = Define.Maps.BUILDING_05_FLOOR_UG;
                    break;
                case "00":
                    filename = Define.Maps.BUILDING_05_FLOOR_00;
                    break;
                case "01":
                    filename = Define.Maps.BUILDING_05_FLOOR_01;
                    break;
                case "02":
                    filename = Define.Maps.BUILDING_05_FLOOR_02;
                    break;
                case "03":
                    filename = Define.Maps.BUILDING_05_FLOOR_03_1;
                    break;
            }
        }
        return filename + ending;
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
                currentFloor = "-1";
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
