package de.fhe.fhemobile.vos.timetable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by paul on 16.03.15.
 */
public class TimeTableDayVo implements Parcelable {

    public TimeTableDayVo() {
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

    public ArrayList<TimeTableEventVo> getEvents() {
        return mEvents;
    }

    public void setEvents(ArrayList<TimeTableEventVo> _events) {
        mEvents = _events;
    }

    @SerializedName("dayInWeek")
    private int mDayInWeek;

    @SerializedName("name")
    private String mName;

    @SerializedName("events")
    private ArrayList<TimeTableEventVo> mEvents = new ArrayList<>();


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mDayInWeek);
        dest.writeString(mName);
        dest.writeTypedList(mEvents);
    }

    private TimeTableDayVo(Parcel in) {
        mDayInWeek = in.readInt();
        mName = in.readString();
        in.readTypedList(mEvents, TimeTableEventVo.CREATOR);
    }

    public static final Parcelable.Creator<TimeTableDayVo> CREATOR = new Parcelable.Creator<TimeTableDayVo>() {
        public TimeTableDayVo createFromParcel(Parcel source) {
            return new TimeTableDayVo(source);
        }

        public TimeTableDayVo[] newArray(int size) {
            return new TimeTableDayVo[size];
        }
    };
}
