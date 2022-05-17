package de.fhe.fhemobile.vos.timetable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Nadja - 04/2022
 */
public class TimeTableLocationVo implements Parcelable {

    public TimeTableLocationVo(){ }

    private TimeTableLocationVo(final Parcel in){
        mId = in.readString();
        mName = in.readString();
    }

    public String getName() {
        return mName;
    }

    // PARCELABLE ---------------------------------------------------------------------------------

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(mId);
        dest.writeString(mName);
    }

    public static final Parcelable.Creator<TimeTableLocationVo> CREATOR = new Parcelable.Creator<TimeTableLocationVo>(){
        @Override
        public TimeTableLocationVo createFromParcel(Parcel parcel) {
            return new TimeTableLocationVo(parcel);
        }

        @Override
        public TimeTableLocationVo[] newArray(int i) {
            return new TimeTableLocationVo[i];
        }
    };

    // End PARCELABLE ---------------------------------------------------------------------------------

    @SerializedName("locationId")
    private String mId;

    @SerializedName("locationName")
    private String mName;
}
