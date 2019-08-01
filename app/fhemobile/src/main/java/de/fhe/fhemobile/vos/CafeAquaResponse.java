package de.fhe.fhemobile.vos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 04.03.15.
 */
public class CafeAquaResponse {

    public CafeAquaResponse() {
    }

    public boolean isOpen() {
        return mOpen;
    }

    public void setOpen(boolean _open) {
        mOpen = _open;
    }

    @SerializedName("open")
    private boolean mOpen;
}
