package de.fhe.fhemobile.utils.navigation;

import java.util.Arrays;

import de.fhe.fhemobile.models.navigation.Cell;

/**
 * Key for the HashMap containing the route (all cells to walk)
 */
public class BuildingFloorKey implements Comparable {

    private Complex mComplex;
    private int mFloor;

    public BuildingFloorKey(Cell cell) {
        mComplex = cell.getComplex();
        mFloor = cell.getFloorInt();
    }

    public BuildingFloorKey(Complex complex, int floor) {
        mComplex = complex;
        mFloor = floor;
    }

    public Complex getComplex() {
        return mComplex;
    }

    public int getFloorInt() {
        return mFloor;
    }

    public String getFloorString() {
        return NavigationUtils.floorIntToString(mComplex, mFloor);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof BuildingFloorKey)) return false;
        BuildingFloorKey that = (BuildingFloorKey) other;
        return mFloor == that.mFloor && mComplex.equals(that.mComplex);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{mComplex, mFloor});
    }

    /**
     * Returns > 0 if this is bigger than other object o,
     * Returns = 0 if this is equal to other object o,
     * Returns < 0 if this i smaller than other object o
     *
     * @param o other object
     * @return
     */
    @Override
    public int compareTo(Object o) {
        BuildingFloorKey otherKey = (BuildingFloorKey) o;
        //gleiches Gebäude, gleiche Etage
        if (this.equals(otherKey)) return 0;

            //gleiches Gebäude -> Etage vergleichen
        else if (this.getComplex().equals(otherKey.getComplex())) {
            return otherKey.getFloorInt() > this.getFloorInt() ? 1 : -1;
        }

        //verschiedene Gebäude: COMPLEX_4 < COMPLEX_321 < COMPLEX_5 (the order of declaration of enum values in Complex class is used)
        else return this.getComplex().compareTo(otherKey.getComplex());
    }

    /**
     * Returns n if otherKey corresponds to n floors above this key's floor in the same complex,
     * Returns 0 if keys are equal,
     * Return -n if otherKey corresponds to n floors under this key's floor in the same complex
     * Returns x < 0 if otherKey corresponds to another complex, x is an arbitrary score
     *
     * @param otherKey
     * @return int floor differences
     */
    public int getDifference(BuildingFloorKey otherKey) {
        //gleiches Gebäude, gleiche Etage
        if (this.equals(otherKey)) return 0;

            //gleiches Gebäude -> Etage vergleichen
        else if (this.getComplex().equals(otherKey.getComplex())) {

            //floor difference when entrance is at floor -1
            if (this.getComplex().equals(Complex.COMPLEX_4)) {
                return otherKey.getFloorInt() - this.getFloorInt() + 1;
            }
            //floor difference when entrance is at floor 00
            return otherKey.getFloorInt() - this.getFloorInt();
        }

        //verschiedene Gebäude -> Anzahl Etagen hoch/runter in dest complex (otherKey)
        // + 100 (to distinguish between case "same complex" and "different complex",
        // otherwise 03.03 to 03.01 gets same score as 03.01 to 04.01);

        //+2 to distinguish between destination floor -1 and 01 in different building,
        // needed because building 04 is entered at -1 and thus a route can contain both floorplans -1 and 01

        //note: number does not correspond to floor difference between otherKey and this anymore
        else return -(Math.abs(otherKey.mFloor + 2) + 100);
    }
}
