/*
 *  Copyright (c) 2020-2021 Ernst-Abbe-Hochschule Jena
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package de.fhe.fhemobile.utils.navigation;

import java.util.ArrayList;

public class FloorPlanIterator {

    private int currentPosition = Integer.MAX_VALUE;
    //list sorted from destination to start
    private ArrayList<BuildingFloorKey> floorPlanList;

    public FloorPlanIterator(ArrayList<BuildingFloorKey> _floorPlanList){
        this.floorPlanList = _floorPlanList;
    }

    public BuildingFloorKey next(){
        if(currentPosition > floorPlanList.size()){
            //set currentPosition to start
            currentPosition = floorPlanList.size() - 1;
        } else {
            currentPosition--;
        }

        return floorPlanList.get(currentPosition);
    }

    public boolean hasNext(){
        return currentPosition > 0;
    }

    public BuildingFloorKey previous(){
        if(currentPosition > floorPlanList.size()){
            //set currentPosition to destination
            currentPosition = 0;
        } else {
            currentPosition++;
        }
        return floorPlanList.get(currentPosition);
    }

    public boolean hasPrevious(){
        return currentPosition < floorPlanList.size()-1;
    }
}
