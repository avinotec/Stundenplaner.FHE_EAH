/*
 *  Copyright (c) 2014-2019 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
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
        mTitle  = correctUmlauts(_Title);
        mRoom   = _Room;
        mPerson = prettify(correctUmlauts(_Person));
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


    /**
     * Replaces incorrect german umlauts in the given string
     * @param str name as string
     * @return corrected name string
     */
    private String correctUmlauts(String str){
        //method added by Nadja - 05.01.2021

        str = str.replaceAll("Ã„", "Ä");
        str = str.replaceAll("Ãœ", "Ü");
        str = str.replaceAll("Ã–", "Ö");
        str = str.replaceAll("Ã\u009F", "ß");
        str = str.replaceAll("Ã¼", "ü");
        str = str.replaceAll("Ã¶", "ö");
        str = str.replaceAll("Ã¤", "ä");

        return str;
    }

    /**
     * if there are more than one lecturer with this name the department e.g. SciTec is added in brackets
     * this prettifies the name if a whitespace between name and bracket is missing
     * @param person string already corrected for umlauts
     * @return name string
     */
    private String prettify(String person){
        //method added by Nadja - 05.01.2021

        if (person.matches("[a-zA-Z- ,.]+[^ ][(][a-zA-Z-]+[)]")){
            person = person.replaceFirst("[(]", " (");
        }
        return person;
    }


    private final String mTime;
    private final String mTitle;
    private final String mRoom;
    private final String mPerson;
}
