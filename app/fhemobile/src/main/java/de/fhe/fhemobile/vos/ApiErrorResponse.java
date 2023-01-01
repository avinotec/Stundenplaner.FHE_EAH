package de.fhe.fhemobile.vos;

import com.google.gson.annotations.SerializedName;

public class ApiErrorResponse {

    public String getMessage() {
        return message;
    }

    public int getId(){
        return id;
    }

    @SerializedName("message")
    private String message;

    @SerializedName("id")
    private int id;
}
