/*
 *  Copyright (c) 2014-2022 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
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
package de.fhe.fhemobile.events;


public class CanteenChangeEvent extends SimpleEvent {

    public static final String RECEIVED_CANTEENS = "receivedCanteens";
    public static final String RECEIVED_CANTEEN_MENU = "receivedCanteenMenu";
    public static final String RECEIVED_All_CANTEEN_MENUS = "receivedAllCanteenMenus";


    public CanteenChangeEvent(final String type) {
        super(type);
    }

    public static String getReceivedCanteenMenuEventWithCanteenId(final String canteenId){
        return RECEIVED_CANTEEN_MENU + canteenId;
    }
}
