package de.fhe.fhemobile.vos.timetable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * Created by Nadja - 04/2022
 */
public class TimetableLocationVo implements Parcelable {

    public TimetableLocationVo(){ }

    TimetableLocationVo(final Parcel in){
        mId = in.readString();
        mName = in.readString();
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof TimetableLocationVo)) return false;
        final TimetableLocationVo that = (TimetableLocationVo) o;
        return mId.equals(that.mId) && mName.equals(that.mName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mId, mName);
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

    public static final Parcelable.Creator<TimetableLocationVo> CREATOR = new Parcelable.Creator<TimetableLocationVo>(){
        @Override
        public TimetableLocationVo createFromParcel(final Parcel parcel) {
            return new TimetableLocationVo(parcel);
        }

        @Override
        public TimetableLocationVo[] newArray(final int i) {
            return new TimetableLocationVo[i];
        }
    };

    // End PARCELABLE ---------------------------------------------------------------------------------

    @SerializedName("locationId")
    private String mId;

    @SerializedName("locationName")
    private String mName;
}
