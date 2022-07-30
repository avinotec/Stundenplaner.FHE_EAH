package de.fhe.fhemobile.vos.timetable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * Created by Nadja - 04/2022
 */
public class LecturerVo implements Parcelable {

    public LecturerVo(){}

    LecturerVo(final Parcel in){
        mId = in.readString();
        mName = in.readString();
    }

    public String getName() {
        return mName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof LecturerVo)) return false;
        final LecturerVo that = (LecturerVo) o;
        return mId.equals(that.mId) && mName.equals(that.mName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mId, mName);
    }

    // PARCELABLE --------------------------------------------------------------------------------

    public static final Parcelable.Creator<LecturerVo> CREATOR = new Parcelable.Creator<LecturerVo>(){
        @Override
        public LecturerVo createFromParcel(final Parcel parcel) {
            return new LecturerVo(parcel);
        }

        @Override
        public LecturerVo[] newArray(final int i) {
            return new LecturerVo[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int i) {
        dest.writeString(mId);
        dest.writeString(mName);
    }

    // End PARCELABLE --------------------------------------------------------------------------------

    @SerializedName("staffId")
    private String mId;

    @SerializedName("staffName")
    private String mName;
}
