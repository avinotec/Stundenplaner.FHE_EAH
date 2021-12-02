package de.fhe.fhemobile.vos.navigation;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * VO = value of object
 * needed for data transfer between NavigationDialogFragment and NavigationActivity
 * Implementing Parcelable makes it possible to send via intent
 *
 * Created by Nadja - 02.12.2021
 */
public class NavigationDialogVo implements Parcelable {


    public NavigationDialogVo(){

    }

    public void setDestinationLocation(String destLoc) {
        this.mDestLocation = destLoc;
    }

    public void setStartLocation(String startLoc) {
        this.mStartLocation = startLoc;
    }

    public String getDestinationLocation() {
        return mDestLocation;
    }

    public String getStartLocation() {
        return mStartLocation;
    }

    //---------------------------------------------------------
    // Parcel Stuff
    //---------------------------------------------------------

    public NavigationDialogVo(Parcel _In){
        mStartLocation = _In.readString();
        mDestLocation = _In.readString();
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mStartLocation);
        dest.writeString(mDestLocation);
    }

    public static final Parcelable.Creator<NavigationDialogVo> CREATOR
            = new Parcelable.Creator<NavigationDialogVo>() {
        public NavigationDialogVo createFromParcel(Parcel in) {
            return new NavigationDialogVo(in);
        }

        public NavigationDialogVo[] newArray(int size) {
            return new NavigationDialogVo[size];
        }
    };


    //---------------------------------------------------------
    // Member
    //---------------------------------------------------------

    @SerializedName("startLocation")
    private String mStartLocation;

    @SerializedName("destLocation")
    private String mDestLocation;
}
