package de.fhe.fhemobile.utils.navigation;

import androidx.annotation.NonNull;

/**
 * Complex for RouteCalculator
 * merges buildings 3, 2 and 1 to COMPLEX_321
 */
public enum Complex {

    //order important, sorted from west to east
    COMPLEX_4,
    COMPLEX_321,
    COMPLEX_5;

    public static Complex getEnum(String building) {
        switch (building) {
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

    @NonNull
    @Override
    public String toString() {
        switch (this) {
            case COMPLEX_321:
                return "03_02_01";
            case COMPLEX_4:
                return "04";
            case COMPLEX_5:
                return "05";
            default:
                return "";
        }
    }

}
