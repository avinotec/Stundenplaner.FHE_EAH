package de.fhe.fhemobile.vos.timetable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 16.03.15.
 */
public class FlatTimeTableDay implements Parcelable {

    public FlatTimeTableDay() {
    }

    public Integer getDayInWeek() {
        return mDayInWeek;
    }

    public void setDayInWeek(Integer _dayInWeek) {
        mDayInWeek = _dayInWeek;
    }

    public String getName() {
        return mName;
    }

    public void setName(String _name) {
        mName = _name;
    }


    @SerializedName("dayInWeek")
    private int mDayInWeek;

    @SerializedName("name")
    private String mName;



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mDayInWeek);
        dest.writeString(mName);
    }

    private FlatTimeTableDay(Parcel in) {
        mDayInWeek = in.readInt();
        mName = in.readString();
    }

    public static final Creator<FlatTimeTableDay> CREATOR = new Creator<FlatTimeTableDay>() {
        public FlatTimeTableDay createFromParcel(Parcel source) {
            return new FlatTimeTableDay(source);
        }

        public FlatTimeTableDay[] newArray(int size) {
            return new FlatTimeTableDay[size];
        }
    };
}
