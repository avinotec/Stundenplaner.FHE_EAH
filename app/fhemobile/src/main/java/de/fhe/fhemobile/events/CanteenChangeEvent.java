package de.fhe.fhemobile.events;

import de.fhe.fhemobile.events.SimpleEvent;

public class CanteenChangeEvent extends SimpleEvent {
    public static final String RECEIVED_CANTEEN_MENU = "receivedCanteenMenu";
    public static final String RECEIVED_EMPTY_MENU = "receivedEmptyMenu";
    public static final String RECEIVED_CANTEENS = "receivedCanteens";

    public CanteenChangeEvent(String type) {
        super(type);
    }

    public static String getReceivedCanteenMenuEventWithCanteenId(String canteenId){
        return RECEIVED_CANTEEN_MENU + canteenId;
    }
    public static String getReceivedEmptyMenuEventWithCanteenId(String canteenId){
        return RECEIVED_EMPTY_MENU + canteenId;
    }
}
