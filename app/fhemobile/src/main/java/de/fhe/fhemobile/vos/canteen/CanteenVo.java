package de.fhe.fhemobile.vos.canteen;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import de.fhe.fhemobile.models.settings.UserDefaults;

/**
 * Value object for a canteen
 */
public final class CanteenVo implements Parcelable {

    public CanteenVo(){
    }

    public String getCanteenId() {
        return mCanteenId;
    }

    /**
     *
     * @return String Canteen name
     */
    public String getCanteenName() {
        return mCanteenName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int i) {
        dest.writeString(mCanteenId);
        dest.writeString(mCanteenName);
    }

    public CanteenVo(final Parcel in){
        mCanteenId = in.readString();
        mCanteenName = in.readString();
    }

    public static final Creator<CanteenVo> CREATOR = new Creator<CanteenVo>() {
        @Override
        public CanteenVo createFromParcel(final Parcel parcel) {
            return new CanteenVo(parcel);
        }

        @Override
        public CanteenVo[] newArray(final int size) {
            return new CanteenVo[size];
        }
    };


    @SerializedName("id")
    private String mCanteenId = UserDefaults.DEFAULT_CANTEEN_ID;

    @SerializedName("name")
    private String mCanteenName = "";

    /*
    attributes delivered but not needed/used:
    @SerializedName("city")
    @SerializedName("urlPath")
    */
}
