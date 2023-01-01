package de.fhe.fhemobile.vos;

import com.google.gson.annotations.SerializedName;

public class ApiErrorResponse {

//TODO message not used?

    /**
     * default constructor
     */
    public ApiErrorResponse() {
//        message = "uninitialised";
        id = -9999;
    }

//    public String getMessage() {
//        return message;
//    }

    public int getId(){
        return id;
    }

//    @SerializedName("message")
//    private String message;

    @SerializedName("id")
    private int id;
}
