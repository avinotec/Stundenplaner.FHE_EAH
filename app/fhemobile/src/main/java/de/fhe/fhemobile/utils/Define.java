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

import org.jetbrains.annotations.NonNls;

/**
 * we define all static defines centrally here.
 * constants etc.
 */
public class Define {

	@NonNls
	public static final String SHARED_PREFERENCES_KEY_LESSON_LIST = "list";

	//created by Nadja 17.11.2021
	public static class Maps{
		public static final String BUILDING_03_02_01_FLOOR_UG1 = "building_03_02_01_floor_ug1";   //$NON-NLS
		public static final String BUILDING_03_02_01_FLOOR_00 = "building_03_02_01_floor_00";   //$NON-NLS
		public static final String BUILDING_03_02_01_FLOOR_01 = "building_03_02_01_floor_01";   //$NON-NLS
		public static final String BUILDING_03_02_01_FLOOR_02 = "building_03_02_01_floor_02";   //$NON-NLS
		public static final String BUILDING_03_02_01_FLOOR_03 = "building_03_02_01_floor_03";   //$NON-NLS
		public static final String BUILDING_03_02_01_FLOOR_04 = "building_03_02_01_floor_04";   //$NON-NLS
		public static final String BUILDING_03_02_01_GROUND = "building_03_02_01_ground";   	//$NON-NLS
		public static final String BUILDING_04_FLOOR_UG1 = "building_04_floor_ug1";   //$NON-NLS
		public static final String BUILDING_04_FLOOR_00 = "building_04_floor_00";   //$NON-NLS
		public static final String BUILDING_04_FLOOR_01 = "building_04_floor_01";   //$NON-NLS
		public static final String BUILDING_04_FLOOR_02 = "building_04_floor_02";   //$NON-NLS
		public static final String BUILDING_04_FLOOR_03 = "building_04_floor_03";   //$NON-NLS
		public static final String BUILDING_04_GROUND = "building_04_ground";   	//$NON-NLS
		public static final String BUILDING_05_FLOOR_UG1 = "building_05_floor_ug1";   //$NON-NLS
		public static final String BUILDING_05_FLOOR_UG2 = "building_05_floor_ug2";   //$NON-NLS
		public static final String BUILDING_05_FLOOR_00 = "building_05_floor_00";   //$NON-NLS
		public static final String BUILDING_05_FLOOR_01 = "building_05_floor_01";   //$NON-NLS
		public static final String BUILDING_05_FLOOR_02 = "building_05_floor_02";   //$NON-NLS
		public static final String BUILDING_05_FLOOR_03 = "building_05_floor_03";   //$NON-NLS
		public static final String BUILDING_05_FLOOR_3Z = "building_05_floor_3Z";   //$NON-NLS
		public static final String BUILDING_05_GROUND = "building_05_ground";   //$NON-NLS
	}


	public static class Navigation {

		public static final String FLOORCONNECTION_TYPE_STAIR = "staircase";    //$NON-NLS
		public static final String FLOORCONNECTION_TYPE_ELEVATOR = "elevator";  //$NON-NLS
		public static final String FLOORCONNECTION_TYPE_BRIDGE = "bridge"; //$NON-NLS

		//Size of the grid overlying the floorplan (unit: cells - needs to be integer)
		//Note: cell numbering at gridded PNGs (docs folder) starts at 0 -> width/height = number + 1
		public static final double cellgrid_width = 45;
		public static final double cellgrid_height = 30;

		//für A-Star Algorithmus
		public static final int COSTS_CELL = 1;
		public static final int COSTS_ROOM = 3;
		public static final int COSTS_EXIT = 1;
		// floorconnections already count twice
		// because the cell at the entered floor and the one at the reached floor are both counted
		public static final int COSTS_FLOORCONNECTION = 1;

	}

/** Push notifications from Google Firebase to the App */
	@NonNls
	public final static String PUSH_NOTIFICATION_CHANNEL_ID = "de.fhe.fhemobile.push";
	@NonNls
	public static final String PUSH_NOTIFICATION_CHANNEL_NAME = "Notification";
	@NonNls
	public static final String PUSH_NOTIFICATION_STUNDENPLANAENDERUNG_TITLE_NOTIFICATION = "Stundenplanänderung";
	public static final long[] PUSH_NOTIFICATION_VIBRATION_PATTERN = {1000, 500, 1000, 0};


	/**
	 * milliseconds within a double-click has to be executed to leave application
	 */
	public static final long APP_CLOSING_DOUBLECLICK_DELAY_TIME = 2000L;


}
