package de.fhe.fhemobile.vos;

import com.google.gson.annotations.SerializedName;

public class ApiErrorResponse {

// --Commented out by Inspection START (29.03.2023 02:13):
//    public String getMessage() {
//        return message;
//    }
// --Commented out by Inspection STOP (29.03.2023 02:13)

    public int getId(){
        return id;
    }

// --Commented out by Inspection START (29.03.2023 02:14):
//    @SerializedName("message")
//    private String message;
// --Commented out by Inspection STOP (29.03.2023 02:14)

    @SerializedName("id")
    private int id;
}
