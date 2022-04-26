package de.fhe.fhemobile.vos.canteen;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Value object for a canteen
 */
public class CanteenVo implements Parcelable {

    final private String canteenId;
    final private String canteenName;

    public CanteenVo(String id, String name){
        canteenId = id;
        canteenName = name;
    }

    public String getCanteenId() {
        return canteenId;
    }

    public String getName() {
        return canteenName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(canteenId);
        dest.writeString(canteenName);
    }

    public CanteenVo(Parcel in){
        canteenId = in.readString();
        canteenName = in.readString();
    }

    public static final Creator<CanteenVo> CREATOR = new Creator<CanteenVo>() {
        @Override
        public CanteenVo createFromParcel(Parcel parcel) {
            return new CanteenVo(parcel);
        }

        @Override
        public CanteenVo[] newArray(int i) {
            return new CanteenVo[i];
        }
    };


    @SerializedName("id")
    private Integer mId;

    @SerializedName("name")
    private String mName;
}
