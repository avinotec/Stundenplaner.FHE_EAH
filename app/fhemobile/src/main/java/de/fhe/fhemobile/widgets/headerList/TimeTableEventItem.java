package de.fhe.fhemobile.widgets.headerList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.fhe.fhemobile.R;

/**
 * Created by paul on 03.03.15.
 */
public class TimeTableEventItem implements IBaseItem {

    public TimeTableEventItem(String _Time, String _Title, String _Room, String _Person) {
        mTime   = _Time;
        mTitle  = _Title;
        mRoom   = _Room;
        mPerson = _Person;
    }

    @Override
    public int getViewType() {
        return EItemType.EVENT.ordinal();
    }

    @Override
    public View getView(LayoutInflater _inflater, View _convertView, ViewGroup _parent) {
        
        ViewHolder holder;
        if (_convertView == null) {
            holder = new ViewHolder();
            _convertView = _inflater.inflate(R.layout.item_timetable_event, _parent, false);
            
            holder.mTime    = (TextView) _convertView.findViewById(R.id.itemEventTime);
            holder.mTitle   = (TextView) _convertView.findViewById(R.id.itemEventTitle);
            holder.mRoom    = (TextView) _convertView.findViewById(R.id.itemEventRoom);
            holder.mPerson  = (TextView) _convertView.findViewById(R.id.itemEventPerson);

            _convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) _convertView.getTag();
        }
        
        holder.mTime.setText(mTime);
        holder.mTitle.setText(mTitle);
        holder.mRoom.setText(mRoom);
        holder.mPerson.setText(mPerson);

        return _convertView;
    }

    static class ViewHolder {
        TextView mTime;
        TextView mTitle;
        TextView mRoom;
        TextView mPerson;
    }
    
    private String mTime;
    private String mTitle;
    private String mRoom;
    private String mPerson;
}
