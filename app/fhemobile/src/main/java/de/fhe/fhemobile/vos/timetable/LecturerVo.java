package de.fhe.fhemobile.vos.timetable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Nadja - 04/2022
 */
public class LecturerVo implements Parcelable {

    public LecturerVo(){}

    private LecturerVo(final Parcel in){
        mId = in.readString();
        mName = in.readString();
    }

    public static final Parcelable.Creator<LecturerVo> CREATOR = new Parcelable.Creator<LecturerVo>(){
        @Override
        public LecturerVo createFromParcel(Parcel parcel) {
            return new LecturerVo(parcel);
        }

        @Override
        public LecturerVo[] newArray(int i) {
            return new LecturerVo[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(mId);
        dest.writeString(mName);
    }

    public String getName() {
        return mName;
    }

    @SerializedName("staffId")
    private String mId;

    @SerializedName("staffName")
    private String mName;
}
