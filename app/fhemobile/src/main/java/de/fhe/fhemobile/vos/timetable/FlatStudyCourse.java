package de.fhe.fhemobile.vos.timetable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 12.03.15.
 */
public class FlatStudyCourse implements Parcelable {

    public FlatStudyCourse() {
    }

    protected FlatStudyCourse(Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
    }

    public static final Creator<FlatStudyCourse> CREATOR = new Creator<FlatStudyCourse>() {
        @Override
        public FlatStudyCourse createFromParcel(Parcel in) {
            return new FlatStudyCourse(in);
        }

        @Override
        public FlatStudyCourse[] newArray(int size) {
            return new FlatStudyCourse[size];
        }
    };

    public String getId() {
        return mId;
    }

    public void setId(String _id) {
        mId = _id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String _title) {
        mTitle = _title;
    }



    @SerializedName("id")
    private String              mId;

    @SerializedName("title")
    private String              mTitle;



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
    }
}
