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

package de.fhe.fhemobile.utils;

/**
 * we define all static defines centrally here.
 * constants etc.
 */
public class Define {

	public static class Navigation {
		public static final String BUILDING_03_02_01_FLOOR_UG = "building_03_02_01_floor_ug";   //$NON-NLS
		public static final String BUILDING_03_02_01_FLOOR_00 = "building_03_02_01_floor_00";   //$NON-NLS
		public static final String BUILDING_03_02_01_FLOOR_01 = "building_03_02_01_floor_01";   //$NON-NLS
		public static final String BUILDING_03_02_01_FLOOR_02 = "building_03_02_01_floor_02";   //$NON-NLS
		public static final String BUILDING_03_02_01_FLOOR_03 = "building_03_02_01_floor_03";   //$NON-NLS
		public static final String BUILDING_03_02_01_FLOOR_04 = "building_03_02_01_floor_04";   //$NON-NLS
		public static final String BUILDING_04_FLOOR_UG = "building_04_floor_ug";   //$NON-NLS
		public static final String BUILDING_04_FLOOR_00 = "building_04_floor_00";   //$NON-NLS
		public static final String BUILDING_04_FLOOR_01 = "building_04_floor_01";   //$NON-NLS
		public static final String BUILDING_04_FLOOR_02 = "building_04_floor_02";   //$NON-NLS
		public static final String BUILDING_04_FLOOR_03 = "building_04_floor_03";   //$NON-NLS
		public static final String BUILDING_05_FLOOR_UG = "building_05_floor_ug";   //$NON-NLS
		public static final String BUILDING_05_FLOOR_00 = "building_05_floor_00";   //$NON-NLS
		public static final String BUILDING_05_FLOOR_01 = "building_05_floor_01";   //$NON-NLS
		public static final String BUILDING_05_FLOOR_02 = "building_05_floor_02";   //$NON-NLS
		public static final String BUILDING_05_FLOOR_03 = "building_05_floor_03";   //$NON-NLS

		public static final String BUILDING_01 = "01";  //$NON-NLS
		public static final String BUILDING_02 = "02";  //$NON-NLS
		public static final String BUILDING_03 = "03";  //$NON-NLS
		public static final String BUILDING_04 = "04";  //$NON-NLS
		public static final String BUILDING_05 = "05";  //$NON-NLS

		public static final String FLOORCONNECTION_TYPE_STAIR = "staircase";    //$NON-NLS
		public static final String FLOORCONNECTION_TYPE_ELEVATOR = "elevator";  //$NON-NLS
		//ways outside buildings + bridge
		//todo icons neu machen und Positionierung festlegen
		public static final String FLOORCONNECTION_TYPE_WAY = "way"; //$NON-NLS

		//Size of the grid overlying the floorplan (unit: cells - needs to be integer)
		//Note: cell numbering at gridded PNGs (docs folder) starts at 0 -> width/height = number + 1
		public static final double cellgrid_width = 45;
		public static final double cellgrid_height = 30;

		//für A-Star Algorithmus
		public static final int COSTS_CELL = 1;
		public static final int COSTS_ROOM = 3;
		public static final int COSTS_FLOORCONNECTION = 2;

	}

}
