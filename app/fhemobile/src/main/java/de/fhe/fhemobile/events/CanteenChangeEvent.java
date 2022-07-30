package de.fhe.fhemobile.events;

import de.fhe.fhemobile.events.SimpleEvent;

public class CanteenChangeEvent extends SimpleEvent {
    public static final String RECEIVED_CANTEEN_MENU = "receivedCanteenMenu";
    public static final String RECEIVED_EMPTY_MENU = "receivedEmptyMenu";
    public static final String RECEIVED_CANTEENS = "receivedCanteens";

    public CanteenChangeEvent(final String type) {
        super(type);
    }

    public static String getReceivedCanteenMenuEventWithCanteenId(final String canteenId){
        return RECEIVED_CANTEEN_MENU + canteenId;
    }
    public static String getReceivedEmptyMenuEventWithCanteenId(final String canteenId){
        return RECEIVED_EMPTY_MENU + canteenId;
    }
}
