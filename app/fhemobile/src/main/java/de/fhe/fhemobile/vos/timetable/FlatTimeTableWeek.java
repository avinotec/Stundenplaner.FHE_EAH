package de.fhe.fhemobile.vos.timetable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 16.03.15.
 */
public class FlatTimeTableWeek implements Parcelable {

    public FlatTimeTableWeek() {
    }

    public int getWeekInYear() {
        return mWeekInYear;
    }

    public void setWeekInYear(int _weekInYear) {
        mWeekInYear = _weekInYear;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(int _year) {
        mYear = _year;
    }


    @SerializedName("weekInYear")
    private int mWeekInYear;

    @SerializedName("year")
    private int mYear;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mWeekInYear);
        dest.writeInt(mYear);
    }

    private FlatTimeTableWeek(Parcel in) {
        mWeekInYear = in.readInt();
        mYear = in.readInt();
    }

    public static final Creator<FlatTimeTableWeek> CREATOR = new Creator<FlatTimeTableWeek>() {
        public FlatTimeTableWeek createFromParcel(Parcel source) {
            return new FlatTimeTableWeek(source);
        }

        public FlatTimeTableWeek[] newArray(int size) {
            return new FlatTimeTableWeek[size];
        }
    };
}
