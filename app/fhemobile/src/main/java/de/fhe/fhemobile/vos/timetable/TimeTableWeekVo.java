package de.fhe.fhemobile.vos.timetable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by paul on 16.03.15.
 */
public class TimeTableWeekVo implements Parcelable {

    public TimeTableWeekVo() {
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

    public ArrayList<TimeTableDayVo> getDays() {
        return mDays;
    }

    public void setDays(ArrayList<TimeTableDayVo> _days) {
        mDays = _days;
    }

    @SerializedName("weekInYear")
    private int mWeekInYear;

    @SerializedName("year")
    private int mYear;

    @SerializedName("weekdays")
    private ArrayList<TimeTableDayVo> mDays = new ArrayList<>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mWeekInYear);
        dest.writeInt(mYear);
        dest.writeTypedList(mDays);
    }

    private TimeTableWeekVo(Parcel in) {
        mWeekInYear = in.readInt();
        mYear = in.readInt();
        in.readTypedList(mDays, TimeTableDayVo.CREATOR);
    }

    public static final Parcelable.Creator<TimeTableWeekVo> CREATOR = new Parcelable.Creator<TimeTableWeekVo>() {
        public TimeTableWeekVo createFromParcel(Parcel source) {
            return new TimeTableWeekVo(source);
        }

        public TimeTableWeekVo[] newArray(int size) {
            return new TimeTableWeekVo[size];
        }
    };
}
