package de.fhe.fhemobile.vos.timetable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 12.03.15.
 */
public class FlatTerms implements Parcelable {

    public FlatTerms() {
    }

    protected FlatTerms(Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
    }

    public static final Creator<FlatTerms> CREATOR = new Creator<FlatTerms>() {
        @Override
        public FlatTerms createFromParcel(Parcel in) {
            return new FlatTerms(in);
        }

        @Override
        public FlatTerms[] newArray(int size) {
            return new FlatTerms[size];
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
    private String                  mId;

    @SerializedName("title")
    private String                  mTitle;


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
